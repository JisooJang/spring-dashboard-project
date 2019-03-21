package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="member_group")
public class Member_group {
	
	@Id
	@Column(name="member_idx")
	private int member_idx;
	
	//@Id
	@Column(name="group_idx", nullable=false)
	private int group_idx;
	
	@Column(name="authority", nullable=false)
	private char authority;
	
	public Member_group() {
		
	}
	public Member_group(int member_idx, int group_idx, char authority) {
		super();
		this.member_idx = member_idx;
		this.group_idx = group_idx;
		this.authority = authority;
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public int getGroup_idx() {
		return group_idx;
	}
	public void setGroup_idx(int group_idx) {
		this.group_idx = group_idx;
	}
	public char getAuthority() {
		return authority;
	}
	public void setAuthority(char authority) {
		this.authority = authority;
	}
}
