package com.littleone.littleone_web.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Alarm {
	private int alarm_idx;
	private int requester;
	private String requester_name;	// 실제 의미는 알람 신청자 닉네임
	private String requester_thumbnail;
	private int recipient;
	private String date;
	private char event_type;
	private char read_chk;
	
	// Integer로 바꾸면 getOutputStream() 오류남. alert_board와 outer join을 위해 추가
	private Integer board_idx;
	private Integer comment_idx;
	
	public Alarm(int requester, int recipient, String date) {
		super();
		this.requester = requester;
		this.recipient = recipient;
		this.date = date;
	}
	
	public Alarm(int requester, int recipient, String date, char event_type) {
		super();
		this.requester = requester;
		this.recipient = recipient;
		this.date = date;
		this.event_type = event_type;
	}
	
	public Alarm(int alarm_idx, int requester, String requester_name, String requester_thumbnail, int recipient, String date, char event_type) {
		super();
		this.alarm_idx = alarm_idx;
		this.requester = requester;
		this.requester_name = requester_name;
		this.requester_thumbnail = requester_thumbnail;
		this.recipient = recipient;
		if(date.length() == 21) {
			this.date = date.substring(0, 19);
			try {
				setDateFormat();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			this.date = date;
		}
		this.event_type = event_type;
	}
	
	public Alarm(int alarm_idx, int requester, String requester_name, String requester_thumbnail, int recipient,
			String date, String event_type, String read_chk, Integer board_idx, Integer comment_idx) {
		super();
		this.alarm_idx = alarm_idx;
		this.requester = requester;
		this.requester_name = requester_name;
		this.requester_thumbnail = requester_thumbnail;
		this.recipient = recipient;
		if(date.length() == 21) {
			this.date = date.substring(0, 19);
			try {
				setDateFormat();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			this.date = date;
		}
		char temp = event_type.charAt(0);
		this.event_type = temp;
		this.read_chk = read_chk.charAt(0);
		this.board_idx = board_idx;
		this.comment_idx = comment_idx;
	}

	public Alarm(int requester, int recipient) {
		super();
		this.requester = requester;
		this.recipient = recipient;
	}

	public Alarm() {
		super();
	}

	public int getRequester() {
		return requester;
	}
	public void setRequester(int requester) {
		this.requester = requester;
	}
	public int getRecipient() {
		return recipient;
	}
	public void setRecipient(int recipient) {
		this.recipient = recipient;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public char getEvent_type() {
		return event_type;
	}

	public void setEvent_type(char event_type) {
		this.event_type = event_type;
	}
	public int getAlarm_idx() {
		return alarm_idx;
	}

	public void setAlarm_idx(int alarm_idx) {
		this.alarm_idx = alarm_idx;
	}

	public String getRequester_name() {
		return requester_name;
	}

	public void setRequester_name(String requester_name) {
		this.requester_name = requester_name;
	}

	public String getRequester_thumbnail() {
		return requester_thumbnail;
	}

	public void setRequester_thumbnail(String requester_thumbnail) {
		this.requester_thumbnail = requester_thumbnail;
	}
	
	public Integer getBoard_idx() {
		return board_idx;
	}

	public void setBoard_idx(Integer board_idx) {
		this.board_idx = board_idx;
	}

	public Integer getComment_idx() {
		return comment_idx;
	}

	public void setComment_idx(Integer comment_idx) {
		this.comment_idx = comment_idx;
	}

	public char getRead_chk() {
		return read_chk;
	}

	public void setRead_chk(char read_chk) {
		this.read_chk = read_chk;
	}

	public void setDateFormat() throws ParseException {
		Date date_created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.getDate());
		
		// 날짜가 당일이면 몇분전, 몇시간전, 몇초전으로 변환
		// 날짜가 당일이 아니면 년월일까지만 표기
		long curTime = System.currentTimeMillis();
		long regTime_created = date_created.getTime();

		long diffTime_created = (curTime - regTime_created) / 1000;

		String created = "";

		if(diffTime_created < 60) {
			// sec
			created = diffTime_created + "초전";
		} else if ((diffTime_created /= 60) < 60) {
			// min
			created = diffTime_created + "분전";
		} else if ((diffTime_created /= 60) < 24) {
			// hour
			created = (diffTime_created) + "시간전";
		} else {
			// 당일이 아니므로 년월일까지만 표시
			created = this.getDate().substring(0, 10);
		}
		setDate(created);
	}
}
