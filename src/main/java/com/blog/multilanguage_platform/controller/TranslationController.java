package com.blog.multilanguage_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.multilanguage_platform.services.TranslationService;
import java.util.Map;

@RestController
@RequestMapping("/api/translate")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TranslationController {
    
    @Autowired
    private TranslationService translationService;
    
    /**
     * Translate text to a target language
     * @param text The text to translate
     * @param lang Target language (e.g., 'en', 'hi', 'es', 'fr')
     * @return Translated text
     */
    @GetMapping("/text")
    public ResponseEntity<Map<String, String>> translateText(
            @RequestParam(name = "q") String text,
            @RequestParam(name = "lang") String lang) {
        
        String translated = translationService.translateText(text, lang);
        
        return ResponseEntity.ok(Map.of(
            "original", text,
            "translated", translated,
            "language", lang
        ));
    }
    
    /**
     * Debug endpoint to inspect translation API behavior
     * @param text The text to translate
     * @param lang Target language
     * @return Detailed debug information
     */
    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> translateDebug(
            @RequestParam(name = "q") String text,
            @RequestParam(name = "lang") String lang) {
        
        Map<String, Object> debug = translationService.translateDebug(text, lang);
        return ResponseEntity.ok(debug);
    }
}
