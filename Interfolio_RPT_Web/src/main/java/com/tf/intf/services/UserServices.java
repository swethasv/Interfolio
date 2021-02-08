package com.tf.intf.services;

import java.util.List;
import java.util.Map;

import com.tf.intf.model.ParamVO;
import com.tf.intf.model.TemplateVO;

public interface UserServices {

	String createCase();

	List<TemplateVO> getTemplateId();

	String uploadGradedClassReport();

	String uploadSOQCommentsFile();

	String uploadSOQStatsFile();

	String deleteUploadedFile();

	String caseAndFileValidation();


	String uploadCSVFile(ParamVO param);

}
