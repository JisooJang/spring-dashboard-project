package com.littleone.littleone_web.api_response;

import java.util.List;

import org.json.JSONObject;

import com.littleone.littleone_web.domain.Infant_diary;

public class Infant_diary_response {
	private String result;
	private List<Infant_diary> diary_list = null;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public List<Infant_diary> getDiary_list() {
		return diary_list;
	}
	public void setDiary_list(List<Infant_diary> diary_list) {
		this.diary_list = diary_list;
	}
	
	public Infant_diary_response(String result, List<Infant_diary> diary_list) {
		super();
		this.result = result;
		this.diary_list = diary_list;
	}
	public Infant_diary_response() {
		super();
	}
}
