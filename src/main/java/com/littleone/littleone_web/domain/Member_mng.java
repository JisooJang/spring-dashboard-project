package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="member_mng")
public class Member_mng {
	@Id
	@Column(name="member_idx")
	private int member_idx;
	
	@Column(name="point", nullable=false)
	private int point;
	
	@Column(name="grade", nullable=false)
	private char grade;
	
	@Column(name="introduction", nullable=true)
	private String introduction;
	
	@Column(name="last_login_date", nullable=true)
	private String last_login_date;
	
	@Column(name="login_err_cnt", nullable=false)
	private int login_err_cnt;
	
	@Column(name="dormant_chk", nullable=false)
	private char dormant_chk;
	
	@Column(name="unit", nullable=true)
	private char unit;	
	
	public Member_mng() {
		super();
	}
	public Member_mng(int member_idx, int point, char grade, String last_login_date, int login_err_cnt, char dormant_chk) {
		super();
		this.member_idx = member_idx;
		this.point = point;
		this.grade = grade;
		this.last_login_date = last_login_date;
		this.login_err_cnt = login_err_cnt;
		this.dormant_chk = dormant_chk;
	}
	
	public Member_mng(int member_idx, int point, char grade, String introduction, String last_login_date,
			int login_err_cnt) {
		super();
		this.member_idx = member_idx;
		this.point = point;
		this.grade = grade;
		this.introduction = introduction;
		this.last_login_date = last_login_date;
		this.login_err_cnt = login_err_cnt;
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public char getGrade() {
		return grade;
	}
	public void setGrade(char grade) {
		this.grade = grade;
	}
	public String getLast_login_date() {
		return last_login_date;
	}
	public void setLast_login_date(String last_login_date) {
		this.last_login_date = last_login_date;
	}
	public int getLogin_err_cnt() {
		return login_err_cnt;
	}
	public void setLogin_err_cnt(int login_err_cnt) {
		this.login_err_cnt = login_err_cnt;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public char getDormant_chk() {
		return dormant_chk;
	}
	public void setDormant_chk(char dormant_chk) {
		this.dormant_chk = dormant_chk;
	}
	public char getUnit() {
		return unit;
	}
	public void setUnit(char unit) {
		this.unit = unit;
	}
}
