package com.business;


import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class RPTDeleteCase {

	  private String product = "byc"; //Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	  private String request_verb = "GET";
	  //private String request_string ="/byc-tenure/"+ HMAC_Encryption.tenant_id +"/packets/30089/packet_attachments";
	  private String request_string ="/byc-tenure/"+ HMAC_Encryption.tenant_id +"/packets/30115";
	 // private String request_string ="/byc-tenure/"+ HMAC_Encryption.tenant_id +"/applicants/18993/dossier";
	  private String query_string = "";

	  private String HMACRequestString() { 
	    String HMAC_request_string = request_string;
	    if (product == "byc"){
	      HMAC_request_string = HMAC_request_string + query_string; 
	    }
	    return HMAC_request_string;
	  }

	  private final String timestampString() {
	    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	    String timestamp_string = dateFormatGmt.format(new Date());
	    return timestamp_string;
	  }

	  private String verbRequestString() { 
	    return request_verb + "\n\n\n" + timestampString() + "\n" + HMACRequestString();
	  }

	  private String gen_HMAC() {
	    try {
	      Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
	      SecretKeySpec secret_key = new SecretKeySpec(HMAC_Encryption.private_key.getBytes(), "HmacSHA1");
	      sha1_HMAC.init(secret_key);
	      String output_hash = org.apache.commons.codec.binary.Base64.encodeBase64String(sha1_HMAC.doFinal(verbRequestString().getBytes()));
	      String authorization_header = "INTF " + HMAC_Encryption.public_key + output_hash;
	      return authorization_header;
	    }
	    catch(Exception e) {
	      System.out.println("Error: " + e.getMessage());
	      return null;
	    }
	  }   

	   
	  private String getResults() throws IOException {
		    String full_request = HMAC_Encryption.host + request_string + query_string;
		    System.out.println("Calling: " + full_request);
		    StringBuilder sb = null;
		    HMAC_Encryption hmac = new HMAC_Encryption();
		    URL url = new URL(full_request);
		    
		    HttpURLConnection conn =
		      (HttpURLConnection) url.openConnection();
		    conn.setRequestProperty("Timestamp", hmac.timestampString());
		    conn.setRequestProperty("Authorization", gen_HMAC());
		    conn.setRequestProperty("INTF-DatabaseID", HMAC_Encryption.tenant_id);	
		    conn.setRequestMethod(request_verb);
		    conn.setDoInput(true);
			conn.setDoOutput(true);		    
		    conn.setRequestProperty("User-Agent", "Java client");
		    conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
		    
		    if (conn.getResponseCode() != 200) {
		    	System.out.println(conn.getResponseCode());
		      throw new IOException(conn.getResponseMessage());
		    }
            try {
		    // Buffer the result into a string
		    BufferedReader rd = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		    sb = new StringBuilder();
		    String line;
		    while ((line = rd.readLine()) != null) {
		      sb.append(line);
		    }
		    rd.close(); 
		    conn.disconnect();
            } catch(Exception ex) {
            	ex.printStackTrace();
            }
		    
		    System.out.println("Resposne: " + sb.toString());
		    return sb.toString();
		  }

	  public static void main(String[] args) {
	    RPTDeleteCase token = new RPTDeleteCase();
	    try {
	      System.out.println(token.getResults());
	    } catch (IOException e) {
	      System.out.println(e.getMessage());
	    }
	  }
 
}
