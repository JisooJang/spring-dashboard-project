package com.littleone.littleone_web.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="notify_email")
public class Notify_email {
	@Id
	@Column(name="email")
	private String email;
	@Column(name="date_created")
	private String date_created;
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDate_created() {
		return date_created;
	}

	public void setDate_created(String date_created) {
		this.date_created = date_created;
	}

	public Notify_email(String email, String date_created) {
		super();
		this.email = email;
		this.date_created = date_created;
	}

	public Notify_email(String email) {
		super();
		this.email = email;
	}

	public Notify_email() {
		super();
	}
}

