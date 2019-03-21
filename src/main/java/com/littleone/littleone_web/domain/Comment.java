package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="comment")
public class Comment {
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="board_idx", nullable=false)
	private int board_idx;
	
	@OneToOne(fetch=FetchType.EAGER)	// 즉시 로딩
	@JoinColumn(name="writer_idx")
	private Member member;
	
	@Column(name="category", nullable=false)
	private char category;
	
	@Column(name="contents", nullable=false)
	private String contents;
	
	@Column(name="likes", nullable=false)
	private int likes;
	
	@Column(name="date_created", nullable=false)
	private String date_created;
	
	@Column(name="date_updated", nullable=true)
	private String date_updated;
	
	@Transient
	private int likes_check = 0;
	
	public Comment() {
		super();
	}
	
	public Comment(int board_idx, Member member, char category, String contents, String date_created,
			String date_updated) {
		super();
		this.board_idx = board_idx;
		this.member = member;
		this.category = category;
		this.contents = contents;
		this.date_created = date_created;
		if(date_updated != null && date_updated.length() > 0) {
			this.date_updated = date_updated;
		}	
	}

	public Comment(int board_idx, Member member, char category, String contents, int likes, String date_created,
			String date_updated) {
		super();
		this.board_idx = board_idx;
		this.member = member;
		this.category = category;
		this.contents = contents;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null && date_updated.length() > 0) {
			this.date_updated = date_updated;
		}
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
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public char getCategory() {
		return category;
	}
	public void setCategory(char category) {
		this.category = category;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getDate_created() {
		return date_created;
	}
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	public String getDate_updated() {
		return date_updated;
	}
	public void setDate_updated(String date_updated) {
		this.date_updated = date_updated;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getLikes_check() {
		return likes_check;
	}

	public void setLikes_check(int likes_check) {
		this.likes_check = likes_check;
	}
}
