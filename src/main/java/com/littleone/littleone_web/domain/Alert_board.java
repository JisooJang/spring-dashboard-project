package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="alert_board")
public class Alert_board {
	@Id
	@Column(name="idx")
	private int idx;
	@Column(name="board_idx", nullable=false)
	private int board_idx;
	@Column(name="comment_idx", nullable=true)
	private int comment_idx;
	
	public Alert_board(int idx, int board_idx, int comment_idx) {
		super();
		this.idx = idx;
		this.board_idx = board_idx;
		this.comment_idx = comment_idx;
	}
	public Alert_board() {
		super();
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public int getBoard_idx() {
		return board_idx;
	}
	public void setBoard_idx(int board_idx) {
		this.board_idx = board_idx;
	}
	public int getComment_idx() {
		return comment_idx;
	}
	public void setComment_idx(int comment_idx) {
		this.comment_idx = comment_idx;
	}
	
}
