package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="member_log")
public class Member_log {
	@Id
	@Column(name="session_id")
	private String session_id;
	@Column(name="member_idx", nullable=false)
	private int member_idx;
	@Column(name="log_type")
	private char log_type;
	@Column(name="ip_address", nullable=false)
	private String ip_address;
	@Column(name="browser", nullable=true)
	private String browser;
	@Column(name="login_date", nullable=false)
	private String login_date;
	@Column(name="logout_date", nullable=false)
	private String logout_date;
	
	public Member_log(String session_id, int member_idx, char log_type, String ip_address, String browser, String login_date, String logout_date) {
		super();
		this.session_id = session_id;
		this.member_idx = member_idx;
		this.log_type = log_type;
		this.ip_address = ip_address;
		this.browser = browser;
		this.login_date = login_date;
		this.logout_date = logout_date;
	}
	
	public Member_log() {
		super();
	}
	
	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public String getLogin_date() {
		return login_date;
	}

	public void setLogin_date(String login_date) {
		this.login_date = login_date;
	}

	public String getLogout_date() {
		return logout_date;
	}

	public void setLogout_date(String logout_date) {
		this.logout_date = logout_date;
	}

	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public char getLog_type() {
		return log_type;
	}

	public void setLog_type(char log_type) {
		this.log_type = log_type;
	}
}
