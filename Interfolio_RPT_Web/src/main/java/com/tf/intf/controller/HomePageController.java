package com.tf.intf.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class HomePageController {

	@GetMapping(value = "/")
	public String homePage() {
		return "uploadSOQFile";
	}
}
