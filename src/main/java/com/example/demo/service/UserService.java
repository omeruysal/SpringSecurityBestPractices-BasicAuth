package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	public User save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}
	
	public User getOne(long id, User user) {
		
		return userRepository.findById(id).orElseThrow(()-> new NotFoundException());
	}

	public User update(long id, User user) {
		
		User userDB = userRepository.findById(id).orElseThrow(()-> new NotFoundException());
		userDB.setEmail(user.getEmail());
		
		return userRepository.save(userDB);
	}

}
