package com.tf.intf.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tf.intf.services.UserServices;

@RestController
@RequestMapping(path = "/interfolio")
public class Controller {

	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
	
	@Autowired
	UserServices userServices;
	
	@GetMapping(path = "/createCase/{template_id}", produces = "application/json")
	public String createCase(@PathVariable String template_id) throws IOException {
		return userServices.createCase(template_id);
		
	}
	
	@GetMapping(path = "/getTemplateId", produces = "application/json")
	public Map<Object, Object> getTemplateId() {
		
		return userServices.getTemplateId();
	}
	
	@GetMapping(path = "/uploadGradedClassReport", produces = "application/json")
	public String uploadGradedClassReport() throws IOException {
		return userServices.uploadGradedClassReport();
		
	}
	
	@GetMapping(path = "/uploadSOQCommentsFile", produces = "application/json")
	public String uploadSOQCommentsFile() throws IOException {
		return userServices.uploadSOQCommentsFile();
		
	}
	
	@GetMapping(path = "/uploadSOQStatsFile", produces = "application/json")
	public String uploadSOQStatsFile() throws IOException {
		return userServices.uploadSOQStatsFile();
		
	}
	
	@GetMapping(path = "/deleteUploadedFile", produces = "application/json")
	public String deleteUploadedFile() throws IOException {
		return userServices.deleteUploadedFile();
	}
	
	@GetMapping(path = "/caseAndFileValidation", produces = "application/json")
	public String caseAndFileValidation() throws IOException {
		return userServices.caseAndFileValidation();
	}
}
