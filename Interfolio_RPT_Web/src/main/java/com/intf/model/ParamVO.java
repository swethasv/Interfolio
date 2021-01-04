package com.intf.model;

import java.util.Date;
import java.util.List;

public class ParamVO {

	private int packet_id;
	private int unit_id;
	private String cwid;
	private String candidate_first_name;
	private String candidate_last_name;
	private String candidate_email;
	private List<ParamVO> paramList;
	private Date due_at;
	private String template_id;
	
	

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
	public List<ParamVO> getParamList() {
		return paramList;
	}
	public void setParamList(List<ParamVO> paramList) {
		this.paramList = paramList;
	}
	public Date getDue_at() {
		return due_at;
	}
	public void setDue_at(Date due_at) {
		this.due_at = due_at;
	}

	
}
