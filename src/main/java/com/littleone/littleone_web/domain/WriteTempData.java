package com.littleone.littleone_web.domain;

public class WriteTempData {
	private int infant_idx;
	private int member_idx;
	private String session_id;
	private String memo;
	private float temperature;
	private String serial_num;
	
	
	public WriteTempData() {
		super();
	}
	public WriteTempData(int infant_idx, int member_idx, String session_id, String memo, float temperature,
			String serial_num) {
		super();
		this.infant_idx = infant_idx;
		this.member_idx = member_idx;
		this.session_id = session_id;
		this.memo = memo;
		this.temperature = temperature;
		this.serial_num = serial_num;
	}
	public WriteTempData(int infant_idx, int member_idx, String session_id, float temperature, String serial_num) {
		super();
		this.infant_idx = infant_idx;
		this.member_idx = member_idx;
		this.session_id = session_id;
		this.temperature = temperature;
		this.serial_num = serial_num;
	}
	public int getInfant_idx() {
		return infant_idx;
	}
	public void setInfant_idx(int infant_idx) {
		this.infant_idx = infant_idx;
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}
	public String getSerial_num() {
		return serial_num;
	}
	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}
}
