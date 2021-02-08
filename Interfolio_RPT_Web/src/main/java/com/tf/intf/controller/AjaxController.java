package com.tf.intf.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tf.intf.model.ParamVO;
import com.tf.intf.services.UserServices;

@RestController
@RequestMapping(path = "/interfolio")
public class AjaxController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AjaxController.class);

	@Autowired
	UserServices userServices;

	@GetMapping("/createCase")
	public String createCase() {
		return userServices.createCase();
	}
	
	@PostMapping("/uploadCSVFile")
	public String uploadCSVFile(@RequestBody ParamVO param) throws IOException {
		return userServices.uploadCSVFile(param);
	}
}
