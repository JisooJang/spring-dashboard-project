package com.littleone.littleone_web.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="infant_thumbnail")
public class Infant_thumbnail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5202279289238543108L;
	@Id
	@Column(name="infant_idx")
	private int infant_idx;
	@Column(name="origin_file", nullable=false)
	private String origin_file;
	@Column(name="server_file", nullable=false)
	private String server_file;
	@Column(name="url", nullable=false)
	private String url;
	
	public Infant_thumbnail(int infant_idx, String origin_file, String server_file, String url) {
		super();
		this.infant_idx = infant_idx;
		this.origin_file = origin_file;
		this.server_file = server_file;
		this.url = url;
	}
	public Infant_thumbnail() {
		super();
	}

	public int getInfant_idx() {
		return infant_idx;
	}
	public void setInfant_idx(int infant_idx) {
		this.infant_idx = infant_idx;
	}
	public String getOrigin_file() {
		return origin_file;
	}
	public void setOrigin_file(String origin_file) {
		this.origin_file = origin_file;
	}
	public String getServer_file() {
		return server_file;
	}
	public void setServer_file(String server_file) {
		this.server_file = server_file;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
