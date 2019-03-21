package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="board_file")
public class Board_file {
	
	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="board_idx", nullable=false)
	private int board_idx;
	
	@Column(name="original_filename", nullable=false)
	private String original_filename;
	
	@Column(name="server_filename", nullable=false)
	private String server_filename;
	
	@Column(name="file_url", nullable=false)
	private String file_url;
	
	@Column(name="file_size", nullable=false)
	private int file_size;
	
	public Board_file(int board_idx, String original_filename, String server_filename, String file_url,
			int file_size) {
		super();
		this.board_idx = board_idx;
		this.original_filename = original_filename;
		this.server_filename = server_filename;
		this.file_url = file_url;
		this.file_size = file_size;
	}

	public Board_file() {
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

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
}
