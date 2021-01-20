package com.tf.intf.model;

public class SOQVO {

	private String CSUFID;
	private String CWID;
	private String template_id;
	private String cand_name;
	private String cand_email;
	private String file_type;
	private String file_name;
	
	public String getCSUFID() {
		return CSUFID;
	}
	public void setCSUFID(String cSUFID) {
		CSUFID = cSUFID;
	}
	public String getCWID() {
		return CWID;
	}
	public void setCWID(String cWID) {
		CWID = cWID;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getCand_name() {
		return cand_name;
	}
	public void setCand_name(String cand_name) {
		this.cand_name = cand_name;
	}
	public String getCand_email() {
		return cand_email;
	}
	public void setCand_email(String cand_email) {
		this.cand_email = cand_email;
	}
	public String getFile_type() {
		return file_type;
	}
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	
}
