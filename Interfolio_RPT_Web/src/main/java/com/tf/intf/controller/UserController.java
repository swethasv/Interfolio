package com.tf.intf.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tf.intf.model.TemplateVO;
import com.tf.intf.services.UserServices;

@Controller
public class UserController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserServices userServices;

	@RequestMapping(path = "/getTemplate")
	public String getTemplateId(Model model) {
		List<TemplateVO> templateList = userServices.getTemplateId();
		model.addAttribute("templates", templateList);
		return "createCase";
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
	public String caseAndFileValidation() {
		return userServices.caseAndFileValidation();
	}

}
