package com.intf.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class HMAC_Encryption {

	
	// Production 
	 /* public static String public_key = "V92RB20EL5B3LF4FR0LOK562LEL3YVOE:";
	  public static String private_key = "TC8N5V95B7QY1B9U7M1H14HQ3GLTH96294PMQ1O1QYX9IUWDVGHD6YQZLNPR3Y9WYWXABGM52P55O2T7FKNQZF65PNR5XT3EMFTRH6D1T50H75C6Y8NVK6J5KH5XKV4GHV";
	  public static String tenant_id = "10216"; //use databaseID for Faculty180 API
	  public static String host = "https://logic.interfolio.com"; */

	  //Sandbox
	  public static String public_key = "OTLU675BE8AIISFI6I86AYGWGVOKNP9K:";
	  public static String private_key = "H5GR667CX6G3ZZ2LQIG0D9EYM81ZJTBPB9F9N61RP5XY8JYSKHV6Y43K7TFQFN77QW6PP8V5FRJA5P9F1WNG4WMVYKSRLEE3U6YA4JD1D5M5EG1MA4Z2G3YZC5NUZEK5RI";
	  public static String tenant_id = "17563"; //use databaseID for Faculty180 API
	  public static String host = "https://logic-sandbox.interfolio.com";
	  
	//This message is from swetha
	  public static String product = "byc"; //Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	  //private String request_verb = "POST";
	 
	  
	 

	 public String timestampString(){
		 SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		    String timestamp_string = dateFormatGmt.format(new Date());
		    
		    return timestamp_string;
	 }

	  public String gen_HMAC(String request_string, String query_string, String request_verb) {
		  String HMAC_request_string = request_string;
		    if (product == "byc"){
		      HMAC_request_string = HMAC_request_string + query_string; 
		    }
		    
		  String verbReq = request_verb + "\n\n\n" +  timestampString() + "\n" + HMAC_request_string;
	    try {
	      Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
	      SecretKeySpec secret_key = new SecretKeySpec(private_key.getBytes(), "HmacSHA1");
	      sha1_HMAC.init(secret_key);
	      String output_hash = org.apache.commons.codec.binary.Base64.encodeBase64String(sha1_HMAC.doFinal(verbReq.getBytes()));	      
	     
	      String authorization_header = "INTF " + public_key + output_hash;
	     
	      return authorization_header;
	    }
	    catch(Exception e) {
	      System.out.println("Error: " + e.getMessage());
	      return null;
	    }
	  }
	  
	  public String gen_HMAC(String query_string, String request_verb) {
		  String HMAC_request_string = "/byc-tenure/" + tenant_id + "/packet_templates/";
		    if (product == "byc"){
		      HMAC_request_string = HMAC_request_string + query_string; 
		    }
		    
		  String verbReq = request_verb + "\n\n\n" +  timestampString() + "\n" + HMAC_request_string;
	    try {
	      Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
	      SecretKeySpec secret_key = new SecretKeySpec(private_key.getBytes(), "HmacSHA1");
	      sha1_HMAC.init(secret_key);
	      String output_hash = org.apache.commons.codec.binary.Base64.encodeBase64String(sha1_HMAC.doFinal(verbReq.getBytes()));	      
	     
	      String authorization_header = "INTF " + public_key + output_hash;
	     
	      return authorization_header;
	    }
	    catch(Exception e) {
	      System.out.println("Error: " + e.getMessage());
	      return null;
	    }
	  }
	 
	  public String getResults(String request_string, String query_string) throws IOException {
	    String full_request = host + request_string + query_string;
	   
	    URL url = new URL(full_request);
	    HttpURLConnection conn =
	      (HttpURLConnection) url.openConnection();
	    conn.setRequestProperty("Timestamp", timestampString());
	    conn.setRequestProperty("Authorization", gen_HMAC(request_string,query_string));
	    conn.setRequestProperty("INTF-DatabaseID", tenant_id);

	    if (conn.getResponseCode() != 200) {
	      throw new IOException(conn.getResponseMessage());
	    }

	    // Buffer the result into a string
	    BufferedReader rd = new BufferedReader(
	        new InputStreamReader(conn.getInputStream()));
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = rd.readLine()) != null) {
	      sb.append(line);
	    }
	    rd.close(); 

	    conn.disconnect();
	    return sb.toString();
	  }
	  
	  public HttpURLConnection getConnection(String request_string, String query_string, String request_verb) throws IOException {
		  String full_request = host + request_string + query_string;
		    System.out.println("Calling: " + full_request);
		    StringBuilder sb = null;
		    URL url = new URL(full_request);
		    
		    /*
		    HttpsURLConnection httpsConn =  (HttpsURLConnection) url.openConnection();
		    httpsConn.setRequestProperty("Timestamp", timestampString());
		    httpsConn.setRequestProperty("Authorization", gen_HMAC());
		    httpsConn.setRequestProperty("INTF-DatabaseID", tenant_id);
		    */
		    
		    HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();
		    conn.setRequestProperty("Timestamp", timestampString());
		    conn.setRequestProperty("Authorization", gen_HMAC(request_string,query_string,request_verb));
		    conn.setRequestProperty("INTF-DatabaseID", tenant_id);	
		    conn.setRequestMethod(request_verb);
		    conn.setDoInput(true);
			conn.setDoOutput(true);		    
		    conn.setRequestProperty("User-Agent", "Java client");
		    conn.setRequestProperty("Accept", "application/json");
		    conn.setRequestProperty("Content-Type", "application/json");
			//conn.setRequestProperty("Content-Type", "multipart/form-data");
		    //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//application/x-www-form-urlencoded			
			conn.setDoInput(true);
			conn.setDoOutput(true);			
		   
		    return conn;
		  }
}
