package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;

@SqlResultSetMapping(
        name="AlertMapping",
        classes = @ConstructorResult(
                targetClass = Alarm.class,
                columns = {
                        @ColumnResult(name="alarm_idx", type = Integer.class),
                        @ColumnResult(name="requester", type = Integer.class),
                        @ColumnResult(name="requester_name", type = String.class),
                        @ColumnResult(name="requester_thumbnail", type = String.class),
                        @ColumnResult(name="recipient", type = Integer.class),
                        @ColumnResult(name="date", type = String.class),
                        @ColumnResult(name="event_type", type = String.class),
                        @ColumnResult(name="read_chk", type = String.class),
                        @ColumnResult(name="board_idx", type = Integer.class),
                        @ColumnResult(name="comment_idx", type = Integer.class)
                })
)
@Entity
@Table(name="alert")
public class Alert {
	
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="requester", nullable=false)
	private int requester;
	
	@Column(name="recipient", nullable=false)
	private int recipient;
	
	@Column(name="request_date", nullable=false)
	private String request_date;
	
	@Column(name="event_type", nullable=false)
	private char event_type;	// 0: 그룹수락알림, 1:그룹신청알림, 2:게시물댓글알림, 3:갤러리댓글알림, 4:그룹다이어리작성알림, 5:그룹스케쥴작성알림, 6:배변알림, 7:수유알림, 8:저체온알림, 9:고체온알림
	
	@Column(name="read_chk", nullable=false)
	private char read_chk;	// 읽음 구분값. 'f' 또는 't'
	
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
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
	public String getRequest_date() {
		return request_date;
	}
	public void setRequest_date(String request_date) {
		this.request_date = request_date;
	}
	public char getEvent_type() {
		return event_type;
	}
	public void setEvent_type(char event_type) {
		this.event_type = event_type;
	}
	public char getRead_chk() {
		return read_chk;
	}
	public void setRead_chk(char read_chk) {
		this.read_chk = read_chk;
	}
}
