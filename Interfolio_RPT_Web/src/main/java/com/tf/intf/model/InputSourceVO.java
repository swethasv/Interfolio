package com.tf.intf.model;

import java.sql.Date;

public class InputSourceVO {

	private String cwid;
	private String template_id;
	private String review_term;
	private int tenure;
	private Date create_date;
	
	public String getCwid() {
		return cwid;
	}
	public void setCwid(String cwid) {
		this.cwid = cwid;
	}
	public String getTemplate_id() {
		return template_id;
	}
	public void setTemplate_id(String template_id) {
		this.template_id = template_id;
	}
	public String getReview_term() {
		return review_term;
	}
	public void setReview_term(String review_term) {
		this.review_term = review_term;
	}
	public int getTenure() {
		return tenure;
	}
	public void setTenure(int tenure) {
		this.tenure = tenure;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}
}
