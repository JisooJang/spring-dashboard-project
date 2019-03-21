package com.littleone.littleone_web.api_response;

public class DiaryResponse {
	private String event_date_start;
	private String event_type = "1";
	
	public DiaryResponse(String event_date_start) {
		super();
		this.event_date_start = event_date_start;
	}
	public DiaryResponse() {
		super();
	}
	public String getEvent_date_start() {
		return event_date_start;
	}
	public void setEvent_date_start(String event_date_start) {
		this.event_date_start = event_date_start;
	}
	public String getEvent_type() {
		return event_type;
	}
	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}
}
