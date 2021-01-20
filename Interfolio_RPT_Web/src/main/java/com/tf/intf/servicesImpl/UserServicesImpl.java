package com.tf.intf.servicesImpl;

import java.io.IOException;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tf.intf.DAO.DAO;
import com.tf.intf.model.CaseDetailsVO;
import com.tf.intf.model.ParamVO;
import com.tf.intf.model.TemplateVO;
import com.tf.intf.services.UserServices;
import com.tf.intf.util.Constants;
import com.tf.intf.util.HMAC_Encryption;
import com.tf.intf.util.IntfUtils;

@Service
public class UserServicesImpl implements UserServices {

	@Autowired
	HMAC_Encryption hmc_Encryption;

	@Autowired
	IntfUtils intfUtils;

	@Autowired
	DAO dao;

	@Override
	public String createCase(String template_id) throws IOException {
		String result = null;
		List<ParamVO> paramList = dao.getCaseCreateData(Constants.FILE_TO_UPLOAD);
		if (paramList != null && paramList.size() != 3) {
			for (ParamVO tmpVO : paramList) {
				result = intfUtils.createCase(tmpVO);
			}
		}
		return result;
	}

	public Map<Object, Object> getTemplateId() {
		Map<Object, Object> map = new HashMap<Object, Object>();
		List<TemplateVO> templateVO = dao.getTemplateId();
		map.put("TemplateID", templateVO);
		return map;
	}

	// Upload file for graded class report
	public String uploadGradedClassReport() {
		OkHttpClient client = new OkHttpClient();
		String response_result = null;
		String request_verb = Constants.REQ_POST;
		String fileUploadPath = null;
		Request request;
		CaseDetailsVO caseDetails = null;
		int applicant_id = 0;
		String request_string = null;
		File file = null;
		Response response = null;
		RequestBody body = null;
		try {
			List<ParamVO> paramList = dao.getSOQFileDetails(Constants.FILE_TO_UPLOAD, Constants.SOQ_COMM_FILE_TYPE);
			if (paramList != null && paramList.size() != 0) {
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					tmpVO.setNetworkPath(Constants.NETWORKPATH_GRADED);
					intfUtils.getFileFromNetwork(tmpVO);
					fileUploadPath = Constants.FILESEPERATOR + Constants.LOCALFILEPATH + tmpVO.getFile_name();
					caseDetails = intfUtils.getCaseDetails(tmpVO, Constants.GRADED_CLASS_LIST_SEC_NAME);
					if (caseDetails.getRequirement_id() != null) {
						applicant_id = caseDetails.getApplicant_id();
						// System.out.println("Applicant Id : "+applicant_id);
						request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/applicants/"
								+ applicant_id + "/on_behalf_documents";
						tmpVO.setReqString(request_string);

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
												caseDetails.getRequirement_id().toString()))
								.addFormDataPart(Constants.DOCTYPE, null, RequestBody
										.create(MediaType.parse(Constants.CONTENTTYPE_JSON), "TestUpload".getBytes()))
								.build();

						request = new Request.Builder().url(HMAC_Encryption.host + request_string)
								.method(request_verb, body)
								.addHeader(Constants.TIMESTAMP, hmc_Encryption.timestampString())
								.addHeader(Constants.AUTHORIZATION,
										hmc_Encryption.gen_HMAC(request_string, "", request_verb))
								.addHeader(Constants.DATABASE_ID, HMAC_Encryption.tenant_id).build();

						response = client.newCall(request).execute();
						if (response.code() == 201) {
							dao.updateSOQAuditFlg(tmpVO, Constants.FILE_UPLOAD_SUCCESS);
							file = new File(Constants.LOCALFILEPATH + tmpVO.getFile_name());
							response_result = "Success";
							System.out.println(tmpVO.getFile_name() + " - is successfully uploaded to "
									+ tmpVO.getCandidate_first_name());
							if (file.exists()) {
								file.delete();
								// System.out.println(tmpVO.getFile_name()+" - is being deleted");
							}
						} else {
							tmpVO.setErrorMsg(Constants.GENERICERRMESSAGE + tmpVO.getReqString());
							dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
						}
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
		return response_result;
	}

	public String uploadSOQCommentsFile() {
		OkHttpClient client = new OkHttpClient();
		String response_result = null;
		String request_verb = Constants.REQ_POST;
		String fileUploadPath = null;
		Request request;
		CaseDetailsVO caseDetails = null;
		int applicant_id = 0;
		String request_string = null;
		File file = null;
		Response response = null;
		RequestBody body = null;
		try {
			List<ParamVO> paramList = dao.getSOQFileDetails(Constants.FILE_TO_UPLOAD, Constants.SOQ_COMM_FILE_TYPE);
			if (paramList != null && paramList.size() != 0) {

				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					tmpVO.setNetworkPath(Constants.NETWORKPATH);
					intfUtils.getFileFromNetwork(tmpVO);
					fileUploadPath = Constants.FILESEPERATOR + Constants.LOCALFILEPATH + tmpVO.getFile_name();
					caseDetails = intfUtils.getCaseDetails(tmpVO, Constants.SOQ_COMMENTS_SECTION_NAME);
					if (caseDetails.getRequirement_id() != null) {
						applicant_id = caseDetails.getApplicant_id();
						// System.out.println("Applicant Id : "+applicant_id);
						request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/applicants/"
								+ applicant_id + "/on_behalf_documents";
						tmpVO.setReqString(request_string);

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
												caseDetails.getRequirement_id().toString()))
								.addFormDataPart(Constants.DOCTYPE, null, RequestBody
										.create(MediaType.parse(Constants.CONTENTTYPE_JSON), "TestUpload".getBytes()))
								.build();

						request = new Request.Builder().url(HMAC_Encryption.host + request_string)
								.method(request_verb, body)
								.addHeader(Constants.TIMESTAMP, hmc_Encryption.timestampString())
								.addHeader(Constants.AUTHORIZATION,
										hmc_Encryption.gen_HMAC(request_string, "", request_verb))
								.addHeader(Constants.DATABASE_ID, HMAC_Encryption.tenant_id).build();

						response = client.newCall(request).execute();
						if (response.code() == 201) {
							dao.updateSOQAuditFlg(tmpVO, Constants.FILE_UPLOAD_SUCCESS);
							file = new File(Constants.LOCALFILEPATH + tmpVO.getFile_name());
							response_result = "Success";
							System.out.println(tmpVO.getFile_name() + " - is successfully uploaded to "
									+ tmpVO.getCandidate_first_name());
							if (file.exists()) {
								file.delete();
								// System.out.println(tmpVO.getFile_name()+" - is being deleted");
							}
						} else {
							tmpVO.setErrorMsg(Constants.GENERICERRMESSAGE + tmpVO.getReqString());
							dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
						}
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
		return response_result;
	}

	public String uploadSOQStatsFile() {
		OkHttpClient client = new OkHttpClient();
		String response_result = null;
		String request_verb = Constants.REQ_POST;
		String fileUploadPath = null;
		Request request;
		CaseDetailsVO caseDetails = null;
		int applicant_id = 0;
		String request_string = null;
		File file = null;
		Response response = null;
		RequestBody body = null;
		try {
			List<ParamVO> paramList = dao.getSOQFileDetails(Constants.FILE_TO_UPLOAD, Constants.SOQ_STAT_FILE_TYPE);
			if (paramList != null && paramList.size() != 0) {
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					tmpVO.setNetworkPath(Constants.NETWORKPATH);
					intfUtils.getFileFromNetwork(tmpVO);
					fileUploadPath = Constants.FILESEPERATOR + Constants.LOCALFILEPATH + tmpVO.getFile_name();
					caseDetails = intfUtils.getCaseDetails(tmpVO, Constants.SOQ_STATS_SECTION_NAME);
					if (caseDetails.getRequirement_id() != null) {
						applicant_id = caseDetails.getApplicant_id();
						// System.out.println("Applicant Id : "+applicant_id);
						request_string = Constants.APICALLPART1 + HMAC_Encryption.tenant_id + "/applicants/"
								+ applicant_id + "/on_behalf_documents";
						tmpVO.setReqString(request_string);

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
												caseDetails.getRequirement_id().toString()))
								.addFormDataPart(Constants.DOCTYPE, null, RequestBody
										.create(MediaType.parse(Constants.CONTENTTYPE_JSON), "TestUpload".getBytes()))
								.build();

						request = new Request.Builder().url(HMAC_Encryption.host + request_string)
								.method(request_verb, body)
								.addHeader(Constants.TIMESTAMP, hmc_Encryption.timestampString())
								.addHeader(Constants.AUTHORIZATION,
										hmc_Encryption.gen_HMAC(request_string, "", request_verb))
								.addHeader(Constants.DATABASE_ID, HMAC_Encryption.tenant_id).build();

						response = client.newCall(request).execute();
						if (response.code() == 201) {
							dao.updateSOQAuditFlg(tmpVO, Constants.FILE_UPLOAD_SUCCESS);
							file = new File(Constants.LOCALFILEPATH + tmpVO.getFile_name());
							response_result = "Success";
							System.out.println(tmpVO.getFile_name() + " - is successfully uploaded to "
									+ tmpVO.getCandidate_first_name());
							if (file.exists()) {
								file.delete();
								// System.out.println(tmpVO.getFile_name()+" - is being deleted");
							}
						} else {
							tmpVO.setErrorMsg(Constants.GENERICERRMESSAGE + tmpVO.getReqString());
							dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
						}
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

		return response_result;
	}

	public String deleteUploadedFile() {
		String response = null;
		CaseDetailsVO caseDetails = null;
		int applicant_id = 0;
		int media_id = 0;
		try {
			List<ParamVO> paramList = dao.getSOQFilesFromDataBase(Constants.FILE_TO_DELETE);
			if (paramList != null && paramList.size() != 0) {
				for (ParamVO tmpVO : paramList) {
					dao.updateSOQAuditFlg(tmpVO, Constants.FILE_IN_RUNNING_STATE);
					caseDetails = intfUtils.getCaseDetailsForDelete(tmpVO);
					if (caseDetails.getMedia_id() > 0) {
						applicant_id = caseDetails.getApplicant_id();
						System.out.println("Applicant ID : " + applicant_id);
						media_id = caseDetails.getMedia_id();
						System.out.println("File Name : " + tmpVO.getFile_name());
						System.out.println("Media ID : " + media_id);

						response = intfUtils.deleteFilesUploaded(applicant_id, media_id, tmpVO);
						System.out.println("Response : " + response);
						if (response.equals("Success")) {
							dao.updateSOQAuditFlg(tmpVO, Constants.FILE_TO_UPLOAD);
							System.out.println("Delete process is completed for file : " + tmpVO.getFile_name());
						}

					} else {
						tmpVO.setErrorMsg(Constants.GENERICERRMESSAGE + tmpVO.getReqString());
						dao.updateErrorMsg(tmpVO, Constants.FILE_UPLOAD_FAILED);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String caseAndFileValidation() {
		String response = "Success";
		boolean fileValidationReturn = false;
		boolean caseValidationReturn = false;
		try {
			List<ParamVO> paramList = dao.getSOQFilesFromDataBase(Constants.FILE_TO_UPLOAD);
			if (paramList != null && paramList.size() != 0) {
				System.out.println("Process started at - " + new Date());
				System.out.println("Process started for - " + paramList.size() + " records");
				for (ParamVO tmpVO : paramList) {
					caseValidationReturn = intfUtils.checkIfCaseExists(tmpVO);
					if (caseValidationReturn) {
						tmpVO.setNetworkPath(Constants.NETWORKPATH);
						fileValidationReturn = intfUtils.checkFileInNetworkPath(tmpVO);
						if (!fileValidationReturn) {
							dao.updateSOQAuditFlg(tmpVO, Constants.FILE_NOT_FOUND);
						}
					} else {
						// Method call to update flag as F

					}

				}
				System.out.println("Process completed for - " + paramList.size() + " records");
				System.out.println("Process completed at - " + new Date());
			}
		} catch (

		Exception e) {
			response = null;
			e.printStackTrace();
		}
		return response;
	}
}
