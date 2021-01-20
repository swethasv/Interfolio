package com.tf.intf.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tf.intf.DAO.DAO;
import com.tf.intf.model.CaseDetailsVO;
import com.tf.intf.model.ParamVO;

@Component
public class IntfUtils {

	@Autowired
	DAO dao;
	
	@Autowired
	HMAC_Encryption hmc_Encryption;

	public String timestampString() {
		SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timestamp_string = dateFormatGmt.format(new Date());

		return timestamp_string;
	}

	public java.sql.Date getSysdate() {
		java.util.Date uDate = new java.util.Date();
		java.sql.Date sqlDate = new java.sql.Date(uDate.getTime());

		return sqlDate;
	}

	public void getFileFromNetwork(ParamVO paramVO) {
		URL url = null;
		File saveDir = null;
		try {
			url = new URL(paramVO.getNetworkPath() + paramVO.getFile_name());
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestProperty("UserVO-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			InputStream inputStream = conn.getInputStream();
			byte[] getData = readInputStream(inputStream);
			saveDir = new File(Constants.LOCALFILEPATH);
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			File file = new File(saveDir + File.separator + paramVO.getFile_name());
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(getData);
			if (fos != null) {
				fos.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			String errMsg = url + " not found in the network path";
			System.out.println(errMsg);
			paramVO.setErrorMsg(errMsg);
			dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
		}
	}

	private static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	/**
	 * Fetch the template name using template ID
	 * 
	 * @param paramVO
	 * @return templateName
	 */
	private String getTemplateName(ParamVO paramVO) throws Exception {
		String templateName = null;
		String request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/packet_templates/"
				+ paramVO.getTemplate_id();
		String request_verb = Constants.REQ_GET;
		String result = getAPIResponse(request_string, request_verb, paramVO);
		if (result != null) {
			JSONObject jsonObject = new JSONObject(result);
			JSONObject jsonObject1 = (JSONObject) jsonObject.get("packet_template");
			templateName = (String) jsonObject1.get("name");
			templateName = templateName.replace(" ", Constants.REPLACE_WHITESPACE);
		} else {
			paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
			dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
		}
		return templateName;
	}

	/**
	 * Get the applicant ID and requirement section ID using case ID
	 * 
	 * @param paramVO
	 * @param packetSectionName
	 * @return requirement ID and applicant ID as part of the caseDetails object
	 */
	public CaseDetailsVO getCaseDetails(ParamVO paramVO, String packetSectionName) {
		CaseDetailsVO caseDetails = new CaseDetailsVO();
		String request_string = null;
		try {
			int caseID = getCaseID(paramVO);
			request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/packets/" + caseID;
			if (caseID > 0) {
				String request_verb = Constants.REQ_GET;
				JSONObject jsonObject;
				jsonObject = new JSONObject(getAPIResponse(request_string, request_verb, paramVO));
				JSONObject jsonObject1 = (JSONObject) jsonObject.get(Constants.PACKET);
				caseDetails.setApplicant_id((int) jsonObject1.get(Constants.APP_ID));
				JSONArray jsonArray = (JSONArray) jsonObject1.get(Constants.REQ_BY_SEC);
				JSONObject jsonObject2 = new JSONObject();
				JSONArray jsonArray1 = new JSONArray();
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject2 = (JSONObject) jsonArray.get(i);
					if (jsonObject2.get("name").equals(packetSectionName)) {
						jsonArray1.put(jsonObject2.get(Constants.REQ_DOC));
						break;
					}
				}
				if (!jsonArray1.isEmpty()) {
					JSONArray jsonArray3 = (JSONArray) jsonArray1.get(0);
					jsonObject2 = (JSONObject) jsonArray3.get(0);
					caseDetails.setRequirement_id((int) jsonObject2.get("id"));

				} else {
					paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
					dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
				}
			}
		} catch (Exception e) {
			paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
			dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
			System.out.println(e.getMessage());
		}

		return caseDetails;
	}

	/**
	 * Get the Case ID using Candidate name and template ID
	 * 
	 * @param paramVO
	 * @return caseID
	 */
	public int getCaseID(ParamVO paramVO) throws Exception {
		int result = 0;
		List<Integer> caseArrUsingCandName = new ArrayList<Integer>();
		List<Integer> caseArrUsingTempName = new ArrayList<Integer>();
		String request_verb = Constants.REQ_GET;
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		// Get the template name based on template ID
		String templateName = getTemplateName(paramVO);
		// Get the case ID based on candidate name as a search text
		String request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/packets"
				+ Constants.APICALLWITHSEARCHTEXT
				+ paramVO.getCandidate_first_name().replace(" ", Constants.REPLACE_WHITESPACE);
		String APIResult = getAPIResponse(request_string, request_verb, paramVO);
		if (APIResult != null) {
			jsonObject = new JSONObject(APIResult);
			jsonObject2 = new JSONObject();
			jsonArray = (JSONArray) jsonObject.get("results");
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObject2 = (JSONObject) jsonArray.get(i);
					caseArrUsingCandName.add((Integer) jsonObject2.get("id"));
				}
			} else {
				System.out.println("No results found in " + request_string);
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
				dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
			}
		}

		if (templateName != null && caseArrUsingCandName.size() > 0) {
			// Get the case ID based on template name as a search text
			request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/packets"
					+ Constants.APICALLWITHSEARCHTEXT + templateName;
			jsonObject = new JSONObject(getAPIResponse(request_string, request_verb, paramVO));
			jsonObject2 = new JSONObject();
			jsonArray = (JSONArray) jsonObject.get("results");
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject2 = (JSONObject) jsonArray.get(i);
				caseArrUsingTempName.add((Integer) jsonObject2.get("id"));
			}
			// Fetch the case ID that is common between the above two API calls
			caseArrUsingCandName.retainAll(caseArrUsingTempName);
			if (!caseArrUsingCandName.isEmpty()) {
				result = caseArrUsingCandName.get(0);
			} else {
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
				dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
			}
		}
		return result;
	}

	/**
	 * This method is used to make an API call with a GET request, just pass the
	 * request_string and the parameters
	 * 
	 * @param reqMethod
	 * @param requestVerb
	 * @param paramVO
	 * @return API response
	 */
	public String getAPIResponse(String reqMethod, String requestVerb, ParamVO paramVO) {
		String full_request = HMAC_Encryption.host + reqMethod + "";
		// System.out.println("Calling: " + full_request);
		HMAC_Encryption hmac = new HMAC_Encryption();
		StringBuilder sb = null;
		URL url;
		HttpURLConnection conn = null;
		BufferedReader rd = null;
		try {
			url = new URL(full_request);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Timestamp", hmac.timestampString());
			conn.setRequestProperty("Authorization", hmac.gen_HMAC(reqMethod, "", requestVerb));
			conn.setRequestProperty("INTF-DatabaseID", HMAC_Encryption.tenant_id);
			conn.setRequestMethod(requestVerb);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("UserVO-Agent", "Java client");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + reqMethod);
				dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
				throw new IOException(conn.getResponseMessage());
			}
			// Buffer the result into a string
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		String result = null;
		if (sb == null)
			result = null;
		else
			result = sb.toString();
		return result;
	}

	// Added by Kalmesh on 08/01/2021 for delete document
	public CaseDetailsVO getCaseDetailsForDelete(ParamVO paramVO) {
		CaseDetailsVO caseDetails = new CaseDetailsVO();
		String request_string = null;
		try {
			int caseID = getCaseID(paramVO);
			request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/packets/" + caseID;
			if (caseID > 0) {
				String request_verb = Constants.REQ_GET;
				JSONObject jsonObject;
				jsonObject = new JSONObject(getAPIResponse(request_string, request_verb, paramVO));
				JSONObject jsonObject1 = (JSONObject) jsonObject.get(Constants.PACKET);
				caseDetails.setApplicant_id((int) jsonObject1.get(Constants.APP_ID));
				JSONArray jsonArray = (JSONArray) jsonObject1.get("application_documents");
				JSONObject jsonObject2 = new JSONObject();

				if (!jsonArray.isEmpty()) {
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObject2 = (JSONObject) jsonArray.get(i);
						if (jsonObject2.get("name").equals(paramVO.getFile_name())) {
							caseDetails.setMedia_id((int) jsonObject2.get("media_id"));
						}
					}

				} else {
					paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
					dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
				}
			}
		} catch (Exception e) {
			paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
			dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
			System.out.println(e.getMessage());
		}

		return caseDetails;
	}

	// Added by Kalmesh on 08/01/2021 for delete document
	public String deleteFilesUploaded(int applicant_id, int media_id, ParamVO paramVO) {
		String request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/applicants/" + applicant_id
				+ "/on_behalf_documents/" + media_id;
		String requestVerb = Constants.REQ_DELETE;
		String response = getAPIResponseForDelete(request_string, requestVerb, paramVO);
		return response;
	}

	// Added by Kalmesh on 08/01/2021 for delete document
	private String getAPIResponseForDelete(String reqMethod, String requestVerb, ParamVO paramVO) {
		String full_request = HMAC_Encryption.host + reqMethod + "";
		HMAC_Encryption hmac = new HMAC_Encryption();
		URL url;
		HttpURLConnection conn = null;
		String result = null;
		try {
			url = new URL(full_request);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Timestamp", hmac.timestampString());
			conn.setRequestProperty("Authorization", hmac.gen_HMAC(reqMethod, "", requestVerb));
			conn.setRequestProperty("INTF-DatabaseID", HMAC_Encryption.tenant_id);
			conn.setRequestMethod(requestVerb);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("UserVO-Agent", "Java client");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() == 204) {
				result = "Success";
			} else {
				System.out.println(conn.getResponseCode());
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + reqMethod);
				dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
				throw new IOException(conn.getResponseMessage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;

	}

	public boolean checkFileInNetworkPath(ParamVO paramVO) throws IOException {
		URL url = null;
		InputStream inputStream = null;
		URLConnection conn = null;
		boolean success = false;
		try {
			url = new URL(paramVO.getNetworkPath() + paramVO.getFile_name());
			conn = url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestProperty("UserVO-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			inputStream = conn.getInputStream();
			if (inputStream != null) {
				success = true;
			}
		} catch (IOException e) {
			success = false;
		} finally {
			if (conn != null) {
				conn = null;
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return success;
	}

	public boolean checkIfCaseExists(ParamVO paramVO) {
		boolean result = false;
		String requestVerb = Constants.REQ_GET;
		String request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/packets"
				+ Constants.APICALLWITHSEARCHTEXT
				+ paramVO.getCandidate_first_name().replace(" ", Constants.REPLACE_WHITESPACE);
		String full_request = HMAC_Encryption.host + request_string + "";
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(full_request);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Timestamp", hmc_Encryption.timestampString());
			conn.setRequestProperty("Authorization", hmc_Encryption.gen_HMAC(request_string, "", requestVerb));
			conn.setRequestProperty("INTF-DatabaseID", HMAC_Encryption.tenant_id);
			conn.setRequestMethod(requestVerb);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("User-Agent", "Java client");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			System.out.println("Response Code : " + conn.getResponseCode());
			System.out.println("Response Message : " + conn.getResponseMessage());
			if (conn.getResponseCode() != 200) {
				System.out.println(conn.getResponseCode());
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + conn.getResponseCode());
				dao.updateErrorMsg(paramVO, Constants.FILE_UPLOAD_FAILED);
				throw new IOException(conn.getResponseMessage());
			} else {
				result = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return result;
	}

	public String createCase(ParamVO tmpVO) throws IOException {
		String request_string = "/byc-tenure/" + HMAC_Encryption.tenant_id + "/packets/create_from_template";
		String query_string = "";
		String request_verb = "POST";
		HMAC_Encryption hmac = new HMAC_Encryption();
		StringBuilder sb = null;
		HttpURLConnection conn = hmac.getConnection(request_string, query_string, request_verb);
		String result = "No records found to create a new case!!";
		try (OutputStream os = conn.getOutputStream()) {

			Map<Object, Object> jsonValues = new HashMap<Object, Object>();
			jsonValues.put("packet_id", tmpVO.getTemplate_id());
			jsonValues.put("unit_id", PacketDetails.getUnitID(tmpVO.getTemplate_id()));
			jsonValues.put("candidate_first_name", tmpVO.getCandidate_first_name());
			jsonValues.put("candidate_last_name", tmpVO.getCandidate_last_name());
			jsonValues.put("candidate_email", tmpVO.getCandidate_email()); //
			jsonValues.put("due_date", tmpVO.getDue_at());
			JSONObject parameters = new JSONObject(jsonValues);
			JSONObject payload = new JSONObject();
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
			dao.updateCaseCreateAuditFlag(tmpVO, Constants.CASE_CREATE_SUCCESS);
		}

		try {
			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			sb = new StringBuilder();

			while ((rd.readLine()) != null) {
				sb.append(rd.readLine());
			}
			rd.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		conn.disconnect();

		if (sb != null) {
			result = sb.toString();
		}
		return result;
	}
}
