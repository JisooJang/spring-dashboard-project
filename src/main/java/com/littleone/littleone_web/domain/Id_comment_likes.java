package com.littleone.littleone_web.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="id_comment_likes")
@IdClass(Id_comment_likes.class)
public class Id_comment_likes implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="member_idx")
	private int member_idx;
	
	@Id
	@Column(name="comment_idx")
	private int comment_idx;
	
	public Id_comment_likes(int member_idx, int comment_idx) {
		super();
		this.member_idx = member_idx;
		this.comment_idx = comment_idx;
	}
	public Id_comment_likes() {
		super();
	}
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public int getComment_idx() {
		return comment_idx;
	}
	public void setComment_idx(int comment_idx) {
		this.comment_idx = comment_idx;
	}
}
