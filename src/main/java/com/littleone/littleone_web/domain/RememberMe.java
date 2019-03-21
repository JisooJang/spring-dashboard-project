package com.littleone.littleone_web.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

@Entity
@Table(name = "remember_me")
public class RememberMe {
	@Id
	@Column(name = "series")
    private String series;
	
	@Column(name = "username", nullable = false, unique = true)
	private String username;		// email 속성 (회원아이디)
	
	@Column(name = "token", nullable = false)
    private String token;
	
	@Column(name = "last_used", nullable = false)
    private Date last_used;
    
	public RememberMe() {
		super();
	}

	public RememberMe(PersistentRememberMeToken persistentRememberMeToken) {
		this.username = persistentRememberMeToken.getUsername();
		this.series = persistentRememberMeToken.getSeries();
		this.token = persistentRememberMeToken.getTokenValue();
		this.last_used = persistentRememberMeToken.getDate();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getLast_used() {
		return last_used;
	}
	public void setLast_used(Date last_used) {
		this.last_used = last_used;
	}

}
