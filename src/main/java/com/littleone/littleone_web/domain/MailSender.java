package com.littleone.littleone_web.domain;

import java.util.ArrayList;
import java.util.List;

public class MailSender {
	private String from;
	private List<String> to = new ArrayList<>();
	private String subject;
	private String content;
	
	public MailSender(String from, List<String> to, String subject, String content) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	public void addTo(String email) {
		this.to.add(email);
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
