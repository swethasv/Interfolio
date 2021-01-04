package com.intf.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class Util {

	 public String timestampString(){
		 SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		    String timestamp_string = dateFormatGmt.format(new Date());
		    
		    return timestamp_string;
	 }
}
