package com.tf.intf.model;

import java.io.File;
import java.util.Date;

public class ParamVO {

	private int packet_id;
	private int unit_id;
	private String cwid;
	private String candidate_first_name;
	private String candidate_last_name;
	private String candidate_email;
	private Date due_at;
	private String template_id;
	private String file_name;
	private String errorMsg;
	private String reqString;
	private String networkPath;
	private String auditFlag = "N";
	private java.sql.Date processDate;
	private Boolean result = false;
	private int applicant_id;
	private int section_id;
	private Integer requirement_id;
	private int case_id;
	private String templateName;
	private int media_id;
	private String response;
	private String file_data;
	/**
	 * @return the networkPath
	 */
	public String getNetworkPath() {
		return networkPath;
	}
	/**
	 * @param networkPath the networkPath to set
	 */
	public void setNetworkPath(String networkPath) {
		this.networkPath = networkPath;
	}
	/**
	 * @return the reqString
	 */
	public String getReqString() {
		return reqString;
	}
	/**
	 * @param reqString the reqString to set
	 */
	public void setReqString(String reqString) {
		this.reqString = reqString;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	/**
	 * @return the file_name
	 */
	public String getFile_name() {
		return file_name;
	}
	/**
	 * @param file_name the file_name to set
	 */
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	/**
	 * @return the template_id
	 */
	public String getTemplate_id() {
		return template_id;
	}
	/**
	 * @param template_id the template_id to set
	 */
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	/**
	 * @return the packet_id
	 */
	public int getPacket_id() {
		return packet_id;
	}
	/**
	 * @param packet_id the packet_id to set
	 */
	public void setPacket_id(int packet_id) {
		this.packet_id = packet_id;
	}
	/**
	 * @return the unit_id
	 */
	public int getUnit_id() {
		return unit_id;
	}
	/**
	 * @param unit_id the unit_id to set
	 */
	public void setUnit_id(int unit_id) {
		this.unit_id = unit_id;
	}
	
	/**
	 * @return the cwid
	 */
	public String getCwid() {
		return cwid;
	}
	/**
	 * @param string the cwid to set
	 */
	public void setCwid(String string) {
		this.cwid = string;
	}
	/**
	 * @return the candidate_first_name
	 */
	public String getCandidate_first_name() {
		return candidate_first_name;
	}
	/**
	 * @param candidate_first_name the candidate_first_name to set
	 */
	public void setCandidate_first_name(String candidate_first_name) {
		this.candidate_first_name = candidate_first_name;
	}
	/**
	 * @return the candidate_last_name
	 */
	public String getCandidate_last_name() {
		return candidate_last_name;
	}
	/**
	 * @param candidate_last_name the candidate_last_name to set
	 */
	public void setCandidate_last_name(String candidate_last_name) {
		this.candidate_last_name = candidate_last_name;
	}
	/**
	 * @return the candidate_email
	 */
	public String getCandidate_email() {
		return candidate_email;
	}
	/**
	 * @param candidate_email the candidate_email to set
	 */
	public void setCandidate_email(String candidate_email) {
		this.candidate_email = candidate_email;
	}
	public Date getDue_at() {
		return due_at;
	}
	public void setDue_at(Date due_at) {
		this.due_at = due_at;
	}
	public String getAuditFlag() {
		return auditFlag;
	}
	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}
	public java.sql.Date getProcessDate() {
		return processDate;
	}
	public void setProcessDate(java.sql.Date processDate) {
		this.processDate = processDate;
	}
	public Boolean getResult() {
		return result;
	}
	public void setResult(Boolean result) {
		this.result = result;
	}
	public int getApplicant_id() {
		return applicant_id;
	}
	public void setApplicant_id(int applicant_id) {
		this.applicant_id = applicant_id;
	}
	public int getSection_id() {
		return section_id;
	}
	public void setSection_id(int section_id) {
		this.section_id = section_id;
	}
	public Integer getRequirement_id() {
		return requirement_id;
	}
	public void setRequirement_id(Integer requirement_id) {
		this.requirement_id = requirement_id;
	}
	public int getCase_id() {
		return case_id;
	}
	public void setCase_id(int case_id) {
		this.case_id = case_id;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public int getMedia_id() {
		return media_id;
	}
	public void setMedia_id(int media_id) {
		this.media_id = media_id;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getFile_data() {
		return file_data;
	}
	public void setFile_data(String file_data) {
		this.file_data = file_data;
	}
}
