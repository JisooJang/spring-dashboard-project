package com.littleone.littleone_web.domain;

import java.io.Serializable;
import java.util.List;

public class GroupInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int group_idx = -1;
	private String group_admin;
	private int group_admin_idx = 0;
	private List<Member> group_members;
	//private List<Infant> infant_list;
	private Infant shared_infant;
	private String date; 	// yyyy-MM-dd
	private int feed_cnt;	// 위 date 날짜의 총 수유 횟수
	private int defec_cnt;	// 위 date 날짜의 총 배변 횟수
	
	private String last_temp;	// 위 date 날짜의 마지막 체온
	
	private String temp_time;	//위 date 날짜의 마지막 측정시간
	private String peepee_time;	//위 date 날짜의 마지막 측정시간
	private String bottle_time;	//위 date 날짜의 마지막 측정시간
	
	public GroupInfo() {
		super();
	}
	public GroupInfo(int group_idx, String group_admin, int group_admin_idx, List<Member> group_members,
			List<Infant> infant_list) {
		super();
		this.group_idx = group_idx;
		this.group_admin = group_admin;
		this.group_admin_idx = group_admin_idx;
		this.group_members = group_members;
		//this.infant_list = infant_list;
	}
	public int getGroup_idx() {
		return group_idx;
	}
	public void setGroup_idx(int group_idx) {
		this.group_idx = group_idx;
	}
	public String getGroup_admin() {
		return group_admin;
	}
	public void setGroup_admin(String group_admin) {
		this.group_admin = group_admin;
	}
	public int getGroup_admin_idx() {
		return group_admin_idx;
	}
	public void setGroup_admin_idx(int group_admin_idx) {
		this.group_admin_idx = group_admin_idx;
	}
	public List<Member> getGroup_members() {
		return group_members;
	}
	public void setGroup_members(List<Member> group_members) {
		this.group_members = group_members;
	}
	/*public List<Infant> getInfant_list() {
		return infant_list;
	}
	public void setInfant_list(List<Infant> infant_list) {
		this.infant_list = infant_list;
	}*/
	public Infant getShared_infant() {
		return shared_infant;
	}
	public void setShared_infant(Infant shared_infant) {
		this.shared_infant = shared_infant;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getFeed_cnt() {
		return feed_cnt;
	}
	public void setFeed_cnt(int feed_cnt) {
		this.feed_cnt = feed_cnt;
	}
	public int getDefec_cnt() {
		return defec_cnt;
	}
	public void setDefec_cnt(int defec_cnt) {
		this.defec_cnt = defec_cnt;
	}
	public String getTemp_time() {
		return temp_time;
	}
	public void setTemp_time(String temp_time) {
		this.temp_time = temp_time;
	}
	public String getPeepee_time() {
		return peepee_time;
	}
	public void setPeepee_time(String peepee_time) {
		this.peepee_time = peepee_time;
	}
	public String getBottle_time() {
		return bottle_time;
	}
	public void setBottle_time(String bottle_time) {
		this.bottle_time = bottle_time;
	}
	public String getLast_temp() {
		return last_temp;
	}
	public void setLast_temp(String last_temp) {
		this.last_temp = last_temp;
	}
	
}
