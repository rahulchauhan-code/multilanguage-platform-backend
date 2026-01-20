package com.blog.multilanguage_platform.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	// Use IDENTITY so PostgreSQL can auto-increment without pre-created sequences
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private Integer userId;
	private String name;
	private String username;
	private String email;
	private String password;
	private String bio;
	private String role;
	private LocalDateTime createAt;
}