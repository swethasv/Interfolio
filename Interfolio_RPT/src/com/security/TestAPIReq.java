package com.security;

/*
 * Stolen from http://xml.nig.ac.jp/tutorial/rest/index.html
 * and http://www.dr-chuck.com/csev-blog/2007/09/calling-rest-web-services-from-java/
*/
import java.io.*;
import java.net.*;

public class TestAPIReq {

	public static void main(String[] args) {
		
		try {
			TestAPIReq.MyGETRequest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void MyGETRequest() throws IOException {
	    URL urlForGetRequest = new URL(""
	    		+ "https://logic-sandbox.interfolio.com/byc-tenure/17563/packet_templates");
	    String readLine = null;
	    HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
	    conection.setRequestMethod("GET");
	    conection.setRequestProperty("TimeStamp", "2020-11-17 04:25:52"); 
	    conection.setRequestProperty("Authorization", "INTF OTLU675BE8AIISFI6I86AYGWGVOKNP9K:63qaU+0DGxmbSscgav5CuK1olxg=");
	    conection.setRequestProperty("INTF-DatabaseID", "17563"); 
	    int responseCode = conection.getResponseCode();
	    

	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(conection.getInputStream()));
	        StringBuffer response = new StringBuffer();
	        while ((readLine = in .readLine()) != null) {
	            response.append(readLine);
	        } in .close();
	        // print result
	        System.out.println("JSON String Result " + response.toString());
	        //GetAndPost.POSTRequest(response.toString());
	    } else {
	    	
	    	TestAPIReq test = new TestAPIReq();
	        System.out.println(test.errorCodes(responseCode));
	    }
	}
	
	public String errorCodes(int responseCode){
		String errorMessage = "Error while running the API request!!";
		if(responseCode == 401){
    		errorMessage = "401 Unauthorized — Client failed to authenticate with the server";
    	}else if(responseCode == 403 ){
    		errorMessage = "403 Forbidden — Client authenticated but does not have permission to access the requested resource";
    	}else if(responseCode == 404 ){
    		errorMessage = "404 Not Found — The requested resource does not exist";
    	}else if(responseCode == 412 ){
    		errorMessage = "412 Precondition Failed — One or more conditions in the request header fields evaluated to false";
    	}else if(responseCode == 500 ){
    		errorMessage = "500 Internal Server Error — A generic error occurred on the server";
    	}else if(responseCode == 503 ){
    		errorMessage = "503 Service Unavailable — The requested service is not available";
    	}
		return errorMessage;
	}
}