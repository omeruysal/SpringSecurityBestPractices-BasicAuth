package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserAuthService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override //Bu methodu override etmemiz zorunlu, burada username kontrolu yapariz eger bu unique username'e sahip kullanici varsa, SecurityContextine bu useri gondeririz, daha sonra password kontrol islemi yapilir
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
		User user = userRepository.findByUsername(username);
	
	if(user==null) {
		throw new UsernameNotFoundException("User bulunamadi");
	}

	return user;
	}

}
