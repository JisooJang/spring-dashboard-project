package com.littleone.littleone_web.api_request;

public class AlarmTalkRequest {
	private String phone;	// 필수
	private String callback;	// 필수
	private String reqdate;
	private String msg;	// 필수
	private String template_code;	// 필수
	private String failed_type;
	private String url;
	private String url_button_txt;
	private String failed_subject;
	private String failed_msg;
	private String apiVersion;	// 필수
	private String client_id;	// 필수
	private String btn_types;
	private String btn_txts;
	private String btn_urls1;
	private String btn_urls2;
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getReqdate() {
		return reqdate;
	}
	public void setReqdate(String reqdate) {
		this.reqdate = reqdate;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getTemplate_code() {
		return template_code;
	}
	public void setTemplate_code(String template_code) {
		this.template_code = template_code;
	}
	public String getFailed_type() {
		return failed_type;
	}
	public void setFailed_type(String failed_type) {
		this.failed_type = failed_type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl_button_txt() {
		return url_button_txt;
	}
	public void setUrl_button_txt(String url_button_txt) {
		this.url_button_txt = url_button_txt;
	}
	public String getFailed_subject() {
		return failed_subject;
	}
	public void setFailed_subject(String failed_subject) {
		this.failed_subject = failed_subject;
	}
	public String getFailed_msg() {
		return failed_msg;
	}
	public void setFailed_msg(String failed_msg) {
		this.failed_msg = failed_msg;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getBtn_types() {
		return btn_types;
	}
	public void setBtn_types(String btn_types) {
		this.btn_types = btn_types;
	}
	public String getBtn_txts() {
		return btn_txts;
	}
	public void setBtn_txts(String btn_txts) {
		this.btn_txts = btn_txts;
	}
	public String getBtn_urls1() {
		return btn_urls1;
	}
	public void setBtn_urls1(String btn_urls1) {
		this.btn_urls1 = btn_urls1;
	}
	public String getBtn_urls2() {
		return btn_urls2;
	}
	public void setBtn_urls2(String btn_urls2) {
		this.btn_urls2 = btn_urls2;
	}
}
