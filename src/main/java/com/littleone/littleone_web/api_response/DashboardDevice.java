package com.littleone.littleone_web.api_response;

public class DashboardDevice {
	private int total_count;
	private String date_time;
	
	public DashboardDevice() {
		super();
	}
	public DashboardDevice(int total_count, String date_time) {
		super();
		this.total_count = total_count;
		this.date_time = date_time;
	}
	public int getTotal_count() {
		return total_count;
	}
	public void setTotal_count(int total_count) {
		this.total_count = total_count;
	}
	public String getDate_time() {
		return date_time;
	}
	public void setDate_time(String date_time) {
		this.date_time = date_time;
	}
}
