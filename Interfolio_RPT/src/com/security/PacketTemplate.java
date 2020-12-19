package com.security;


import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class PacketTemplate {

	// Sandbox
	  private String public_key = "OTLU675BE8AIISFI6I86AYGWGVOKNP9K:";
	  private String private_key = "H5GR667CX6G3ZZ2LQIG0D9EYM81ZJTBPB9F9N61RP5XY8JYSKHV6Y43K7TFQFN77QW6PP8V5FRJA5P9F1WNG4WMVYKSRLEE3U6YA4JD1D5M5EG1MA4Z2G3YZC5NUZEK5RI";
	  private String tenant_id = "17563"; //use databaseID for Faculty180 API
	  private String host = "https://logic-sandbox.interfolio.com";
	  
	// Production 
	  /*private String public_key = "V92RB20EL5B3LF4FR0LOK562LEL3YVOE:";
	  private String private_key = "TC8N5V95B7QY1B9U7M1H14HQ3GLTH96294PMQ1O1QYX9IUWDVGHD6YQZLNPR3Y9WYWXABGM52P55O2T7FKNQZF65PNR5XT3EMFTRH6D1T50H75C6Y8NVK6J5KH5XKV4GHV";
	  private String tenant_id = "10216"; //use databaseID for Faculty180 API
	  private String host = "https://logic.interfolio.com"; */
	  

	  private String product = "byc"; //Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	  private String request_verb = "GET";
	 // private String host = "https://rpt-sandbox.interfolio.com";
	 
	  //private String request_string ="/byc-tenure/"+ tenant_id +"/packets/create_from_template";
	  //private String request_string = "/byc-tenure/" + tenant_id + "/units/17563/packet_templates";
	  private String request_string = "/byc-tenure/" + tenant_id + "/packet_templates";
	  //private String request_string = "/byc/core/tenure/" + tenant_id + "/units/hierarchy";
	  //private String request_string = "/byc-tenure/" + tenant_id + "/packet_templates/search";
	
	  //private String query_string = "?search_text=College%20of%20Humanities";
	  private String query_string = "";
	//Faculty180 API will not accept the query string as a part of the request verb, but BYC requires it.

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
	      SecretKeySpec secret_key = new SecretKeySpec(private_key.getBytes(), "HmacSHA1");
	      sha1_HMAC.init(secret_key);
	      String output_hash = org.apache.commons.codec.binary.Base64.encodeBase64String(sha1_HMAC.doFinal(verbRequestString().getBytes()));
	      
	      if (product == "faculty180"){
	        output_hash = ":" + output_hash;
	      }
	      String authorization_header = "INTF " + public_key + output_hash;
	     
	      return authorization_header;
	    }
	    catch(Exception e) {
	      System.out.println("Error: " + e.getMessage());
	      return null;
	    }
	  }
	 
	  private String getResults() throws IOException {
	    String full_request = host + request_string + query_string;
	   
	    URL url = new URL(full_request);
	    HttpURLConnection conn =
	      (HttpURLConnection) url.openConnection();
	    conn.setRequestProperty("Timestamp", timestampString());
	    conn.setRequestProperty("Authorization", gen_HMAC());
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
	  
	  public static int getPacketID(String jsonObj){
		  int packet_id = 0;
		  JSONObject jsonObject = new JSONObject(jsonObj);	      
	      JSONArray jsonArray = (JSONArray)jsonObject.get("results");	      
	      for (int i = 0; i < jsonArray.length(); i++) {
	    	  JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
	    	  packet_id = (int) jsonObject1.get("id");	    	 
	        }
	      return packet_id;
	  }
	  
	  public static void main(String[] args) {
	    PacketTemplate token = new PacketTemplate();
	  
	    try {
	      System.out.println(token.getResults());
	     
	    	 
	    } catch (IOException e) {
	      System.out.println("Error: " + e.getMessage());
	    }
	  }

 
}
