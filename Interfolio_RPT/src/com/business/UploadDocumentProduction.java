package com.business;
//package com.security;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody; 
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class UploadDocumentProduction {
	
	public static String public_key = "V92RB20EL5B3LF4FR0LOK562LEL3YVOE:";
	  public static String private_key = "TC8N5V95B7QY1B9U7M1H14HQ3GLTH96294PMQ1O1QYX9IUWDVGHD6YQZLNPR3Y9WYWXABGM52P55O2T7FKNQZF65PNR5XT3EMFTRH6D1T50H75C6Y8NVK6J5KH5XKV4GHV";
	  public static String tenant_id = "10216"; //use databaseID for Faculty180 API
	  public static String host = "https://logic.interfolio.com"; 

	  private String product = "byc"; //Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	  private String request_verb = "POST";
	  
	  
	  private String request_string ="/byc-tenure/"+ tenant_id +"/applicants/18993/on_behalf_documents";
	  //String jsonString = "{\"title\":\"SOQ_Test\",\"packet_section_id\":203530,\"file\":\"D:\\Interfolio\\SOQ Comments Report.pdf\",\"document\":{\"format\":\"PDF\"}}";
	  //String jsonString = "{\"title\":\"SOQ_Test\",\"packet_section_id\":\"203530\",\"file\":\"D:\\Interfolio\\SOQ Comments Report.pdf\",\"documents\":{\"format\":\"PDF\"}}";
	 /* private String request_string ="/byc-tenure/"+ tenant_id +"/applicants/18993/applicant_documents";
	  String jsonString = "{\"documents\":{\"format\":\"PDF\"}}";*/
	  
	  private String query_string = "";

	  private String HMACRequestString() { 
	    String HMAC_request_string = request_string;
	    if (product == "byc"){
	      HMAC_request_string = HMAC_request_string + query_string; 
	    }
	    System.out.println("HMAC_request_string - Request String: " + HMAC_request_string);
	    return HMAC_request_string;
	  }

	  private final String timestampString() {
		    SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		    String timestamp_string = dateFormatGmt.format(new Date());
		    System.out.println("Timestamp: " + timestamp_string);
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
	      System.out.println("output_hash: " + output_hash);
	      if (product == "faculty180"){
	        output_hash = ":" + output_hash;
	      }
	      String authorization_header = "INTF " + public_key + output_hash;
	      System.out.println("Authorization: " + authorization_header);
	      return authorization_header;
	    }
	    catch(Exception e) {
	      System.out.println("Error: " + e.getMessage());
	      return null;
	    }
	  }   

	   
	  private String getResults() throws IOException {
		  //String full_request = host + request_string + query_string;
		  String full_request = "https://logic.interfolio.com/byc-tenure/10216/applicants/151338/applicant_docuemnt";  
		  System.out.println("Calling: " + full_request);
		 /*   StringBuilder sb = null;
		    URL url = new URL(full_request);
		  HttpURLConnection conn =
			      (HttpURLConnection) url.openConnection();
			    conn.setRequestProperty("Timestamp", timestampString());
			    conn.setRequestProperty("Authorization", gen_HMAC());
			    conn.setRequestProperty("INTF-DatabaseID", tenant_id);	
			    conn.setRequestMethod(request_verb);
			    conn.setDoInput(true);
				conn.setDoOutput(true);		    
			    conn.setRequestProperty("User-Agent", "Java client");
			    conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Type", "multipart/form-data");
				//conn.setRequestProperty("Content-Disposition", "form-data");
		  */
		   @SuppressWarnings("deprecation")
		DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            
            // server back-end URL
           HttpPost httppost = new HttpPost("https://logic.interfolio.com/byc-tenure/10216/applicants/151338/applicant_documents");
           
		   File inFile = new File("D:\\Interfolio\\SOQ Comments Report.pdf");
	       FileInputStream fis = null;
	       fis = new FileInputStream(inFile);  
           
           MultipartEntity entity = new MultipartEntity();
           // set the file input stream and file name as arguments	           
           entity.addPart("file", new InputStreamBody(fis, inFile.getName()));  
           httppost.setEntity(entity);
           String jsonString = "{\"title\":\"SOQ_Test\",\"packet_section_id\":\"1459684\",\"document\":{\"format\":\"PDF\"}}";
           HttpEntity e = new StringEntity(jsonString.toString());
           httppost.setEntity(e);
           httppost.setHeader("Timestamp", timestampString()); 
			httppost.setHeader("Authorization", gen_HMAC());
			//httppost.setHeader("INTF-DatabaseID", tenant_id);	
			httppost.setHeader("User-Agent", "Java client");
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-Type", "multipart/form-data");
			
			
           // execute the request
           HttpResponse response = httpclient.execute(httppost);
           
           int statusCode = response.getStatusLine().getStatusCode();
           HttpEntity responseEntity = response.getEntity();
           String responseString = EntityUtils.toString(responseEntity, "UTF-8");
           System.out.println("[" + statusCode + "] " + responseString);
         /*  conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
           conn.setRequestProperty("file", entity.getContentType().getValue());
	       try (OutputStream os = conn.getOutputStream()) { 
	            JSONObject jsonValues1 = new JSONObject();
			    jsonValues1.put("format", "PDF");
			    JSONObject jsonValues = new JSONObject();	
				jsonValues.put("title", "test");
			    jsonValues.put("packet_section_id", 1459684);
			    jsonValues.put("document", jsonValues1);				    
		    	System.out.println(jsonValues);	 
		    	os.write(jsonValues.toString().getBytes());
		    	os.close();
	       } catch (IOException e1) {
				  // TODO Auto-generated catch block
			 	e1.printStackTrace();
			  }
		    if (conn.getResponseCode() != 200) {
		    	System.out.println(conn.getResponseCode());
		    	System.out.println(conn.getContent());
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
		    
		    System.out.println("Resposne: " + sb.toString());*/
		    return responseString;
		  }
		   
	public static void main(String[] args) {
		UploadDocumentProduction token = new UploadDocumentProduction();
	    try {
	      System.out.println(token.getResults());
	    } catch (IOException e) {
	      System.out.println(e.getMessage());
	    }
	  }

 
}
