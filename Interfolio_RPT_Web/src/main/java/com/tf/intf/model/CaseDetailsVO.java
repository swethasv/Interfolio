package com.tf.intf.model;

public class CaseDetailsVO {
	
	public int applicant_id;
	public int section_id;
	public Integer requirement_id;
	public int case_id;
	public String templateName;
	public int media_id;
	
	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}
	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	/**
	 * @return the applicant_id
	 */
	public int getApplicant_id() {
		return applicant_id;
	}
	/**
	 * @param applicant_id the applicant_id to set
	 */
	public void setApplicant_id(int applicant_id) {
		this.applicant_id = applicant_id;
	}
	
	
	/**
	 * @return the requirement_id
	 */
	public Integer getRequirement_id() {
		return requirement_id;
	}
	/**
	 * @param requirement_id the requirement_id to set
	 */
	public void setRequirement_id(Integer requirement_id) {
		this.requirement_id = requirement_id;
	}
	/**
	 * @return the section_id
	 */
	public int getSection_id() {
		return section_id;
	}
	/**
	 * @param section_id the section_id to set
	 */
	public void setSection_id(int section_id) {
		this.section_id = section_id;
	}

	/**
	 * @return the case_id
	 */
	public int getCase_id() {
		return case_id;
	}
	/**
	 * @param case_id the case_id to set
	 */
	public void setCase_id(int case_id) {
		this.case_id = case_id;
	}
	/**
	 * @return the media_id
	 */
	public int getMedia_id() {
		return media_id;
	}
	/**
	 * @param media_id the media_id to set
	 */
	public void setMedia_id(int media_id) {
		this.media_id = media_id;
	}
	
}
