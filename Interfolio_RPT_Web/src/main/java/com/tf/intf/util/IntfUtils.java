package com.tf.intf.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.google.common.collect.ObjectArrays;
import com.tf.intf.model.InputSourceVO;
import com.tf.intf.model.ParamVO;

@Component
public class IntfUtils {

	@Autowired
	HMAC_Encryption hmc_Encryption;
	
	private String product = "byc";

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

	public ParamVO getFileFromNetwork(ParamVO paramVO) {
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
			paramVO.setResult(true);
		} catch (IOException e) {
			paramVO.setResult(false);
			paramVO.setErrorMsg(url + " not found in the network path");
			paramVO.setAuditFlag(Constants.FILE_NOT_FOUND);
		}
		return paramVO;
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
	private ParamVO getTemplateName(ParamVO paramVO) throws Exception {
		String templateName = null;
		ParamVO param = null;
		String request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/packet_templates/"
				+ paramVO.getTemplate_id();
		String request_verb = Constants.REQ_GET;
		param = getAPIResponse(request_string, request_verb);
		if (param.getResponse() != null) {
			JSONObject jsonObject = new JSONObject(param.getResponse());
			JSONObject jsonObject1 = (JSONObject) jsonObject.get("packet_template");
			templateName = (String) jsonObject1.get("name");
			templateName = templateName.replace(" ", Constants.REPLACE_WHITESPACE);
			param.setTemplateName(templateName);
		} else {
			param.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
			param.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
		}
		return param;
	}

	/**
	 * Get the applicant ID and requirement section ID using case ID
	 * 
	 * @param paramVO
	 * @param packetSectionName
	 * @return requirement ID and applicant ID as part of the caseDetails object
	 */
	public ParamVO getCaseDetails(ParamVO paramVO, String packetSectionName) {
		String request_string = null;
		ParamVO param = null;
		try {
			param = getCaseID(paramVO);
			request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/packets/" + param.getCase_id();
			if (param.getCase_id() > 0) {
				String request_verb = Constants.REQ_GET;
				param = getAPIResponse(request_string, request_verb);
				String APIResult = param.getResponse();
				JSONObject jsonObject = new JSONObject(APIResult);
				JSONObject jsonObject1 = (JSONObject) jsonObject.get(Constants.PACKET);
				paramVO.setApplicant_id((int) jsonObject1.get(Constants.APP_ID));
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
					paramVO.setRequirement_id((int) jsonObject2.get("id"));
					paramVO.setResult(true);

				} else {
					paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
					paramVO.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
					paramVO.setResult(false);
				}
			}
		} catch (Exception e) {
			paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
			paramVO.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
			paramVO.setResult(false);
		}

		return paramVO;
	}

	/**
	 * Get the Case ID using Candidate name and template ID
	 * 
	 * @param paramVO
	 * @return caseID
	 */
	public ParamVO getCaseID(ParamVO paramVO) throws Exception {
		int result = 0;
		ParamVO param = null;
		String templateName = null;
		String APIResult = null;
		List<Integer> caseArrUsingCandName = new ArrayList<Integer>();
		List<Integer> caseArrUsingTempName = new ArrayList<Integer>();
		String request_verb = Constants.REQ_GET;
		JSONObject jsonObject = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		// Get the template name based on template ID
		param = getTemplateName(paramVO);
		templateName = param.getTemplateName();
		// Get the case ID based on candidate name as a search text
		String request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/packets"
				+ Constants.APICALLWITHSEARCHTEXT
				+ paramVO.getCandidate_first_name().replace(" ", Constants.REPLACE_WHITESPACE);
		param = getAPIResponse(request_string, request_verb);
		APIResult = param.getResponse();
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
				param.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
				param.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
			}
		}

		if (templateName != null && caseArrUsingCandName.size() > 1) {
			// Get the case ID based on template name as a search text
			request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/packets"
					+ Constants.APICALLWITHSEARCHTEXT + templateName;
			jsonObject = new JSONObject(getAPIResponse(request_string, request_verb));
			jsonObject2 = new JSONObject();
			jsonArray = (JSONArray) jsonObject.get("results");
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonObject2 = (JSONObject) jsonArray.get(i);
				caseArrUsingTempName.add((Integer) jsonObject2.get("id"));
			}
			// Fetch the case ID that is common between the above two API calls
			caseArrUsingCandName.retainAll(caseArrUsingTempName);
		}
		if (!caseArrUsingCandName.isEmpty()) {
			result = caseArrUsingCandName.get(0);
			param.setCase_id(result);
		} else {
			param.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
			param.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
		}
		return param;
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
	public ParamVO getAPIResponse(String reqMethod, String requestVerb) {
		String full_request = hmc_Encryption.getHost() + reqMethod + "";
		ParamVO param = new ParamVO();
		StringBuilder sb = null;
		URL url;
		HttpURLConnection conn = null;
		BufferedReader rd = null;
		try {
			url = new URL(full_request);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Timestamp", timestampString());
			conn.setRequestProperty("Authorization", hmc_Encryption.gen_HMAC(reqMethod, "", requestVerb));
			conn.setRequestProperty("INTF-DatabaseID", hmc_Encryption.getTenantId());
			conn.setRequestMethod(requestVerb);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("User-Agent", "Java client");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			if (conn.getResponseCode() != 200) {
				param.setErrorMsg(Constants.GENERICERRMESSAGE + reqMethod);
				param.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
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

		param.setResponse(result);
		return param;
	}

	public ParamVO getMediaId(ParamVO paramVO) {
		ParamVO param = null;
		String request_string = null;
		try {
			param = getCaseID(paramVO);
			request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/packets/" + param.getCase_id();
			if (param.getCase_id() > 0) {
				String request_verb = Constants.REQ_GET;
				param = getAPIResponse(request_string, request_verb);
				String APIResult = param.getResponse();
				JSONObject jsonObject = new JSONObject(APIResult);
				JSONObject jsonObject1 = (JSONObject) jsonObject.get(Constants.PACKET);
				param.setApplicant_id((int) jsonObject1.get(Constants.APP_ID));
				JSONArray jsonArray = (JSONArray) jsonObject1.get("application_documents");
				JSONObject jsonObject2 = new JSONObject();

				if (!jsonArray.isEmpty()) {
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObject2 = (JSONObject) jsonArray.get(i);
						if (jsonObject2.get("name").equals(paramVO.getFile_name())) {
							param.setMedia_id((int) jsonObject2.get("media_id"));
						}
					}

				} else {
					param.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
					param.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
				}
			}
		} catch (Exception e) {
			param.setErrorMsg(Constants.GENERICERRMESSAGE + request_string + " in getCaseDetails method");
			param.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
		}

		return param;
	}

	public ParamVO deleteFilesUploaded(int applicant_id, int media_id, ParamVO paramVO) {
		ParamVO param = null;
		String request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/applicants/" + applicant_id
				+ "/on_behalf_documents/" + media_id;
		String requestVerb = Constants.REQ_DELETE;
		param = deleteAPICall(request_string, requestVerb, paramVO);
		return param;
	}

	private ParamVO deleteAPICall(String reqMethod, String requestVerb, ParamVO paramVO) {
		ParamVO param = new ParamVO();
		String full_request = hmc_Encryption.getHost() + reqMethod + "";
		HMAC_Encryption hmac = new HMAC_Encryption();
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(full_request);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Timestamp", timestampString());
			conn.setRequestProperty("Authorization", hmac.gen_HMAC(reqMethod, "", requestVerb));
			conn.setRequestProperty("INTF-DatabaseID", hmc_Encryption.getTenantId());
			conn.setRequestMethod(requestVerb);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("UserVO-Agent", "Java client");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() == 204) {
				param.setResult(true);
			} else {
				param.setResult(false);
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + reqMethod);
				throw new IOException(conn.getResponseMessage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return param;

	}

	public ParamVO checkFileInNetworkPath(List<String> fileNames, ParamVO paramVO) throws IOException {
		if (fileNames.contains(paramVO.getFile_name())) {
			paramVO.setResult(true);
		} else {
			paramVO.setResult(false);
		}
		return paramVO;

	}

	public ParamVO checkIfCaseExists(ParamVO paramVO) {
		ParamVO param = null;
		String requestVerb = Constants.REQ_GET;
		String request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/packets"
				+ Constants.APICALLWITHSEARCHTEXT
				+ paramVO.getCandidate_first_name().replace(" ", Constants.REPLACE_WHITESPACE);
		param = getAPIResponse(request_string, requestVerb);
		String APIResult = param.getResponse();
		JSONObject jsonObject = null;
		JSONArray jsonArray = new JSONArray();
		if (APIResult != null) {
			jsonObject = new JSONObject(APIResult);
			jsonArray = (JSONArray) jsonObject.get("results");
			if (jsonArray.length() > 0) {
				paramVO.setResult(true);
				paramVO.setErrorMsg(null);
			} else {
				paramVO.setAuditFlag(Constants.FILE_UPLOAD_FAILED);
				paramVO.setResult(false);
				paramVO.setErrorMsg(Constants.GENERICERRMESSAGE + request_string);
			}
		}
		return paramVO;
	}

	public ParamVO createCase(ParamVO paramVO) throws IOException {
		String request_string = "/byc-tenure/" + hmc_Encryption.getTenantId() + "/packets/create_from_template";
		String query_string = "";
		String request_verb = Constants.REQ_POST;
		HttpURLConnection conn = hmc_Encryption.getConnection(request_string, query_string, request_verb);
		try (OutputStream os = conn.getOutputStream()) {
			Map<Object, Object> jsonValues = new HashMap<Object, Object>();
			jsonValues.put("packet_id", paramVO.getTemplate_id());
			jsonValues.put("unit_id", getUnitID(paramVO.getTemplate_id()));
			jsonValues.put("candidate_first_name", paramVO.getCandidate_first_name());
			jsonValues.put("candidate_last_name", paramVO.getCandidate_last_name());
			jsonValues.put("candidate_email", paramVO.getCandidate_email()); //
			jsonValues.put("due_date", paramVO.getDue_at());
			JSONObject parameters = new JSONObject(jsonValues);
			JSONObject payload = new JSONObject();
			payload.put("packet", parameters);
			os.write(payload.toString().getBytes());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			paramVO.setResult(false);
			paramVO.setErrorMsg("Case creation failed");
		}
		if (conn.getResponseCode() == 201) {
			paramVO.setResult(true);
			paramVO.setErrorMsg("Success");
		} else {
			paramVO.setResult(false);
			paramVO.setErrorMsg("Case creation failed");
		}
		return paramVO;
	}

	public List<String> getFilesFromNetworkPath() throws IOException {
		String NETWORKPATH = "\\\\Ccsoq3\\soqfiles$";
		String NETWORKPATH_GRADED = "\\\\erpbiprd3\\c$\\BurstDestFolder";
		File file = new File(NETWORKPATH);
		File file1 = new File(NETWORKPATH_GRADED);
		String[] fileList1 = file.list();
		String[] fileList2 = file1.list();
		String[] fileList = ObjectArrays.concat(fileList1, fileList2, String.class);
		List<String> fileNames = Arrays.asList(fileList);
		return fileNames;
	}

	public File decodeToFile(String inputString) {
		byte[] fileByte = null;
		File filePath = null;
		try {
			fileByte = Base64.getDecoder().decode(inputString);
			String path = Constants.LOCALFILEPATH + "InputData" + ".xlsx";
			filePath = new File(path);
			OutputStream os = new FileOutputStream(filePath);
			FileCopyUtils.copy(fileByte, os);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	@SuppressWarnings("resource")
	public List<InputSourceVO> parseInputFile(File file) {
		List<InputSourceVO> inputSourceList = new ArrayList<InputSourceVO>();
		try {
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fis);
			Sheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = firstSheet.iterator();
			InputSourceVO inputSource = null;
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				if (nextRow.getRowNum() != 0) {
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					inputSource = new InputSourceVO();
					while (cellIterator.hasNext()) {
						Cell nextCell = cellIterator.next();
						if (nextCell.getColumnIndex() == 0) {
							if (nextCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								double d = nextCell.getNumericCellValue();
								int value = (int) d;
								inputSource.setCwid(String.valueOf(value));
							} else {
								inputSource.setCwid(nextCell.getStringCellValue());
							}
						} else if (nextCell.getColumnIndex() == 1) {
							if (nextCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								double d = nextCell.getNumericCellValue();
								int value = (int) d;
								inputSource.setTemplate_id(String.valueOf(value));
							} else {
								inputSource.setTemplate_id(nextCell.getStringCellValue());
							}
						} else if (nextCell.getColumnIndex() == 2) {
							inputSource.setReview_term(nextCell.getStringCellValue());
						} else if (nextCell.getColumnIndex() == 3) {
							double d = nextCell.getNumericCellValue();
							inputSource.setTenure((int) d);
						}
					}
					inputSourceList.add(inputSource);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (file.exists()) {
				file.delete();
			}
		}
		return inputSourceList;
	}
	
	public int getUnitID(String query_string) {
		int unit_id = 0;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(getResults(query_string));
			JSONObject jsonObject1 = (JSONObject) jsonObject.get("packet_template");
			unit_id = (int) jsonObject1.get("unit_id");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return unit_id;
	}
	
	private String getResults(String query_string) throws IOException {
		String host = hmc_Encryption.getHost();
		String tenant_id = hmc_Encryption.getTenantId();
		String requestString = "/byc-tenure/" + tenant_id + "/packet_templates/";
		String full_request = host + requestString + query_string;
		
		URL url = new URL(full_request);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Timestamp", timestampString());
		conn.setRequestProperty("Authorization", gen_HMAC(query_string, tenant_id));
		conn.setRequestProperty("INTF-DatabaseID", tenant_id);

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		return sb.toString();
	}
	
	private String gen_HMAC(String query_string, String tenant_id) {
		String HMAC_request_string = "/byc-tenure/" + tenant_id + "/packet_templates/";
		if (product == HMAC_Encryption.product) {
			HMAC_request_string = HMAC_request_string + query_string;
		}

		String verbReq = Constants.REQ_GET + "\n\n\n" + timestampString() + "\n" + HMAC_request_string;
		try {
			Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
			SecretKeySpec secret_key = new SecretKeySpec(hmc_Encryption.getPrivateKey().getBytes(), "HmacSHA1");
			sha1_HMAC.init(secret_key);
			String output_hash = org.apache.commons.codec.binary.Base64
					.encodeBase64String(sha1_HMAC.doFinal(verbReq.getBytes()));

			String authorization_header = "INTF " + hmc_Encryption.getPublicKey() + output_hash;

			return authorization_header;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
	}
}
