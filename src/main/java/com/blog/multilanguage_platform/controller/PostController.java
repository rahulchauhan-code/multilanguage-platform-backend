package com.blog.multilanguage_platform.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blog.multilanguage_platform.dto.Post;
import com.blog.multilanguage_platform.dto.PostRequest;
import com.blog.multilanguage_platform.dto.Users;
import com.blog.multilanguage_platform.repositeries.PostRepo;
import com.blog.multilanguage_platform.repositeries.Post_ContentRepo;
import com.blog.multilanguage_platform.services.PostServices;
import com.blog.multilanguage_platform.services.TranslationService;
import com.blog.multilanguage_platform.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PostController {
	@Autowired
	private PostServices postServices;
	@Autowired
	private UserService userServices;
	@Autowired
	private Post_ContentRepo postContentRepo;
	@Autowired
	private PostRepo postRepo;
	@Autowired
	private TranslationService translationService;
	//Create Post
	@PostMapping("/create")
	public ResponseEntity<Post> createUser(@RequestBody Post post){
		Post p=this.postServices.createPost(post);
		return new ResponseEntity<>(p, HttpStatus.CREATED);
	}
	// get all posts
	@GetMapping("/")
	public ResponseEntity<List<Post>> getAllPosts() {
	    return ResponseEntity.ok(this.postServices.getAllPosts());
	}
	//Delete Post By Id
	@DeleteMapping("/{postId}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer postId){
		this.postServices.deletePost(postId);
		return new ResponseEntity<Post>(HttpStatus.OK);
	}
	//Delete Posts By UserId
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deletePostsByUserId(@PathVariable int userId){
		
		this.postServices.deleteByUserId(userId);
		return new ResponseEntity<Post>(HttpStatus.OK);
	}
	//Get Posts By UserId
	/*
	 * @GetMapping("/") public ResponseEntity<List<Post>>
	 * getPostsByUserId(@PathVariable int userID){
	 * 
	 * }
	 */
	@PostMapping("/publish")
    public ResponseEntity<?> publishPost(@RequestBody PostRequest request, HttpServletRequest httpRequest) {
        String email = null;
        
        // 1. Retrieve email from cookies
        if (httpRequest.getCookies() != null) {
            for (Cookie cookie : httpRequest.getCookies()) {
                if ("userEmail".equals(cookie.getName())) {
                    email = cookie.getValue();
                    break;
                }
            }
        }

        if (email == null) {
            return new ResponseEntity<>("Unauthorized: No login session found", HttpStatus.UNAUTHORIZED);
        }

        // 2. Find User ID by Email
        Optional<Users> userOpt = this.userServices.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
        }

        // 3. Create Post using the found User ID
        Post createdPost = this.postServices.createPostWithContent(request, userOpt.get().getUserId());
        
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }
	//post by userid
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable int userId, @RequestParam(required = false) String lang) {
	    
	    List<Post> posts = this.postServices.getPostsByUserId(userId);
	    
	    if (lang != null && !lang.isEmpty()) {
	        posts.forEach(post -> {
	            // Translate the category
	            String translatedCategory = translationService.translateText(post.getCatogery(), lang);
	            post.setCatogery(translatedCategory);
	            
	        });
	    }
	    return ResponseEntity.ok(posts);
	}
}
