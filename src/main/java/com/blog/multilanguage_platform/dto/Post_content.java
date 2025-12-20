package com.blog.multilanguage_platform.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post_content {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int postid;
	private String title;
	private String content;
}
