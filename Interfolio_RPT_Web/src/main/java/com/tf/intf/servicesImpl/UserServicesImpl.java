package com.tf.intf.servicesImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tf.intf.DAO.DAO;
import com.tf.intf.model.InputSourceVO;
import com.tf.intf.model.ParamVO;
import com.tf.intf.model.TemplateVO;
import com.tf.intf.services.UserServices;
import com.tf.intf.util.Constants;
import com.tf.intf.util.HMAC_Encryption;
import com.tf.intf.util.IntfUtils;

@Service
public class UserServicesImpl implements UserServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServicesImpl.class);

	@Autowired
	HMAC_Encryption hmc_Encryption;

	@Autowired
	IntfUtils intfUtils;

	@Autowired
	DAO dao;

	@Override
	public String createCase() {
		long startTime = new Date().getTime();
		LOGGER.info("Create case process started.");
		String result = null;
		ParamVO paramVO = null;
		int paramListSize = 0;
		List<ParamVO> paramList = dao.getCaseCreateData(Constants.FILE_TO_UPLOAD);
		if (paramList != null && paramList.size() != 0) {
			paramListSize = paramList.size();
			for (ParamVO tmpVO : paramList) {
				try {
					paramVO = intfUtils.createCase(tmpVO);
					if (paramVO.getResult()) {
						dao.updateCaseCreateAuditFlag(paramVO, Constants.CASE_CREATE_SUCCESS);
						result = paramVO.getErrorMsg();
					} else {
						result = paramVO.getErrorMsg();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			result = "No records to process";
		}
		LOGGER.info("Create case process completed.");
		long endTime = new Date().getTime();
		long finalTime = (endTime - startTime);
		long diffSeconds = finalTime / 1000 % 60;
		long diffMinutes = finalTime / (60 * 1000) % 60;
		long diffHours = finalTime / (60 * 60 * 1000) % 24;
		LOGGER.info("Process took " + diffHours + ":" + diffMinutes + ":" + diffSeconds + " time to complete for "
				+ paramListSize + " " + "records.");
		return result;
	}

	public List<TemplateVO> getTemplateId() {
		//Map<Object, Object> map = new HashMap<Object, Object>();
		List<TemplateVO> templateVO = dao.getTemplateId();
		//map.put("Template", templateVO);
		return templateVO;
	}

	public String uploadGradedClassReport() {
		long startTime = new Date().getTime();
		LOGGER.info("File upload process for GRADED_CLASS_FILE_TYPE started.");
		OkHttpClient client = new OkHttpClient();
		String response_result = null;
		String request_verb = Constants.REQ_POST;
		String fileUploadPath = null;
		Request request;
		int applicant_id = 0;
		Integer requirement_id = 0;
		String request_string = null;
		File file = null;
		Response response = null;
		RequestBody body = null;
		ParamVO param = null;
		int paramListSize = 0;
		try {
			List<ParamVO> paramList = dao.getSOQFileDetails(Constants.FILE_TO_UPLOAD, Constants.GRADED_CLASS_FILE_TYPE);
			if (paramList != null && paramList.size() != 0) {
				paramListSize = paramList.size();
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					param = intfUtils.getCaseDetails(tmpVO, Constants.GRADED_CLASS_LIST_SEC_NAME);
					if (param.getRequirement_id() != null) {
						applicant_id = param.getApplicant_id();
						requirement_id = param.getRequirement_id();
						request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/applicants/"
								+ applicant_id + "/on_behalf_documents";
						tmpVO.setReqString(request_string);
						tmpVO.setNetworkPath(Constants.NETWORKPATH_GRADED);
						param = intfUtils.getFileFromNetwork(tmpVO);
						if (param.getResult()) {
							fileUploadPath = Constants.FILESEPERATOR + Constants.LOCALFILEPATH + tmpVO.getFile_name();

							body = new MultipartBody.Builder().setType(MultipartBody.FORM)
									.addFormDataPart(Constants.TITLE, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													tmpVO.getFile_name().getBytes()))
									.addFormDataPart(Constants.FILE, fileUploadPath,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_STREAM),
													new File(fileUploadPath)))
									.addFormDataPart(Constants.DOCFORMAT, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													"PDF".getBytes()))
									.addFormDataPart(Constants.REQID, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													requirement_id.toString()))
									.addFormDataPart(Constants.DOCTYPE, null, RequestBody.create(
											MediaType.parse(Constants.CONTENTTYPE_JSON), "TestUpload".getBytes()))
									.build();

							request = new Request.Builder().url(hmc_Encryption.getHost() + request_string)
									.method(request_verb, body)
									.addHeader(Constants.TIMESTAMP, intfUtils.timestampString())
									.addHeader(Constants.AUTHORIZATION,
											hmc_Encryption.gen_HMAC(request_string, "", request_verb))
									.addHeader(Constants.DATABASE_ID, hmc_Encryption.getTenantId()).build();

							response = client.newCall(request).execute();
							if (response.code() == 201) {
								dao.updateSOQAuditFlg(tmpVO, Constants.FILE_UPLOAD_SUCCESS);
								file = new File(Constants.LOCALFILEPATH + tmpVO.getFile_name());
								response_result = "Success";
								LOGGER.info(tmpVO.getFile_name() + " - is successfully uploaded to "
										+ tmpVO.getCandidate_first_name());
								if (file.exists()) {
									file.delete();
								}
							} else {
								LOGGER.info("Failed to upload file - " + param.getFile_name());
								param.setErrorMsg("Failed to upload file - " + param.getFile_name());
								dao.updateErrorMsg(param, Constants.FILE_UPLOAD_FAILED);
							}
							if (response != null) {
								response.body().close();
								response.close();
							}
						} else {
							LOGGER.info("File not found in network path - " + param.getFile_name());
							dao.updateErrorMsg(param, param.getAuditFlag());
						}
					} else {
						LOGGER.info("No case found for - " + param.getCandidate_first_name() + " "
								+ param.getCandidate_last_name());
						dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		LOGGER.info("File upload process for GRADED_CLASS_FILE_TYPE completed");
		long endTime = new Date().getTime();
		long finalTime = (endTime - startTime);
		long diffSeconds = finalTime / 1000 % 60;
		long diffMinutes = finalTime / (60 * 1000) % 60;
		long diffHours = finalTime / (60 * 60 * 1000) % 24;
		LOGGER.info("Process took " + diffHours + ":" + diffMinutes + ":" + diffSeconds + " time to complete for "
				+ paramListSize + " " + "records.");

		return response_result;
	}

	public String uploadSOQCommentsFile() {
		long startTime = new Date().getTime();
		LOGGER.info("File upload process for SOQ_COMM_FILE_TYPE started.");
		OkHttpClient client = new OkHttpClient();
		String response_result = null;
		String request_verb = Constants.REQ_POST;
		String fileUploadPath = null;
		Request request;
		int applicant_id = 0;
		Integer requirement_id = 0;
		String request_string = null;
		File file = null;
		Response response = null;
		RequestBody body = null;
		ParamVO param = null;
		int paramListSize = 0;
		try {
			List<ParamVO> paramList = dao.getSOQFileDetails(Constants.FILE_TO_UPLOAD, Constants.SOQ_COMM_FILE_TYPE);
			if (paramList != null && paramList.size() != 0) {
				paramListSize = paramList.size();
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					param = intfUtils.getCaseDetails(tmpVO, Constants.SOQ_COMMENTS_SECTION_NAME);
					if (param.getRequirement_id() != null) {
						applicant_id = param.getApplicant_id();
						requirement_id = param.getRequirement_id();
						request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/applicants/"
								+ applicant_id + "/on_behalf_documents";
						tmpVO.setReqString(request_string);
						tmpVO.setNetworkPath(Constants.NETWORKPATH);
						param = intfUtils.getFileFromNetwork(tmpVO);
						if (param.getResult()) {
							fileUploadPath = Constants.FILESEPERATOR + Constants.LOCALFILEPATH + tmpVO.getFile_name();

							body = new MultipartBody.Builder().setType(MultipartBody.FORM)
									.addFormDataPart(Constants.TITLE, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													tmpVO.getFile_name().getBytes()))
									.addFormDataPart(Constants.FILE, fileUploadPath,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_STREAM),
													new File(fileUploadPath)))
									.addFormDataPart(Constants.DOCFORMAT, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													"PDF".getBytes()))
									.addFormDataPart(Constants.REQID, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													requirement_id.toString()))
									.addFormDataPart(Constants.DOCTYPE, null, RequestBody.create(
											MediaType.parse(Constants.CONTENTTYPE_JSON), "TestUpload".getBytes()))
									.build();

							request = new Request.Builder().url(hmc_Encryption.getHost() + request_string)
									.method(request_verb, body)
									.addHeader(Constants.TIMESTAMP, intfUtils.timestampString())
									.addHeader(Constants.AUTHORIZATION,
											hmc_Encryption.gen_HMAC(request_string, "", request_verb))
									.addHeader(Constants.DATABASE_ID, hmc_Encryption.getTenantId()).build();

							response = client.newCall(request).execute();
							if (response.code() == 201) {
								dao.updateSOQAuditFlg(tmpVO, Constants.FILE_UPLOAD_SUCCESS);
								file = new File(Constants.LOCALFILEPATH + tmpVO.getFile_name());
								response_result = "Success";
								LOGGER.info(tmpVO.getFile_name() + " - is successfully uploaded to "
										+ tmpVO.getCandidate_first_name());
								if (file.exists()) {
									file.delete();
								}
							} else {
								LOGGER.info("Failed to upload file - " + param.getFile_name());
								param.setErrorMsg("Failed to upload file - " + param.getFile_name());
								dao.updateErrorMsg(param, Constants.FILE_UPLOAD_FAILED);
							}
							if (response != null) {
								response.body().close();
								response.close();
							}
						} else {
							LOGGER.info("File not found in network path - " + param.getFile_name());
							dao.updateErrorMsg(param, param.getAuditFlag());
						}
					} else {
						LOGGER.info("No case found for - " + param.getCandidate_first_name() + " "
								+ param.getCandidate_last_name());
						dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		LOGGER.info("File upload process for SOQ_COMM_FILE_TYPE completed.");
		long endTime = new Date().getTime();
		long finalTime = (endTime - startTime);
		long diffSeconds = finalTime / 1000 % 60;
		long diffMinutes = finalTime / (60 * 1000) % 60;
		long diffHours = finalTime / (60 * 60 * 1000) % 24;
		LOGGER.info("Process took " + diffHours + ":" + diffMinutes + ":" + diffSeconds + " time to complete for "
				+ paramListSize + " " + "records.");

		return response_result;
	}

	public String uploadSOQStatsFile() {
		long startTime = new Date().getTime();
		LOGGER.info("File upload process for SOQ_STAT_FILE_TYPE started.");
		OkHttpClient client = new OkHttpClient();
		String response_result = null;
		String request_verb = Constants.REQ_POST;
		String fileUploadPath = null;
		Request request;
		int applicant_id = 0;
		Integer requirement_id = 0;
		String request_string = null;
		File file = null;
		Response response = null;
		RequestBody body = null;
		ParamVO param = null;
		int paramListSize = 0;
		try {
			List<ParamVO> paramList = dao.getSOQFileDetails(Constants.FILE_TO_UPLOAD, Constants.SOQ_STAT_FILE_TYPE);
			if (paramList != null && paramList.size() != 0) {
				paramListSize = paramList.size();
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					param = intfUtils.getCaseDetails(tmpVO, Constants.SOQ_STATS_SECTION_NAME);
					if (param.getRequirement_id() != null) {
						applicant_id = param.getApplicant_id();
						requirement_id = param.getRequirement_id();
						request_string = Constants.APICALLPART1 + hmc_Encryption.getTenantId() + "/applicants/"
								+ applicant_id + "/on_behalf_documents";
						tmpVO.setReqString(request_string);
						tmpVO.setNetworkPath(Constants.NETWORKPATH);
						param = intfUtils.getFileFromNetwork(tmpVO);
						if (param.getResult()) {
							fileUploadPath = Constants.FILESEPERATOR + Constants.LOCALFILEPATH + tmpVO.getFile_name();

							body = new MultipartBody.Builder().setType(MultipartBody.FORM)
									.addFormDataPart(Constants.TITLE, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													tmpVO.getFile_name().getBytes()))
									.addFormDataPart(Constants.FILE, fileUploadPath,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_STREAM),
													new File(fileUploadPath)))
									.addFormDataPart(Constants.DOCFORMAT, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													"PDF".getBytes()))
									.addFormDataPart(Constants.REQID, null,
											RequestBody.create(MediaType.parse(Constants.CONTENTTYPE_JSON),
													requirement_id.toString()))
									.addFormDataPart(Constants.DOCTYPE, null, RequestBody.create(
											MediaType.parse(Constants.CONTENTTYPE_JSON), "TestUpload".getBytes()))
									.build();

							request = new Request.Builder().url(hmc_Encryption.getHost() + request_string)
									.method(request_verb, body)
									.addHeader(Constants.TIMESTAMP, intfUtils.timestampString())
									.addHeader(Constants.AUTHORIZATION,
											hmc_Encryption.gen_HMAC(request_string, "", request_verb))
									.addHeader(Constants.DATABASE_ID, hmc_Encryption.getTenantId()).build();

							response = client.newCall(request).execute();
							if (response.code() == 201) {
								dao.updateSOQAuditFlg(tmpVO, Constants.FILE_UPLOAD_SUCCESS);
								file = new File(Constants.LOCALFILEPATH + tmpVO.getFile_name());
								response_result = "Success";
								LOGGER.info(tmpVO.getFile_name() + " - is successfully uploaded to "
										+ tmpVO.getCandidate_first_name());
								if (file.exists()) {
									file.delete();
								}
							} else {
								LOGGER.info("Failed to upload file - " + param.getFile_name());
								param.setErrorMsg("Failed to upload file - " + param.getFile_name());
								dao.updateErrorMsg(param, Constants.FILE_UPLOAD_FAILED);
							}
							if (response != null) {
								response.body().close();
								response.close();
							}
						} else {
							LOGGER.info("File not found in network path - " + param.getFile_name());
							dao.updateErrorMsg(param, param.getAuditFlag());
						}
					} else {
						LOGGER.info("No case found for - " + param.getCandidate_first_name() + " "
								+ param.getCandidate_last_name());
						dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		LOGGER.info("File upload process for SOQ_STAT_FILE_TYPE completed.");
		long endTime = new Date().getTime();
		long finalTime = (endTime - startTime);
		long diffSeconds = finalTime / 1000 % 60;
		long diffMinutes = finalTime / (60 * 1000) % 60;
		long diffHours = finalTime / (60 * 60 * 1000) % 24;
		LOGGER.info("Process took " + diffHours + ":" + diffMinutes + ":" + diffSeconds + " time to complete for "
				+ paramListSize + " " + "records.");

		return response_result;
	}

	public String deleteUploadedFile() {
		long startTime = new Date().getTime();
		LOGGER.info("Delete process for uploaded files started.");
		String response = null;
		int applicant_id = 0;
		int media_id = 0;
		ParamVO param = null;
		int paramListSize = 0;
		try {
			List<ParamVO> paramList = dao.getSOQFilesFromDataBase(Constants.FILE_TO_DELETE);
			if (paramList != null && paramList.size() != 0) {
				paramListSize = paramList.size();
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					param = intfUtils.getMediaId(tmpVO);
					if (param.getMedia_id() > 0) {
						applicant_id = param.getApplicant_id();
						media_id = param.getMedia_id();
						param = intfUtils.deleteFilesUploaded(applicant_id, media_id, tmpVO);
						if (param.getResult()) {
							dao.updateSOQAuditFlg(tmpVO, Constants.FILE_TO_UPLOAD);
							LOGGER.info("Delete process is completed for file : " + tmpVO.getFile_name());
						} else {
							dao.updateErrorMsg(param, Constants.FILE_UPLOAD_FAILED);
						}
					} else {
						dao.updateErrorMsg(param, Constants.FILE_UPLOAD_FAILED);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		LOGGER.info("Delete process for uploaded files completed.");
		long endTime = new Date().getTime();
		long finalTime = (endTime - startTime);
		long diffSeconds = finalTime / 1000 % 60;
		long diffMinutes = finalTime / (60 * 1000) % 60;
		long diffHours = finalTime / (60 * 60 * 1000) % 24;
		LOGGER.info("Process took " + diffHours + ":" + diffMinutes + ":" + diffSeconds + " time to complete for "
				+ paramListSize + " " + "records.");

		return response;
	}

	public String caseAndFileValidation() {
		long startTime = new Date().getTime();
		LOGGER.info("Case and File validation process started.");
		String response = "Success";
		List<ParamVO> param_List = new ArrayList<ParamVO>();
		ParamVO paramVO = null;
		int paramListSize = 0;
		try {
			List<String> fileNames = intfUtils.getFilesFromNetworkPath();
			List<ParamVO> paramList = dao.getSOQFilesFromDataBase(Constants.FILE_TO_UPLOAD);
			if (paramList != null && paramList.size() != 0) {
				paramListSize = paramList.size();
				for (ParamVO tmpVO : paramList) {
					paramVO = new ParamVO();
					paramVO.setProcessDate(intfUtils.getSysdate());
					paramVO = intfUtils.checkIfCaseExists(tmpVO);
					if (paramVO.getResult()) {
						tmpVO.setNetworkPath(Constants.NETWORKPATH);
						paramVO = intfUtils.checkFileInNetworkPath(fileNames, tmpVO);
						if (!paramVO.getResult()) {
							paramVO.setAuditFlag(Constants.FILE_NOT_FOUND);
							paramVO.setErrorMsg(tmpVO.getFile_name() + " - " + "not found in network path");
							LOGGER.info(tmpVO.getFile_name() + " - " + "not found in network path.");
						}
					} else {
						LOGGER.info("No case found for - " + paramVO.getCandidate_first_name());
					}
					param_List.add(paramVO);
				}
				dao.batchUpdateSOQAuditFlag(param_List);
			}
		} catch (Exception e) {
			response = null;
			e.printStackTrace();
		}
		LOGGER.info("Case and File validation process completed.");
		long endTime = new Date().getTime();
		long finalTime = (endTime - startTime);
		long diffSeconds = finalTime / 1000 % 60;
		long diffMinutes = finalTime / (60 * 1000) % 60;
		long diffHours = finalTime / (60 * 60 * 1000) % 24;

		LOGGER.info("Process took " + diffHours + ":" + diffMinutes + ":" + diffSeconds + " time to complete for "
				+ paramListSize + " " + "records.");
		return response;
	}

	@Override
	public String uploadCSVFile(ParamVO param) {
		String response = null;
		String inputString = param.getFile_data();
		File file = null;
		List<InputSourceVO> listInputSourceVO = null;
		if (inputString != null) {
			file = intfUtils.decodeToFile(inputString);
			if (file.getName() != null) {
				listInputSourceVO = intfUtils.parseInputFile(file);
				if (listInputSourceVO != null && listInputSourceVO.size() != 0) {
					try {
						dao.deleteRecords();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					dao.createDataFromInputSource(listInputSourceVO);
					response = "Success";

				} else {
					response = "Fail";
				}
			} else {
				response = "Fail";
			}
		} else {
			response = "Fail";
		}
		return response;
	}
}
