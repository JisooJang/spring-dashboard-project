package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="id_comment")
public class Id_comment {
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	@Column(name="diary_idx", nullable=false)
	private int diary_idx;
	@OneToOne
	@JoinColumn(name="writer_idx")
	private Member member;
	@Column(name="contents", nullable=false)
	private String contents;
	@Column(name="date_created", nullable=false)
	private String date_created;
	@Column(name="date_updated", nullable=true)
	private String date_updated;
	@Column(name="likes", nullable=false)
	private int likes;
	@Transient
	private int likes_check = 0;
	
	public Id_comment(int idx, int diary_idx, Member member, String contents, String date_created,
			String date_updated, int likes) {
		super();
		this.idx = idx;
		this.diary_idx = diary_idx;
		this.member = member;
		this.contents = contents;
		this.date_created = date_created;
		this.date_updated = date_updated;
		this.likes = likes;
	}
	
	public Id_comment() {
		super();
	}

	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public int getDiary_idx() {
		return diary_idx;
	}
	public void setDiary_idx(int diary_idx) {
		this.diary_idx = diary_idx;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
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
