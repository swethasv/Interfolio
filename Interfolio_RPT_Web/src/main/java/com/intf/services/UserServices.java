package com.intf.services;

import java.io.IOException;
import java.util.Map;

public interface UserServices {

	String createCase(String template_id) throws IOException;

	Map<Object, Object> getTemplateId();

}
