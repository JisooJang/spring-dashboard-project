package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="gallery")
public class Gallery {
	
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@OneToOne
	@JoinColumn(name="member_idx")
	private Member member;
	
	@Column(name="subject", nullable=false)
	private String subject;
	
	@Column(name="contents", nullable=false)
	private String contents;
	
	@Column(name="hashtag", nullable=false)
	private String hashtag;
	
	@Column(name="hits", nullable=false)
	private int hits;
	
	@Column(name="likes", nullable=false)
	private int likes;
	
	@Column(name="date_created", nullable=false)
	private String date_created;
	
	@Column(name="date_updated", nullable=true)
	private String date_updated;
	
	@Column(name="original_filename", nullable=false)
	private String original_filename;
	
	@Column(name="server_filename", nullable=false)
	private String server_filename;
	
	@Column(name="file_url", nullable=false)
	private String file_url;
	
	@Column(name="file_size", nullable=false)
	private int file_size;
	
	public Gallery(int idx, Member member, String subject, String contents, String hashtag, int hits, int likes, String date_created,
			String date_updated, String original_filename, String server_filename, String file_url, int file_size) {
		super();
		this.idx = idx;
		this.member = member;
		this.subject = subject;
		this.contents = contents;
		this.hashtag = hashtag;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		this.date_updated = date_updated;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_url = file_url;
		this.file_size = file_size;
	}
	public Gallery() {
		super();
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
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
	public String getDate_updated() {
		return date_updated;
	}
	public void setDate_updated(String date_updated) {
		this.date_updated = date_updated;
	}
	public String getOriginal_filename() {
		return original_filename;
	}
	public void setOriginal_filename(String original_filename) {
		this.original_filename = original_filename;
	}
	public String getServer_filename() {
		return server_filename;
	}
	public void setServer_filename(String server_filename) {
		this.server_filename = server_filename;
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public int getFile_size() {
		return file_size;
	}
	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
}
