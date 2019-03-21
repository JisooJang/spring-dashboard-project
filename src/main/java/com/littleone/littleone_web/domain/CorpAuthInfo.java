package com.littleone.littleone_web.domain;

public class CorpAuthInfo {
	private int member_idx;
	private String license_url;
	private String corp_num;
	private String corp_name;
	private String name;
	private char field_code;
	private char service_code;
	
	public CorpAuthInfo(int member_idx, String license_url, String corp_num, String corp_name, String name,
			char field_code, char service_code) {
		super();
		this.member_idx = member_idx;
		this.license_url = license_url;
		this.corp_num = corp_num;
		this.corp_name = corp_name;
		this.name = name;
		this.field_code = field_code;
		this.service_code = service_code;
	}
	
	public CorpAuthInfo() {
	}

	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public String getCorp_num() {
		return corp_num;
	}
	public void setCorp_num(String corp_num) {
		this.corp_num = corp_num;
	}
	public String getCorp_name() {
		return corp_name;
	}
	public void setCorp_name(String corp_name) {
		this.corp_name = corp_name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char getField_code() {
		return field_code;
	}
	public void setField_code(char field_code) {
		this.field_code = field_code;
	}
	public char getService_code() {
		return service_code;
	}
	public void setService_code(char service_code) {
		this.service_code = service_code;
	}
	public String getLicense_url() {
		return license_url;
	}
	public void setLicense_url(String license_url) {
		this.license_url = license_url;
	}
}
