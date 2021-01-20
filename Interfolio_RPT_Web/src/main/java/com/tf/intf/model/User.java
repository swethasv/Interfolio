package com.tf.intf.model;

public class User {

	private String CWID;
	private String CAND_FIRST_NM;
	
	public String getId() {
		return CWID;
	}
	public void setId(String id) {
		this.CWID = id;
	}
	public String getName() {
		return CAND_FIRST_NM;
	}
	public void setName(String name) {
		this.CAND_FIRST_NM = name;
	}
	
}
