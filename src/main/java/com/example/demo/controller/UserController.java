package com.example.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.annotation.LoggedInUser;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/")
	public String healthCheck() {
		
		return "Spring boot project is up";
	}
	
	@GetMapping("/user-page")
	public String global(Authentication a , Principal p) { //Authentication classi ile logged in olmus user'a ulasiriz, Principal ile logged in user'in usernameine
	 User user = (User) a.getPrincipal();
	 //User user = SecurityContextHolder.getContext().getAuthentication(); //diyerek Authentication objesini parametre olarak almadan SecurityContextHolderdan getirebiliriz
	// User user3 = (User) p; // bu islemi gerceklestiremeyiz
	 
	 System.out.println(user);
	 System.out.println(p.getName());
	 
		return "This is Global Page. Welcome " + user.getEmail();
	}
	
	@GetMapping("/admin-page")
	@PreAuthorize("hasRole('ROLE_admin')")
	public String secured(@LoggedInUser User user) { // Ya da 2.yol olarak kendi anotasyonumuzu yaratarak user'a dogrudan erisim saglayabiliriz
		
		return "This is Admin Page. Welcome admin : " + user.getUsername();
	}
	
	
	@PostMapping("/api/1.0/users")
	public User createUser(@RequestBody User user) {
		
		return userService.save(user);
	}
	
	
	@PutMapping("/api/1.0/users/{id}")	//Her user sadece kendini update edebilir ya da admin herkesi uptade edebilir
	@PreAuthorize("(#id == principal.id) OR hasRole('ROLE_admin')")//parametre olarak gelen id ile SecurityContextHolderdaki yani logged in user'in id si ayni degilse controllera ugramadan hata dondurur
	public User updateUser(@RequestBody User user, @PathVariable long id, @LoggedInUser User appUser) {
		
		return userService.update(id, user);
	}

}
