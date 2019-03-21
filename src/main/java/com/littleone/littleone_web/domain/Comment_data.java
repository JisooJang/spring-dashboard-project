package com.littleone.littleone_web.domain;

import java.io.Serializable;
import java.util.List;

public class Comment_data implements Serializable {
	
	private int result;
	
	//@JsonBackReference
	private List<Comment_check> comments;
	
	
	public Comment_data() {
		super();
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
	public List<Comment_check> getComments() {
		return comments;
	}
	
	public void setComments(List<Comment_check> comments) {
		this.comments = comments;
	}
}
