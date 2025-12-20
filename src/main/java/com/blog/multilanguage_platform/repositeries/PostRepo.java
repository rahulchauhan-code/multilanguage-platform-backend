package com.blog.multilanguage_platform.repositeries;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.multilanguage_platform.dto.Post;

public interface PostRepo extends JpaRepository<Post, Integer> {

	List<Post> findByAuthorId(int authorId);
    void deleteByAuthorId(int authorId);
}
