package com.blog.multilanguage_platform.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.multilanguage_platform.dto.Users;
import com.blog.multilanguage_platform.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
	@Autowired
	private UserService userServices;
	//Create User
	@PostMapping("/create")
	public ResponseEntity<Users> createUser(@RequestBody Users user){
		Users u=this.userServices.createUser(user);
		return new ResponseEntity<>(u, HttpStatus.CREATED);
	}
	
	//Update User By Id 
	@PutMapping("/{userId}")
	public ResponseEntity<Users> updateUsers(@RequestBody Users user,@PathVariable Integer userId){
		Users u=this.userServices.updateUser(user, userId);
		return ResponseEntity.ok(u);
	}
	
	//Delete User By Id
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer userId){
		this.userServices.deleteUser(userId);
		return new ResponseEntity<Users>(HttpStatus.OK);
	}
	
	//Get All Users
	@GetMapping("/")
	public ResponseEntity<List<Users>> getAllUsers(){
		return ResponseEntity.ok(this.userServices.getAllUsers());
	}
	
	//Get User By Id
	@GetMapping("/{userId}")
	public ResponseEntity<Users> getUserById(@PathVariable Integer userId){
		return ResponseEntity.ok(this.userServices.getUserById(userId));
	}
	// User Login
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
	    String email = credentials.get("email");
	    String password = credentials.get("password");

	    try {
	        Users user = this.userServices.login(email, password);
	        
	        // Create a cookie to store user information (e.g., email or a token)
	        Cookie loginCookie = new Cookie("userEmail", user.getEmail());
	        loginCookie.setMaxAge(7 * 24 * 60 * 60); // Cookie valid for 7 days
	        loginCookie.setPath("/"); // Accessible across the whole application
	        loginCookie.setHttpOnly(true); // Security: Prevents JavaScript from accessing the cookie
	        
	        response.addCookie(loginCookie);
	        
	        return new ResponseEntity<>(user, HttpStatus.OK);
	    } catch (RuntimeException e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
	    }
	}

	// Endpoint to check for existing login cookie
	@GetMapping("/check-login")
	public ResponseEntity<?> checkEasyLogin(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("userEmail".equals(cookie.getName())) {
	                String email = cookie.getValue();
	                // Find user by email from the cookie
	                Optional<Users> user = this.userServices.findByEmail(email);
	                if (user.isPresent()) {
	                    return new ResponseEntity<>(user.get(), HttpStatus.OK);
	                }
	            }
	        }
	    }
	    return new ResponseEntity<>("No session found", HttpStatus.NOT_FOUND);
	}
	// User Logout
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(HttpServletResponse response) {
	    Cookie cookie = new Cookie("userEmail", null);
	    cookie.setPath("/");       
	    cookie.setHttpOnly(true);  
	    cookie.setMaxAge(0);       
	    response.addCookie(cookie);
	    return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
	}
}
