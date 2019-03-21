package com.littleone.littleone_web.api_response;

import java.util.ArrayList;
import java.util.List;

public class ScheduleResponse {
	private String event_date_start;
	private String event_date_end;
	private List<Integer> total_date = null;
	private String event_type;
	
	public String getEvent_type() {
		return event_type;
	}
	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}
	public String getEvent_date_start() {
		return event_date_start;
	}
	public void setEvent_date_start(String event_date_start) {
		this.event_date_start = event_date_start;
	}
	public String getEvent_date_end() {
		return event_date_end;
	}
	public void setEvent_date_end(String event_date_end) {
		this.event_date_end = event_date_end;
	}
	
	public List<Integer> getTotal_date() {
		return total_date;
	}
	public void setTotal_date(List<Integer> total_date) {
		this.total_date = total_date;
	}
	public ScheduleResponse(String event_date_start, String event_date_end, char event_type) {
		super();
		this.event_date_start = event_date_start;
		this.event_date_end = event_date_end;
		
		int event_date_start2 = Integer.parseInt(event_date_start);
		int event_date_end2 = Integer.parseInt(event_date_end);
		
		if(event_date_start2 < event_date_end2) {
			total_date = new ArrayList<Integer>();
		}
		
		for(int i=event_date_start2 ; i<event_date_end2 ; i++) {
			total_date.add(i);
		}
		this.event_type = event_type + "";
	}
	public ScheduleResponse() {
		super();
	}
}
