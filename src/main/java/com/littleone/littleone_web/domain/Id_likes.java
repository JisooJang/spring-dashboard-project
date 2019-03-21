package com.littleone.littleone_web.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="id_likes")
@IdClass(Id_likes.class)
public class Id_likes implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="member_idx")
	private int member_idx;
	@Id
	@Column(name="diary_idx")
	private int diary_idx;
	
	public Id_likes(int member_idx, int diary_idx) {
		super();
		this.member_idx = member_idx;
		this.diary_idx = diary_idx;
	}
	public Id_likes() {
		super();
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public int getDiary_idx() {
		return diary_idx;
	}
	public void setDiary_idx(int diary_idx) {
		this.diary_idx = diary_idx;
	}
}
