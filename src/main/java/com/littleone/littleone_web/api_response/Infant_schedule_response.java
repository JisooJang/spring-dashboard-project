package com.littleone.littleone_web.api_response;

import java.util.List;

import com.littleone.littleone_web.domain.Infant_schedule;

public class Infant_schedule_response {
	private String result;
	private List<Infant_schedule> schedule_list;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public List<Infant_schedule> getSchedule_list() {
		return schedule_list;
	}
	public void setSchedule_list(List<Infant_schedule> schedule_list) {
		this.schedule_list = schedule_list;
	}
	public Infant_schedule_response(String result, List<Infant_schedule> schedule_list) {
		super();
		this.result = result;
		this.schedule_list = schedule_list;
	}
	public Infant_schedule_response() {
		super();
	}
}
