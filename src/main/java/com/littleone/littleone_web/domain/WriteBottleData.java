package com.littleone.littleone_web.domain;

import java.util.List;

public class WriteBottleData {
	private String member_idx;
	private String session_id;
	private String serial_num;
	private List<Bottle> bottles;
	public WriteBottleData() {
		super();
	}
	public WriteBottleData(String member_idx, String session_id, List<Bottle> bottles) {
		super();
		this.member_idx = member_idx;
		this.session_id = session_id;
		this.bottles = bottles;
	}
	public String getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(String member_idx) {
		this.member_idx = member_idx;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public List<Bottle> getBottles() {
		return bottles;
	}
	public void setBottles(List<Bottle> bottles) {
		this.bottles = bottles;
	}
	public String getSerial_num() {
		return serial_num;
	}
	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}
}
