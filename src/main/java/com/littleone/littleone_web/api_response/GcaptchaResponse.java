package com.littleone.littleone_web.api_response;

public class GcaptchaResponse {
	private boolean success;
	private String challenge_ts;
	private String hostname;
	public GcaptchaResponse(boolean success, String challenge_ts, String hostname) {
		super();
		this.success = success;
		this.challenge_ts = challenge_ts;
		this.hostname = hostname;
	}
	public GcaptchaResponse() {
		super();
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getChallenge_ts() {
		return challenge_ts;
	}
	public void setChallenge_ts(String challenge_ts) {
		this.challenge_ts = challenge_ts;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
