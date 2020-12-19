package com.business;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
 
/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 *
 */
public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    
    private String public_key = "OTLU675BE8AIISFI6I86AYGWGVOKNP9K:";
	  private String private_key = "H5GR667CX6G3ZZ2LQIG0D9EYM81ZJTBPB9F9N61RP5XY8JYSKHV6Y43K7TFQFN77QW6PP8V5FRJA5P9F1WNG4WMVYKSRLEE3U6YA4JD1D5M5EG1MA4Z2G3YZC5NUZEK5RI";
	  private String tenant_id = "17563"; //use databaseID for Faculty180 API

	  private String product = "byc"; //Value should be either 'faculty180', or 'byc' (if you are using FS or RPT)
	  private String request_verb = "POST";
	  private String host = "https://logic-sandbox.interfolio.com";
	  
	  private String request_string ="/byc-tenure/"+ tenant_id +"/applicants/18993/on_behalf_documents";
	  String jsonString = "{\"title\":\"SOQ_Test\",\"packet_section_id\":203530,\"file\":\"D:\\Interfolio\\SOQ Comments Report.pdf\",\"document\":{\"format\":\"PDF\"}}";
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
 
    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
         
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        
        httpConn.setRequestProperty("Timestamp", timestampString());
        httpConn.setRequestProperty("Authorization", gen_HMAC());
	    httpConn.setRequestProperty("INTF-DatabaseID", tenant_id);	
	    httpConn.setRequestMethod(request_verb);
	    httpConn.setDoInput(true);
	    httpConn.setDoOutput(true);		    
		httpConn.setRequestProperty("User-Agent", "Java client");
	   // conn.setRequestProperty("Accept", "application/json");
		httpConn.setRequestProperty("Content-Type", "multipart/form-data");
		httpConn.setRequestProperty("Content-Disposition", "form-data");
        
       /* httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Test", "Bonjour"); */
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
     
    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
 
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
 
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
 
        return response;
    }
}