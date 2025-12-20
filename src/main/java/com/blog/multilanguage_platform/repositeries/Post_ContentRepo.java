package com.blog.multilanguage_platform.repositeries;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.multilanguage_platform.dto.Post_content;

public interface Post_ContentRepo extends JpaRepository<Post_content, Integer> {
	void deleteByPostid(int postid);
	List<Post_content> findByPostid(int postid);
}
