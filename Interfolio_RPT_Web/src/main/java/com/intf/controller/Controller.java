package com.intf.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intf.model.User;
import com.intf.services.UserServices;

@RestController
@RequestMapping(path = "/interfolio")
public class Controller {

	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
	
	@Autowired
	UserServices userServices;
	
	@GetMapping(path = "/getData", produces = "application/json")
	public User getData() {
		User user = new User();
		user.setFirstName("ABCD");
		user.setLastName("123");
		user.setEmailAddress("ABCD@email.com");
		user.setTemplateId("12345DEF");

		LOGGER.info("Calling controller");
		
		return user;
	}
	@GetMapping({"/", "/hello"})
	    public String hello(Model model, @RequestParam(value="name", required=false, defaultValue="World") String name) {
	        model.addAttribute("name", name);
	        return "hello";
	    }

	@PostMapping("/getUser")
	public User getUser(@RequestBody User user) {

		return user;
	}
	
	@GetMapping(path = "/createCase/{template_id}", produces = "application/json")
	public String createCase(@PathVariable String template_id) throws IOException {
		return userServices.createCase(template_id);
		
	}
	
	@GetMapping(path = "/getTemplateId", produces = "application/json")
	public Map<Object, Object> getTemplateId() {
		
		return userServices.getTemplateId();
	}
}
