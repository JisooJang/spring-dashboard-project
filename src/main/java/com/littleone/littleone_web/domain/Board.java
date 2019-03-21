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
@Table(name="board")
public class Board {
	
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="category", nullable=false)
	private char category;
	
	@Column(name="subject", nullable=false)
	private String subject;
	
	@Column(name="contents", nullable=false)
	private String contents;
	
	@Column(name="hashtag", nullable=true)
	private String hashtag;
	
	@Column(name="hits", nullable=false)
	private int hits;
	
	@Column(name="likes", nullable=false)
	private int likes;
	
	@Column(name="date_created", nullable=false)
	private String date_created;
	
	@Column(name="date_updated", nullable=true)
	private String date_updated;
	
	@Column(name="notice", nullable=false)
	private char notice;
	
	@Column(name="public_check", nullable=false)
	private char public_check;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)	// 지연 로딩. 연관된 엔티티를 실제 사용할 시점에 조회
	@JoinColumn(name="board_idx")
	private List<Comment> comments = new ArrayList<Comment>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)	// 즉시 로딩. Board 엔티티를 조회할때 Board_file 엔티티도 함께 조회
	@JoinColumn(name="board_idx")
	private List<Board_file> board_files = new ArrayList<Board_file>();
	
	@OneToOne
	@JoinColumn(name="member_idx")
	private Member member;

	public Board() {
		super();
	}
	
	public Board(char category, String subject, String contents, int hits, int likes, String date_created,
			String date_updated, List<Comment> comments, List<Board_file> board_files, Member member) {
		super();
		this.category = category;
		this.subject = subject;
		this.contents = contents;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null) {
			this.date_updated = date_updated;
		}
		if(comments != null) {
			this.comments = comments;
		}
		if(board_files != null) {
			this.board_files = board_files;
		}
		if(member != null) {
			this.member = member;
		}
	}
	
	public Board(char category, String subject, String contents, int hits, int likes, String date_created,
			String date_updated, char notice, List<Comment> comments, List<Board_file> board_files, Member member) {
		super();
		this.category = category;
		this.subject = subject;
		this.contents = contents;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null) {
			this.date_updated = date_updated;
		}
		if(comments != null) {
			this.comments = comments;
		}
		if(board_files != null) {
			this.board_files = board_files;
		}
		if(member != null) {
			this.member = member;
		}
	}
	
	public Board(int idx, char category, String subject, String contents, int hits, int likes, String date_created,
			String date_updated) {
		super();
		this.idx = idx;
		this.category = category;
		this.subject = subject;
		this.contents = contents;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		this.date_updated = date_updated;
	}
		
	public Board(int idx, char category, String subject, String contents, int hits, int likes, String date_created,
			String date_updated, List<Comment> comments, List<Board_file> board_files, Member member) {
		super();
		this.idx = idx;
		this.category = category;
		this.subject = subject;
		this.contents = contents;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null) {
			this.date_updated = date_updated;
		}
		if(comments != null && comments.size() > 0) {
			this.comments = comments;
		}
		if(board_files != null && board_files.size() > 0) {
			this.board_files = board_files;
		}
		if(member != null && member.getEmail().length() > 0) {
			this.member = member;
		}
	}

	public Board(int idx, char category, String subject, String contents, int hits, int likes, String date_created,
			String date_updated, char notice, List<Comment> comments, List<Board_file> board_files, Member member) {
		super();
		this.idx = idx;
		this.category = category;
		this.subject = subject;
		this.contents = contents;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		if(date_updated != null) {
			this.date_updated = date_updated;
		}
		if(comments != null && comments.size() > 0) {
			this.comments = comments;
		}
		if(board_files != null && board_files.size() > 0) {
			this.board_files = board_files;
		}
		if(member != null && member.getEmail().length() > 0) {
			this.member = member;
		}
		this.notice = notice;
	}

	public Board(int idx, char category, String subject, String contents, String hashtag, int hits, int likes,
			String date_created, String date_updated, char notice, char public_check, List<Comment> comments,
			List<Board_file> board_files, Member member) {
		super();
		this.idx = idx;
		this.category = category;
		this.subject = subject;
		this.contents = contents;
		this.hashtag = hashtag;
		this.hits = hits;
		this.likes = likes;
		this.date_created = date_created;
		this.date_updated = date_updated;
		this.notice = notice;
		this.public_check = public_check;
		if(comments != null && comments.size() > 0) {
			this.comments = comments;
		}
		if(board_files != null && board_files.size() > 0) {
			this.board_files = board_files;
		}
		if(member != null && member.getEmail().length() > 0) {
			this.member = member;
		}
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public char getCategory() {
		return category;
	}

	public void setCategory(char category) {
		this.category = category;
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

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Board_file> getBoard_files() {
		return board_files;
	}

	public void setBoard_files(List<Board_file> board_files) {
		this.board_files = board_files;
	}

	public char getNotice() {
		return notice;
	}

	public void setNotice(char notice) {
		this.notice = notice;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public char getPublic_check() {
		return public_check;
	}

	public void setPublic_check(char public_check) {
		this.public_check = public_check;
	}
	
}
