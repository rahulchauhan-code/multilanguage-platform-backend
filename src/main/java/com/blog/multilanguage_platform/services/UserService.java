package com.blog.multilanguage_platform.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blog.multilanguage_platform.dto.Users;
import com.blog.multilanguage_platform.repositeries.UserRepo;

@Service
public class UserService {
	@Autowired
	private UserRepo  userRepo;
	//create user
	public Users createUser(Users user) {
		return this.userRepo.save(user);
	}
	//update User
	public Users updateUser(Users user, Integer userId) {
		Users u=getUserById(userId);
		u.setName(user.getName());
		u.setUsername(user.getUsername());
		u.setBio(user.getBio());
		return this.userRepo.save(u);
	}
	// get User by Id
	public Users getUserById(Integer userId) {
		return this.userRepo.findById(userId).orElseThrow();
	}
	// delete User by Id
	public void deleteUser(Integer userId) {
		Users u=getUserById(userId);
		this.userRepo.delete(u);
	}
	//get All Users
	public List<Users> getAllUsers(){
		return this.userRepo.findAll();
	}
	// User Login
	public Users login(String email, String password) {
	    Users user = this.userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	    if (user.getPassword().equals(password)) {
	        return user;
	    } else {
	        throw new RuntimeException("Invalid Password");
	    }
	}
	public Optional<Users> findByEmail(String email) {
	    return this.userRepo.findByEmail(email);
	}

}
