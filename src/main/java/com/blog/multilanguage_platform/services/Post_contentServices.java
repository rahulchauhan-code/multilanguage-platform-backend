package com.blog.multilanguage_platform.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.multilanguage_platform.dto.Post_content;
import com.blog.multilanguage_platform.repositeries.Post_ContentRepo;
@Service
public class Post_contentServices {
	@Autowired
	private Post_ContentRepo pcRepo;
	//create Post_Content
	public Post_content createPosContent(Post_content pc) {
		return this.pcRepo.save(pc);
	}
	//get All Post_Content
	public List<Post_content> getAllPostContent() {
		return this.pcRepo.findAll();
	}
	//update Post_Content
	public Post_content updatePostContent(Integer userId, Post_content pc) {
		Post_content pc1=getPostContentById(userId);
		pc1.setTitle(pc.getTitle());
		pc1.setContent(pc.getContent());
		return this.pcRepo.save(pc1);
	}
	//get Post_Content by Id
	public Post_content getPostContentById(Integer pcId) {
		return this.pcRepo.findById(pcId).orElseThrow();
	}
	//delete Post_Content
	public void deletePostContent(Integer pcId) {
		Post_content pc=getPostContentById(pcId);
		this.pcRepo.delete(pc);
	}
	//Delete All Post_Content
	public void deleteByPostid(Integer postid) {
		this.pcRepo.deleteByPostid(postid);
	}
	//getPost_Content By PostId
	public List<Post_content> getPostContentByPostId(int postid){
		return this.pcRepo.findByPostid(postid);
	}
}
