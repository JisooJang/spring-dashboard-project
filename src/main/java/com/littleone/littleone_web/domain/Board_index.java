package com.littleone.littleone_web.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Board_index {
	private int idx;
	private int member_idx;		// 관리자가 작성한 게시글인 경우 공지로 보여줄것
	private String subject;
	private String contents;
	private String nickname;
	private String thumbnail;
	private char attached_file = 'f';
	private int hits;
	private int likes;
	private int comment_count;
	private String date_created;
	private String date_updated;
	private String date_created2 = null;	// 당일인경우 몇시간전, 몇분전 표기를 위한 리스트 인덱스용 날짜
	private String date_updated2 = null;
	private int new_check = 0;
	private char category;

	public Board_index(int idx, int member_idx, String subject, String contents, String nickname, String thumbnail,
			int hits, int likes, String date_created, char category) {
		super();
		this.idx = idx;
		this.member_idx = member_idx;
		this.subject = subject;
		this.contents = contents;
		if(nickname != null) {
			this.nickname = nickname;
		}
		if(thumbnail != null) {
			this.thumbnail = thumbnail;
		}
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		this.category = category;
	}
	
	public Board_index(int idx, int member_idx, String subject, String contents, String nickname, String thumbnail,
			int hits, int likes, String date_created) {
		super();
		this.idx = idx;
		this.member_idx = member_idx;
		this.subject = subject;
		this.contents = contents;
		if(nickname != null) {
			this.nickname = nickname;
		}
		if(thumbnail != null) {
			this.thumbnail = thumbnail;
		}
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
	}


	public Board_index(int idx, String subject, String contents, String nickname, String thumbnail,
			int hits, int likes, String date_created) {
		super();
		this.idx = idx;
		this.subject = subject;
		this.contents = contents;
		if(nickname != null) {
			this.nickname = nickname;
		}
		if(thumbnail != null) {
			this.thumbnail = thumbnail;
		}
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
	}

	public Board_index() {
		super();
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}
	public int getMember_idx() {
		return member_idx;
	}

	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}


	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
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
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public char getAttached_file() {
		return attached_file;
	}

	public void setAttached_file(char attached_file) {
		this.attached_file = attached_file;
	}

	public String getDate_updated() {
		return date_updated;
	}

	public void setDate_updated(String date_updated) {
		this.date_updated = date_updated;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	public String getDate_created2() {
		return date_created2;
	}

	public void setDate_created2(String date_created2) {
		this.date_created2 = date_created2;
	}

	public String getDate_updated2() {
		return date_updated2;
	}

	public void setDate_updated2(String date_updated2) {
		this.date_updated2 = date_updated2;
	}

	public int getNew_check() {
		return new_check;
	}

	public void setNew_check(int new_check) {
		this.new_check = new_check;
	}

	public char getCategory() {
		return category;
	}

	public void setCategory(char category) {
		this.category = category;
	}

	public void setDate_created() throws ParseException {
		Date date_created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.getDate_created());
		
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
			created = this.getDate_created().substring(0, 10);
		}
		setDate_created2(created);

	}
	
	public void setDate_updated() throws ParseException {
		Date date_updated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(this.getDate_updated());
		
		// 날짜가 당일이면 몇분전, 몇시간전, 몇초전으로 변환
		// 날짜가 당일이 아니면 년월일까지만 표기
		long curTime = System.currentTimeMillis();
		long regTime_updated = date_updated.getTime();
		long diffTime_updated = (curTime - regTime_updated) / 1000;

		String updated = "";

		if(diffTime_updated < 60) {
			// sec
			updated = diffTime_updated + "초전";
		} else if ((diffTime_updated /= 60) < 60) {
			// min
			System.out.println(diffTime_updated);
			updated = diffTime_updated + "분전";
		} else if ((diffTime_updated /= 60) < 24) {
			// hour
			updated = (diffTime_updated) + "시간전";
		} else {
			// 당일이 아니므로 년월일까지만 표시
			updated = this.getDate_updated().substring(0, 10);
		}
		this.date_updated2 = updated;

	}
}
