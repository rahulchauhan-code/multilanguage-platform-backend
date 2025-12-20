package com.blog.multilanguage_platform.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.multilanguage_platform.dto.Post;
import com.blog.multilanguage_platform.dto.PostRequest;
import com.blog.multilanguage_platform.dto.Post_content;
import com.blog.multilanguage_platform.repositeries.PostRepo;
import com.blog.multilanguage_platform.repositeries.Post_ContentRepo;

import jakarta.transaction.Transactional;

@Service
public class PostServices {
	@Autowired
	private PostRepo postRepo;
	@Autowired
	private Post_ContentRepo postContentRepo;
	//create Post 
	public Post createPost(Post post) {
		return this.postRepo.save(post);
	}
	//get all post
	public List<Post> getAllPosts() {
	    return this.postRepo.findAll();
	}
	//update Post
	public Post getPostById(Integer postId) {
		return this.postRepo.findById(postId).orElseThrow();
	}
	//Delete Post
	public void deletePost(Integer postId) {
		Post p=getPostById(postId);
		this.postRepo.delete(p);
	}
	//Delete All Posts by UserId 
	public void deleteByUserId(Integer userId) {
		this.postRepo.deleteByAuthorId(userId); 
	}
	//get Posts By Userid
	public List<Post> getPostsByUserId(int userId){
		return this.postRepo.findByAuthorId(userId);
	}
	
	@Transactional // Now guaranteed to rollback if ANY part fails
    public Post createPostWithContent(PostRequest request, int userId) {
        // 1. Create Post metadata
        Post newPost = new Post();
        newPost.setAuthorId(userId);
        newPost.setCatogery(request.getCategory()); // Fixed typo
        newPost.setStatus("PUBLISHED");
        newPost.setCreated_at(LocalDateTime.now());
        // Save Post (If this fails, execution stops here)
        Post savedPost = this.postRepo.save(newPost);
        // 2. Create Post Content
        Post_content content = new Post_content();
        content.setPostid(savedPost.getPostId());
        content.setTitle(request.getTitle());
        content.setContent(request.getContent());
        // Save Content
        this.postContentRepo.save(content);
        return savedPost;
    }
}
