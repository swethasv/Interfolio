package com.business;


import java.io.*;
import java.net.*;
import java.util.*;


import org.json.JSONObject;

import com.db.DAOImpl;

public class RPTCreateCase {

	 
	  private String request_string ="/byc-tenure/"+ HMAC_Encryption.tenant_id +"/packets/create_from_template";	  
	  private String query_string = "";
	  String request_verb = "POST";
	   
	  private String createCase() throws IOException {
		  HMAC_Encryption hmac= new HMAC_Encryption();
		  StringBuilder sb = null;
		  ParamVO paramVO = new ParamVO();
		  DAOImpl impl = new DAOImpl();
		  String result = "No records found to create a new case!!";
		  try {
				paramVO = impl.getParamValues();
			} catch (Exception e) {
				e.printStackTrace();
			}
		  HttpURLConnection conn =null;
		  
		  if ( paramVO.getParamList() != null && paramVO.getParamList().size() != 3 ) {
			 
	    		for ( ParamVO tmpVO : paramVO.getParamList() ) {
	    			 conn = hmac.getConnection(request_string, query_string,request_verb);
				  try (OutputStream os = conn.getOutputStream()) {		    	
			    	
			    	Map< Object, Object >jsonValues = new HashMap< Object, Object >();		
					jsonValues.put("packet_id", tmpVO.getTemplate_id());
				    jsonValues.put("unit_id", PacketDetails.getUnitID(tmpVO.getTemplate_id()));			    
				    jsonValues.put("candidate_first_name", tmpVO.getCandidate_first_name());
				    jsonValues.put("candidate_last_name", tmpVO.getCandidate_last_name());
				    jsonValues.put("candidate_email", tmpVO.getCandidate_email());
				    //jsonValues.put("due_date", tmpVO.getDue_at());
				    JSONObject parameters =new JSONObject(jsonValues); 
				    JSONObject payload =new JSONObject();
				    payload.put("packet", parameters);
			    	System.out.println(payload);		    	
			    	os.write(payload.toString().getBytes());
			    	os.close();
				  } catch (IOException e1) {
				 	e1.printStackTrace();
				  }
				    System.out.println(conn.getResponseCode());
				    if (conn.getResponseCode() == 201) {
				    	System.out.println(conn.getResponseCode());
				    	 impl.updateAuditFlg(tmpVO.getCwid(),tmpVO.getTemplate_id());
				    }		    
				    
		            try {
				    // Buffer the result into a string
				    BufferedReader rd = new BufferedReader(
				        new InputStreamReader(conn.getInputStream()));
				    sb = new StringBuilder();
				    
				    while ((rd.readLine()) != null) {
				      sb.append(rd.readLine());
				    }
				    rd.close(); 
				    
		            } catch(FileNotFoundException e){
						  System.out.println(e.getMessage());
					  }catch(Exception ex) {
		            	ex.printStackTrace();
		            }
		            conn.disconnect();
		           
	    		}
	    		if(sb != null){
	    			result = sb.toString();
	    		}
	    	}
		    return result;
		  }

	  public static void main(String[] args) {
	    RPTCreateCase token = new RPTCreateCase();
	     try {
			token.createCase();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	    
	  }
	  
	 

 
}
