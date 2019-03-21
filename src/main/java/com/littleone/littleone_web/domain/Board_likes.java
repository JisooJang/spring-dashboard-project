package com.littleone.littleone_web.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="board_likes")
@IdClass(Board_likes.class)
public class Board_likes implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="member_idx")
	private int member_idx;
	
	@Id
	@Column(name="board_idx")
	private int board_idx;
	
	public Board_likes(int member_idx, int board_idx) {
		super();
		this.member_idx = member_idx;
		this.board_idx = board_idx;
	}

	public Board_likes() {
		super();
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public int getBoard_idx() {
		return board_idx;
	}
	public void setBoard_idx(int board_idx) {
		this.board_idx = board_idx;
	}
}
