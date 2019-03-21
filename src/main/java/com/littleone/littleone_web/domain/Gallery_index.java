package com.littleone.littleone_web.domain;

public class Gallery_index {
	private int diary_idx;
	private int member_idx;
	private String subject;
	private String contents;
	private String nickname;
	private String thumbnail;
	private int hits;
	private int likes;
	private int likes_check;
	private int comment_count;
	private String date_created;
	private String file_url;
	private String hashtag;
	private String[] hashtag2;
	
	public Gallery_index(int diary_idx, int member_idx, String subject, String contents, String nickname, String thumbnail, int hits,
			int likes, String date_created, String file_url, String hashtag) {
		super();
		this.diary_idx = diary_idx;
		this.member_idx = member_idx;
		this.subject = subject;
		this.contents = contents;
		this.nickname = nickname;
		this.thumbnail = thumbnail;
		this.hits = hits;
		this.likes = likes;
		//this.comment_count = (int) comment_count;
		this.date_created = date_created;
		if(file_url != null && file_url.length() > 0) {
			String[] origin_url = file_url.split("https://s3.ap-northeast-2.amazonaws.com/littleone/dashboard/diary/" + member_idx + "/");
			this.file_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/community/gallery/gallery_thumbnail/" + origin_url[1];
		} else {
			file_url = null;
		}
		this.hashtag = hashtag;
		if(hashtag != null) {
			this.hashtag2 = hashtag.split(",");
		}
	}
	public Gallery_index() {
		super();
	}
	public int getDiary_idx() {
		return diary_idx;
	}
	public void setDiary_idx(int diary_idx) {
		this.diary_idx = diary_idx;
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
	public int getLikes_check() {
		return likes_check;
	}
	public void setLikes_check(int likes_check) {
		this.likes_check = likes_check;
	}
	public int getComment_count() {
		return comment_count;
	}
	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	public String getDate_created() {
		return date_created;
	}
	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
	public String[] getHashtag2() {
		return hashtag2;
	}
	public void setHashtag2(String[] hashtag2) {
		this.hashtag2 = hashtag2;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
