package com.littleone.littleone_web.domain;

import java.io.Serializable;

public class Member_infant_id implements Serializable {
	private int member;
	private int infant;
	public Member_infant_id(int member, int infant) {
		super();
		this.member = member;
		this.infant = infant;
	}
	public Member_infant_id() {
		super();
	}
	public int getMember() {
		return member;
	}
	public void setMember(int member) {
		this.member = member;
	}
	public int getInfant() {
		return infant;
	}
	public void setInfant(int infant) {
		this.infant = infant;
	}
}
