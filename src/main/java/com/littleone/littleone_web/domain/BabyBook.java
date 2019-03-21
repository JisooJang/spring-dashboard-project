package com.littleone.littleone_web.domain;

import java.util.List;

public class BabyBook {
	List<Infant_diary> diary_list;
	List<Infant_schedule> schedule_list;
	public BabyBook(List<Infant_diary> diary_list, List<Infant_schedule> schedule_list) {
		super();
		this.diary_list = diary_list;
		this.schedule_list = schedule_list;
	}
	public BabyBook() {
		super();
	}
	public List<Infant_diary> getDiary_list() {
		return diary_list;
	}
	public void setDiary_list(List<Infant_diary> diary_list) {
		this.diary_list = diary_list;
	}
	public List<Infant_schedule> getSchedule_list() {
		return schedule_list;
	}
	public void setSchedule_list(List<Infant_schedule> schedule_list) {
		this.schedule_list = schedule_list;
	}
	
}
