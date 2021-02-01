package com.tf.intf.services;

import java.util.Map;

import com.tf.intf.model.ParamVO;

public interface UserServices {

	String createCase(ParamVO param);

	Map<Object, Object> getTemplateId();

	String uploadGradedClassReport();

	String uploadSOQCommentsFile();

	String uploadSOQStatsFile();

	String deleteUploadedFile();

	String caseAndFileValidation();

	String uploadCSVFIle(ParamVO param);

}
