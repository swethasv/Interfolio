package com.tf.intf.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tf.intf.model.TemplateVO;
import com.tf.intf.services.UserServices;
import com.tf.intf.util.Constants;

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

	@RequestMapping(path = "/getPreProcessData")
	public String getAllSOQFileDetails(Model model) {
		List<TemplateVO> soqDataList = userServices.getAllSOQFileDetails();
		model.addAttribute("soqData", soqDataList);
		return "validateData";
	}
	
	@RequestMapping(path = "/geSOQStatData")
	public String getAllSOQStatFileDetails(Model model) {
		List<TemplateVO> soqDataList = userServices.getSOQFileDetails(Constants.SOQ_STAT_FILE_TYPE);
		model.addAttribute("soqData", soqDataList);
		return "soqStatsFile";
	}
	
	@RequestMapping(path = "/geSOQCommentData")
	public String getAllSOQCommentFileDetails(Model model) {
		List<TemplateVO> soqDataList = userServices.getSOQFileDetails(Constants.SOQ_COMM_FILE_TYPE);
		model.addAttribute("soqData", soqDataList);
		return "soqCommFile";
	}
	
	@RequestMapping(path = "/geSOQGradedClassData")
	public String getAllSOQGradedFileDetails(Model model) {
		List<TemplateVO> soqDataList = userServices.getSOQFileDetails(Constants.GRADED_CLASS_FILE_TYPE);
		model.addAttribute("soqData", soqDataList);
		return "GCLFile";
	}
}
