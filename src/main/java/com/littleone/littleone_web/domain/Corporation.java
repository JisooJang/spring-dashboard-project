package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="corporation")
public class Corporation {
	
	@Id
	@Column(name="member_idx")
	private int member_idx;
	
	@Column(name="corp_num", nullable=false, unique=true)
	private String corp_num;
	
	@Column(name="corp_name", nullable=false)
	private String corp_name;
	
	@Column(name="field_code", nullable=false)
	private char field_code;		// 1. 서비스업, 2. 제조업, 3. 도소매, 4. 기타
	
	@Column(name="service_code", nullable=false)
	private char service_code;		// 1. 지식서비스, 2. 전자상거래, 3. 지식서비스 + 전자상거래
	
	@Column(name="approval", nullable=false)
	private char approval;
	
	@Column(name="dname", nullable=false)
	private String dname;
	
	@Column(name="dphone", nullable=false)
	private String dphone;

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
	public char getApproval() {
		return approval;
	}
	public void setApproval(char approval) {
		this.approval = approval;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getDphone() {
		return dphone;
	}
	public void setDphone(String dphone) {
		this.dphone = dphone;
	}
}
