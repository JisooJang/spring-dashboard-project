package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="shipping_address")
public class Shipping_address {
	
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	private int member_idx;
	
	@Column(name="recipient_name", nullable=false)
	private String recipient_name;
	
	@Column(name="recipient_phone", nullable=false)
	private String recipient_phone;
	
	@Column(name="recipient_phone2")
	private String recipient_phone2;
	
	@Column(name="address_name", nullable=false)
	private String address_name;
	
	@Column(name="address1", nullable=false)
	private String address1;
	
	@Column(name="address2", nullable=false)
	private String address2;
	
	@Column(name="zipcode", nullable=false)
	private String zipcode;
	
	@Column(name="default_check", nullable=false)
	private char default_check;
	
	@Column(name="member_info", nullable=false)
	private char member_info;
	
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
	public String getRecipient_name() {
		return recipient_name;
	}
	public void setRecipient_name(String recipient_name) {
		this.recipient_name = recipient_name;
	}
	public String getRecipient_phone() {
		return recipient_phone;
	}
	public void setRecipient_phone(String recipient_phone) {
		this.recipient_phone = recipient_phone;
	}
	public String getRecipient_phone2() {
		return recipient_phone2;
	}
	public void setRecipient_phone2(String recipient_phone2) {
		this.recipient_phone2 = recipient_phone2;
	}
	public String getAddress_name() {
		return address_name;
	}
	public void setAddress_name(String address_name) {
		this.address_name = address_name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public char getDefault_check() {
		return default_check;
	}
	public void setDefault_check(char default_check) {
		this.default_check = default_check;
	}
	public char getMember_info() {
		return member_info;
	}
	public void setMember_info(char member_info) {
		this.member_info = member_info;
	}
}
