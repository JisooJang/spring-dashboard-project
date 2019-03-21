package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Id_file")
public class Id_file {
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	@Column(name="diary_idx", nullable=false)
	private int diary_idx;
	@Column(name="original_filename", nullable=false)
	private String original_filename;
	@Column(name="server_filename", nullable=false)
	private String server_filename;
	@Column(name="file_url", nullable=false)
	private String file_url;
	@Column(name="represent", nullable=true)
	private char represent;
	
	public Id_file(int idx, int diary_idx, String original_filename, String server_filename, String file_url) {
		super();
		this.idx = idx;
		this.diary_idx = diary_idx;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_url = file_url;
	}
	
	public Id_file(int diary_idx, String original_filename, String server_filename, String file_url) {
		super();
		this.diary_idx = diary_idx;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_url = file_url;
	}
	
	public Id_file(int diary_idx, String original_filename, String server_filename, String file_url, char represent) {
		super();
		this.diary_idx = diary_idx;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_url = file_url;
		this.represent = represent;
	}

	public Id_file() {
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

	public char getRepresent() {
		return represent;
	}

	public void setRepresent(char represent) {
		this.represent = represent;
	}
}
