package com.littleone.littleone_web.domain;

public class SearchMember {

	private String search_phone;
	private String search_name;	// 의미는 닉네임
	private String search_thumbnail;
	private int search_idx;
	private int group_request_check = 0;
	
	public SearchMember(String search_phone, String name, String thumbnail, int search_idx) {
		super();
		this.search_phone = search_phone;
		this.search_name = name;
		this.search_thumbnail = thumbnail;
		this.search_idx = search_idx;
	}
	
	public SearchMember() {
		super();
	}

	public String getSearch_phone() {
		return search_phone;
	}

	public void setSearch_phone(String search_phone) {
		this.search_phone = search_phone;
	}

	public String getSearch_name() {
		return search_name;
	}

	public void setSearch_name(String search_name) {
		this.search_name = search_name;
	}

	public String getSearch_thumbnail() {
		return search_thumbnail;
	}

	public void setSearch_thumbnail(String search_thumbnail) {
		this.search_thumbnail = search_thumbnail;
	}

	public int getSearch_idx() {
		return search_idx;
	}

	public void setSearch_idx(int search_idx) {
		this.search_idx = search_idx;
	}

	public int getGroup_request_check() {
		return group_request_check;
	}

	public void setGroup_request_check(int group_request_check) {
		this.group_request_check = group_request_check;
	}
}