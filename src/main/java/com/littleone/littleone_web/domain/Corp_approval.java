package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="corp_approval")
public class Corp_approval {
	
	@Id
	@Column(name="member_idx")
	private int member_idx;
	
	@Column(name="license_url", nullable=false)
	private String license_url;

	public Corp_approval() {
		
	}

	public Corp_approval(int member_idx, String license_url) {
		super();
		this.member_idx = member_idx;
		this.license_url = license_url;
	}

	public int getMember_idx() {
		return member_idx;
	}

	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}

	public String getLicense_url() {
		return license_url;
	}

	public void setLicense_url(String license_url) {
		this.license_url = license_url;
	}
}
