package com.blog.multilanguage_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.multilanguage_platform.dto.Post_content;
import com.blog.multilanguage_platform.services.Post_contentServices;
import com.blog.multilanguage_platform.services.TranslationService;

@RestController
@RequestMapping("/api/post_contents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class Post_contentController {
	@Autowired
	private Post_contentServices psServ;
	@Autowired
	private TranslationService translationService;
	
	@GetMapping("/")
    public ResponseEntity<List<Post_content>> getAllPostContents(
    		@RequestParam(required = false) String lang) {
        
        List<Post_content> contents = this.psServ.getAllPostContent();
        
        // Apply translation if lang parameter is provided
        if (lang != null && !lang.isEmpty()) {
            for (Post_content pc : contents) {
                if (pc.getTitle() != null && !pc.getTitle().isEmpty()) {
                    String translatedTitle = translationService.translateText(pc.getTitle(), lang);
                    pc.setTitle(translatedTitle);
                }
                if (pc.getContent() != null && !pc.getContent().isEmpty()) {
                    String translatedContent = translationService.translateText(pc.getContent(), lang);
                    pc.setContent(translatedContent);
                }
            }
        }
        
        return ResponseEntity.ok(contents);
    }

}
