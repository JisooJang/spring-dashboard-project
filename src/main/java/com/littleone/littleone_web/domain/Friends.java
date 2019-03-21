package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="infant")
public class Friends {
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int friend_id;
	
	@Column(name="member_idx1")
	private int member_idx1;
	
	@Column(name="member_idx2")
	private int member_idx2;
	
	@Column(name="status")
	private char status;
	
	@Column(name="request_time")
	private String request_time;
	
	public Friends(int friend_id, int member_idx1, int member_idx2, char status, String request_time) {
		super();
		this.friend_id = friend_id;
		this.member_idx1 = member_idx1;
		this.member_idx2 = member_idx2;
		this.status = status;
		this.request_time = request_time;
	}
	public Friends() {
		super();
	}
	public int getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}
	public int getMember_idx1() {
		return member_idx1;
	}
	public void setMember_idx1(int member_idx1) {
		this.member_idx1 = member_idx1;
	}
	public int getMember_idx2() {
		return member_idx2;
	}
	public void setMember_idx2(int member_idx2) {
		this.member_idx2 = member_idx2;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public String getRequest_time() {
		return request_time;
	}
	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}
}
