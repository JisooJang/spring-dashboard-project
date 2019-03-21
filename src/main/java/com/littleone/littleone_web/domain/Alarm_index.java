package com.littleone.littleone_web.domain;

import java.util.List;

public class Alarm_index {
	private List<Alarm> alarm;
	private char checkMaxSize;
	public List<Alarm> getAlarm() {
		return alarm;
	}
	public void setAlarm(List<Alarm> alarm) {
		this.alarm = alarm;
	}
	public char getCheckMaxSize() {
		return checkMaxSize;
	}
	public void setCheckMaxSize(char checkMaxSize) {
		this.checkMaxSize = checkMaxSize;
	}
	public Alarm_index(List<Alarm> alarm, char checkMaxSize) {
		super();
		this.alarm = alarm;
		this.checkMaxSize = checkMaxSize;
	}
	public Alarm_index() {
		super();
	}
}
