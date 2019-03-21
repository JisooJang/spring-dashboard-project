package com.littleone.littleone_web.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Dashboard")
public class Dashboard {
	@Id
	@Column(name="member_idx")
	private int member_idx;
	@Column(name="temp", nullable=false)
	private String temp;
	@Column(name="peepee", nullable=false)
	private String peepee;
	@Column(name="bottle", nullable=false)
	private String bottle;
	/*@Column(name="diary", nullable=false)
	private String diary;*/
	@Column(name="infant_info", nullable=false)
	private String infant_info;
	@Column(name="group_info", nullable=false)
	private String group_info;

	public Dashboard(int member_idx, String temp, String peepee, String bottle, /*String diary,*/
			String infant_info, String group_info) {
		super();
		this.member_idx = member_idx;
		this.temp = temp;
		this.peepee = peepee;
		this.bottle = bottle;
		//this.diary = diary;
		this.infant_info = infant_info;
		this.group_info = group_info;
	}

	public Dashboard() {
		super();
	}

	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getPeepee() {
		return peepee;
	}

	public void setPeepee(String peepee) {
		this.peepee = peepee;
	}

	public String getBottle() {
		return bottle;
	}

	public void setBottle(String bottle) {
		this.bottle = bottle;
	}

	/*public String getDiary() {
		return diary;
	}

	public void setDiary(String diary) {
		this.diary = diary;
	}*/

	public String getInfant_info() {
		return infant_info;
	}

	public void setInfant_info(String infant_info) {
		this.infant_info = infant_info;
	}

	public String getGroup_info() {
		return group_info;
	}

	public void setGroup_info(String group_info) {
		this.group_info = group_info;
	}

	public void setDashboardByJson(List<JsonDashboard> dashboard) {
		/*1 : bottle
		2 : peepee
		3 : infant_info
		4 : temp
		5 : diary
		6 : group_info
		7 : */
		for(int i=0 ; i<dashboard.size(); i++) {
			JsonDashboard board = dashboard.get(i);
			switch(board.getAttr()) {
			case '1':
				this.bottle = i + "-" + board.getX();
				break;
			case '2':
				this.peepee = i + "-" + board.getX();
				break;
			case '3':
				this.infant_info = i + "-" + board.getX();
				break;
			case '4':
				this.temp = i + "-" + board.getX();
				break;
			/*case '5':
				this.diary = i + "-" + board.getX();
				break;*/
			case '6':
				this.group_info = i + "-" + board.getX();
				break;
			}
		}
	}
}
