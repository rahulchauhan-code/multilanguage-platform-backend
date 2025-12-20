package com.blog.multilanguage_platform.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class TranslationService {

    private final String API_URL = "https://libretranslate.de/translate";

    public String translateText(String text, String targetLanguage) {
        if (text == null || text.isEmpty()) return text;

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> request = new HashMap<>();
            request.put("q", text);
            request.put("source", "auto"); // Automatically detect source language
            request.put("target", targetLanguage);
            request.put("format", "text");

            Map<String, Object> response = restTemplate.postForObject(API_URL, request, Map.class);
            return (String) response.get("translatedText"); // Return translated string
        } catch (Exception e) {
            // Fallback to original text if translation fails
            return text;
        }
    }
}
