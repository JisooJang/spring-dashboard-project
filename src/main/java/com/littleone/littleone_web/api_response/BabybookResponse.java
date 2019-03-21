package com.littleone.littleone_web.api_response;

import java.util.List;

public class BabybookResponse {
	List<ScheduleResponse> schedule_response;
	List<DiaryResponse> diary_response;
	public List<ScheduleResponse> getSchedule_response() {
		return schedule_response;
	}
	public void setSchedule_response(List<ScheduleResponse> schedule_response) {
		this.schedule_response = schedule_response;
	}
	
	public List<DiaryResponse> getDiary_response() {
		return diary_response;
	}
	public void setDiary_response(List<DiaryResponse> diary_response) {
		this.diary_response = diary_response;
	}
	
	public BabybookResponse(List<ScheduleResponse> schedule_response, List<DiaryResponse> diary_response) {
		super();
		this.schedule_response = schedule_response;
		this.diary_response = diary_response;
	}
	public BabybookResponse() {
		super();
	}
}
