package com.interfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interfolio.model.User;

@RestController
@RequestMapping(path = "/User")
public class UserController {

	@GetMapping(path = "/getData", produces = "application/json")
	public User getData() {
		User user = new User();
		user.setFirstName("ABCD");
		user.setLastName("123");
		user.setEmailAddress("ABCD@email.com");
		user.setTemplateId("12345DEF");
		
		return user;
	}

	@PostMapping("/getUser")
	public User getUser(@RequestBody User user) {

		return user;
	}
}
