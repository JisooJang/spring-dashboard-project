package com.littleone.littleone_web.api_request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiaryModifyFile {
	@JsonProperty("file_url") 
	private String file_url;
	
	@JsonProperty("idx") 
	private int idx;
	public DiaryModifyFile(String file_url, int idx) {
		super();
		this.file_url = file_url;
		this.idx = idx;
	}
	public DiaryModifyFile() {
		super();
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
}
