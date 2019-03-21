package com.littleone.littleone_web.api_request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ModifyDiaryRequest {
	private String file_url;
	private String idx;	
	private List<MultipartFile> update_file = null;

	public ModifyDiaryRequest() {
		super();
	}

	public ModifyDiaryRequest(String file_url, String idx, List<MultipartFile> update_file) {
		super();
		this.file_url = file_url;
		this.idx = idx;
		this.update_file = update_file;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public String getIdx() {
		return idx;
	}

	public void setIdx(String idx) {
		this.idx = idx;
	}

	public List<MultipartFile> getUpdate_file() {
		return update_file;
	}

	public void setUpdate_file(List<MultipartFile> update_file) {
		this.update_file = update_file;
	}
}
