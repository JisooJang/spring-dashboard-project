package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "thumbnail")
public class Thumbnail {
	
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="member_idx")
	private int member_idx;
	
	@Column(name="original_filename", nullable=false)
	private String original_filename;
	
	@Column(name="server_filename", nullable=false)
	private String server_filename;
	
	@Column(name="file_size", nullable=false)
	private int file_size;
	
	@Column(name="category", nullable=false)
	private char category;

	public Thumbnail(int member_idx, String original_filename, String server_filename, int file_size) {
		super();
		this.member_idx = member_idx;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_size = file_size;
	}

	public Thumbnail(int member_idx, String original_filename, String server_filename, int file_size, char category) {
		super();
		this.member_idx = member_idx;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_size = file_size;
		this.category = category;
	}

	public Thumbnail() {
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

	public int getFile_size() {
		return file_size;
	}

	public void setFile_size(int file_size) {
		this.file_size = file_size;
	}

	public char getCategory() {
		return category;
	}

	public void setCategory(char category) {
		this.category = category;
	}
}
