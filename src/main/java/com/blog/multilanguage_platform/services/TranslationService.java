package com.blog.multilanguage_platform.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final RestTemplate restTemplate;

    public TranslationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Configurable primary API
    @Value("${translation.api.url:https://translate.argosopentech.com/translate}")
    private String apiUrl;

    @Value("${translation.api.key:}")
    private String apiKey; // optional

    public String translateText(String text, String targetLanguage) {
        if (text == null || text.isEmpty()) return text;

        String normalizedTarget = normalizeTargetLanguage(targetLanguage);
        if (normalizedTarget == null || normalizedTarget.isEmpty()) {
            logger.warn("translateText: provided targetLanguage '{}' normalized to empty - skipping translation", targetLanguage);
            return text;
        }

        try {
            // Try Google fallback first (more reliable public endpoint) then primary API
            String google = callGoogleFallback(text, normalizedTarget);
            if (google != null && !google.isEmpty() && !google.equalsIgnoreCase(text)) {
                return google;
            }

            Map<String, Object> debug = callPrimaryApi(text, normalizedTarget);
            String translated = extractTranslatedText(debug);
            if (translated != null && !translated.isEmpty() && !translated.equalsIgnoreCase(text)) {
                return translated;
            }

            // Nothing worked — return original
            return text;
        } catch (Exception e) {
            logger.error("translateText unexpected failure for '{}' -> '{}': {}", text, targetLanguage, e.getMessage(), e);
            return text;
        }
    }

    // Primary API call returns a map with status/body for parsing
    private Map<String, Object> callPrimaryApi(String text, String normalizedTarget) {
        Map<String, Object> debug = new HashMap<>();
        debug.put("input", text);
        debug.put("target", normalizedTarget);
        debug.put("apiUrl", apiUrl);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
            requestMap.add("q", text);
            requestMap.add("source", "auto");
            requestMap.add("target", normalizedTarget);
            requestMap.add("format", "text");
            if (apiKey != null && !apiKey.isEmpty()) {
                requestMap.add("api_key", apiKey);
            }

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestMap, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
            debug.put("statusCode", response.getStatusCode().value());
            debug.put("body", response.getBody());
            return debug;
        } catch (Exception e) {
            debug.put("error", e.getMessage());
            logger.warn("Primary translation API call failed: {}", e.getMessage());
            return debug;
        }
    }

    // Extract a translated string from a variety of possible response shapes
    private String extractTranslatedText(Map<String, Object> apiResponse) {
        if (apiResponse == null) return null;
        Object body = apiResponse.get("body");
        String result = null;
        
        if (body instanceof Map) {
            Map<?, ?> m = (Map<?, ?>) body;
            // common keys
            String[] keys = new String[] {"translatedText", "translation", "translated_text", "result", "translated"};
            for (String k : keys) {
                if (m.containsKey(k)) {
                    Object v = m.get(k);
                    if (v != null) {
                        result = v.toString();
                        break;
                    }
                }
            }
            // Some instances return { "data": { "translations": [ { "translatedText": "..." } ] } }
            if (result == null && m.containsKey("data")) {
                Object data = m.get("data");
                if (data instanceof Map) {
                    Object translations = ((Map) data).get("translations");
                    if (translations instanceof List) {
                        List list = (List) translations;
                        if (!list.isEmpty() && list.get(0) instanceof Map) {
                            Object tt = ((Map) list.get(0)).get("translatedText");
                            if (tt != null) result = tt.toString();
                        }
                    }
                }
            }
        }
        // If body is a plain string
        if (result == null && body instanceof String) {
            result = (String) body;
        }
        // If apiResponse has an explicit translatedText key
        if (result == null) {
            Object direct = apiResponse.get("translatedText");
            if (direct != null) result = direct.toString();
        }
        
        // URL decode the result to remove %20, %0A, etc.
        if (result != null && !result.isEmpty()) {
            try {
                result = URLDecoder.decode(result, StandardCharsets.UTF_8.name());
                logger.debug("URL decoded translation result");
            } catch (Exception e) {
                logger.warn("Failed to URL decode translation: {}", e.getMessage());
            }
        }
        
        return result;
    }

    // Unofficial Google translate fallback (no API key required) — uses public endpoint
    private String callGoogleFallback(String text, String target) {
        try {
            String encoded = UriUtils.encode(text, StandardCharsets.UTF_8);
            String url = String.format("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=%s&dt=t&q=%s", target, encoded);
            Object resp = restTemplate.getForObject(url, Object.class);
            String result = null;
            
            if (resp instanceof List) {
                // The response is nested lists — extract first translated segment
                List outer = (List) resp;
                if (!outer.isEmpty()) {
                    Object first = outer.get(0);
                    if (first instanceof List) {
                        List segs = (List) first;
                        StringBuilder sb = new StringBuilder();
                        for (Object s : segs) {
                            if (s instanceof List && !((List) s).isEmpty()) {
                                Object translatedPiece = ((List) s).get(0);
                                if (translatedPiece != null) sb.append(translatedPiece.toString());
                            }
                        }
                        result = sb.toString();
                    }
                }
            } else if (resp != null) {
                result = resp.toString();
            }
            
            // URL decode to remove %20, %0A, etc.
            if (result != null && !result.isEmpty()) {
                try {
                    result = URLDecoder.decode(result, StandardCharsets.UTF_8.name());
                    logger.debug("URL decoded Google fallback result");
                } catch (Exception e) {
                    logger.warn("Failed to URL decode Google result: {}", e.getMessage());
                }
            }
            
            return result;
        } catch (Exception e) {
            logger.warn("Google fallback failed: {}", e.getMessage());
            return null;
        }
    }

    // Debug method retained for explicit testing
    public Map<String, Object> translateDebug(String text, String targetLanguage) {
        Map<String, Object> debug = new HashMap<>();
        debug.put("input", text);
        debug.put("requestedTarget", targetLanguage);
        debug.put("apiUrl", apiUrl);
        debug.put("apiKeyPresent", apiKey != null && !apiKey.isEmpty());

        String normalizedTarget = normalizeTargetLanguage(targetLanguage);
        debug.put("normalizedTarget", normalizedTarget);

        if (text == null || text.isEmpty()) {
            debug.put("error", "empty input");
            return debug;
        }

        if (normalizedTarget == null || normalizedTarget.isEmpty()) {
            debug.put("error", "invalid target language");
            return debug;
        }

        Map<String, Object> primary = callPrimaryApi(text, normalizedTarget);
        debug.put("primary", primary);
        String extracted = extractTranslatedText(primary);
        debug.put("extracted", extracted);

        String google = callGoogleFallback(text, normalizedTarget);
        debug.put("googleFallback", google);

        return debug;
    }

    // Normalize incoming 'lang' parameter to an ISO 639-1 code supported by LibreTranslate
    private String normalizeTargetLanguage(String lang) {
        if (lang == null) return null;
        String l = lang.trim().toLowerCase(Locale.ROOT);
        if (l.isEmpty()) return null;

        // If passed like "en-US" or "en_US", take the first segment
        if (l.contains("-") || l.contains("_")) {
            l = l.split("[-_]")[0];
        }

        // If already two-letter code, return it
        if (l.length() == 2) return l;

        // Map common language names to codes
        Map<String, String> nameToCode = new HashMap<>();
        nameToCode.put("english", "en");
        nameToCode.put("spanish", "es");
        nameToCode.put("french", "fr");
        nameToCode.put("german", "de");
        nameToCode.put("portuguese", "pt");
        nameToCode.put("russian", "ru");
        nameToCode.put("arabic", "ar");
        nameToCode.put("chinese", "zh");
        nameToCode.put("japanese", "ja");
        nameToCode.put("hindi", "hi");
        nameToCode.put("italian", "it");

        if (nameToCode.containsKey(l)) return nameToCode.get(l);

        // As a last resort, if string starts with known ISO prefix, take first two chars
        if (l.length() > 2) return l.substring(0, 2);

        return l;
    }
}
