package com.littleone.littleone_web.domain;

public class Comment_check {
	private int idx;

	private int board_idx;

	private int writer_idx;
	
	private String writer_thumbnail;
	
	private String writer_nickname;

	private char category;

	private String contents;

	private int likes;

	private String date_created;

	private String date_updated;
	
	private int likes_check = 0;
			
	public Comment_check(int idx, int board_idx, int writer_idx, String writer_thumbnail, String writer_nickname,
			String contents, int likes, String date_created, String date_updated) {
		super();
		this.idx = idx;
		this.board_idx = board_idx;
		this.writer_idx = writer_idx;
		this.writer_thumbnail = writer_thumbnail;
		this.writer_nickname = writer_nickname;
		this.contents = contents;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null && date_updated.length() > 0) {
			this.date_updated = date_updated;
		}	
	}
	
	public Comment_check(int idx, int board_idx, int writer_idx, String writer_thumbnail, String writer_nickname, char category,
			String contents, int likes, String date_created, String date_updated) {
		super();
		this.idx = idx;
		this.board_idx = board_idx;
		this.writer_idx = writer_idx;
		this.writer_thumbnail = writer_thumbnail;
		this.writer_nickname = writer_nickname;
		this.category = category;
		this.contents = contents;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null && date_updated.length() > 0) {
			this.date_updated = date_updated;
		}	
	}

	public Comment_check() {
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

	public int getWriter_idx() {
		return writer_idx;
	}

	public void setWriter_idx(int writer_idx) {
		this.writer_idx = writer_idx;
	}

	public String getWriter_thumbnail() {
		return writer_thumbnail;
	}

	public void setWriter_thumbnail(String writer_thumbnail) {
		this.writer_thumbnail = writer_thumbnail;
	}

	public String getWriter_nickname() {
		return writer_nickname;
	}

	public void setWriter_nickname(String writer_nickname) {
		this.writer_nickname = writer_nickname;
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

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
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

	public int getLikes_check() {
		return likes_check;
	}

	public void setLikes_check(int likes_check) {
		this.likes_check = likes_check;
	}
}
