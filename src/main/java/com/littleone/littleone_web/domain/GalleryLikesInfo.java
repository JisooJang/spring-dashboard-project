package com.littleone.littleone_web.domain;

public class GalleryLikesInfo {
	private int member_idx;
	private String thumbnail;
	private String nickname;

	public GalleryLikesInfo(int member_idx, String thumbnail, String nickname) {
		super();
		this.member_idx = member_idx;
		this.thumbnail = thumbnail;
		this.nickname = nickname;
	}
	
	public int getMember_idx() {
		return member_idx;
	}
	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
