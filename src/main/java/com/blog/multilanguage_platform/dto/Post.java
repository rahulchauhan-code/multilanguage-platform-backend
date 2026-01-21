package com.blog.multilanguage_platform.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "posts_seq_gen")
    @SequenceGenerator(name = "posts_seq_gen", sequenceName = "posts_seq", allocationSize = 1)
	private int postId;
	private int authorId;
	@Column(name = "category")
	private String catogery;
	private String status;
	private LocalDateTime created_at;

	// Transient list of contents attached at runtime (not persisted as a relationship here)
	@Transient
	private List<Post_content> contents;
}