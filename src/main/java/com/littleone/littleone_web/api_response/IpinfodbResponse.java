package com.littleone.littleone_web.api_response;

public class IpinfodbResponse {
	private String statusCode;
	private String statusMessage;
	private String ipAddress;
	private String countryCode;
	private String countryName;
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public IpinfodbResponse(String statusCode, String statusMessage, String ipAddress, String countryCode,
			String countryName) {
		super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.ipAddress = ipAddress;
		this.countryCode = countryCode;
		this.countryName = countryName;
	}
	public IpinfodbResponse() {
		super();
	}
}
