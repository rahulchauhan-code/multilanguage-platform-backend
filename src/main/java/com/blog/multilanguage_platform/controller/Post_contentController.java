package com.blog.multilanguage_platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.multilanguage_platform.dto.Post_content;
import com.blog.multilanguage_platform.services.Post_contentServices;

@RestController
@RequestMapping("/api/post_contents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class Post_contentController {
	@Autowired
	private Post_contentServices psServ;
	@GetMapping("/")
    public ResponseEntity<List<Post_content>> getAllPostContents() {
        return ResponseEntity.ok(this.psServ.getAllPostContent());
    }

}
