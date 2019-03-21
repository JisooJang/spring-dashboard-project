package com.littleone.littleone_web.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="infant_diary")
public class Infant_diary {
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@OneToOne
	@JoinColumn(name="infant_idx")
	private Infant infant;
	
	@OneToOne
	@JoinColumn(name="member_idx")
	private Member member;
	
	@Column(name="subject", nullable=false)
	private String subject;
	@Column(name="contents", nullable=false)
	private String contents;
	@Column(name="event_date", nullable=false)
	private String event_date;
	@Column(name="date_created", nullable=false)
	private String date_created;
	@Column(name="date_updated", nullable=true)
	private String date_updated;
	@Column(name="hashtag", nullable=true)
	private String hashtag;
	@Column(name="share", nullable=false)
	private char share;
	@Column(name="hits", nullable=false)
	private int hits;
	@Column(name="likes", nullable=false)
	private int likes;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name="diary_idx")
	private List<Id_file> id_files = null;	
	
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name="diary_idx")
	private List<Id_comment> comments = null;
	
	public Infant_diary(int idx, Infant infant, Member member, String subject, String contents, 
			String date_created, String event_date, String date_updated, String hashtag, List<Id_file> id_files,
			List<Id_comment> comments) {
		super();
		this.idx = idx;
		this.infant = infant;
		this.member = member;
		this.subject = subject;
		this.contents = contents;
		this.event_date = event_date;
		this.date_created = date_created;
		this.date_updated = date_updated;
		this.hashtag = hashtag;
		this.id_files = id_files;
		this.comments = comments;	
	}
		
	// 대쉬보드 인덱스에서 리스트로 보여줄때 사용되는 생성자
	public Infant_diary(int idx, String subject, String date_created) {
		super();
		this.idx = idx;
		this.subject = subject;
		this.date_created = date_created;
	}

	public Infant_diary() {
		super();
		id_files = new ArrayList<Id_file>();
		comments = new ArrayList<Id_comment>();
	}

	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public Infant getInfant() {
		return infant;
	}
	public void setInfant(Infant infant) {
		this.infant = infant;
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

	public List<Id_file> getId_files() {
		return id_files;
	}
	public void setId_files(List<Id_file> id_files) {
		this.id_files = id_files;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Id_comment> getComments() {
		return comments;
	}

	public void setComments(List<Id_comment> comments) {
		this.comments = comments;
	}

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public char getShare() {
		return share;
	}

	public void setShare(char share) {
		this.share = share;
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
}
