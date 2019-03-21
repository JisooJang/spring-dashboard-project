package com.littleone.littleone_web.domain;

import java.io.Serializable;

public class TmpFileInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String original_file_name;
	private String server_file_name;
	private long file_size;
	
	public TmpFileInfo(String original_file_name, String server_file_name, long file_size) {
		super();
		this.original_file_name = original_file_name;
		this.server_file_name = server_file_name;
		this.file_size = file_size;
	}
	public TmpFileInfo() {
		super();
	}
	public String getOriginal_file_name() {
		return original_file_name;
	}
	public void setOriginal_file_name(String original_file_name) {
		this.original_file_name = original_file_name;
	}
	public String getServer_file_name() {
		return server_file_name;
	}
	public void setServer_file_name(String server_file_name) {
		this.server_file_name = server_file_name;
	}
	public long getFile_size() {
		return file_size;
	}
	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}
}
