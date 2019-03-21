package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Device")
public class Device {
	@Id
	@Column(name="idx")
	private int idx;
	@Column(name="member_idx")
	private int member_idx;
	@Column(name="serial_num")
	private String serial_num;
	@Column(name="type", nullable=false)
	private char type;	// temp : 1 / peepee : 2 / bottle : 3
	@Column(name="firmware", nullable=true)
	private String firmware;
	@Column(name="mac_address", nullable=true)
	private String mac_address;
	@Column(name="unit", nullable=false)
	private char unit;
	@Column(name="switch_chk", nullable=false)
	private char switch_chk;
	@Column(name="reg_time", nullable=false)
	private String reg_time;
	@Column(name="update_time", nullable=true)
	private String update_time;
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public String getSerial_num() {
		return serial_num;
	}
	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public String getReg_time() {
		return reg_time;
	}
	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}
	
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	public char getUnit() {
		return unit;
	}
	public void setUnit(char unit) {
		this.unit = unit;
	}
	public char getSwitch_chk() {
		return switch_chk;
	}
	public void setSwitch_chk(char switch_chk) {
		this.switch_chk = switch_chk;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
	public Device(int member_idx, String serial_num, char type, String reg_time) {
		super();
		this.member_idx = member_idx;
		this.serial_num = serial_num;
		this.type = type;
		this.reg_time = reg_time;
	}
	public Device() {
		super();
	}
}
