package com.blog.multilanguage_platform.repositeries;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.multilanguage_platform.dto.Users;

public interface UserRepo extends JpaRepository<Users, Integer>{
	Optional<Users> findByEmail(String email);
}
