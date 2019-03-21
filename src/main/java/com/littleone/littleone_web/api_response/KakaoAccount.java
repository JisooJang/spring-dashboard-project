package com.littleone.littleone_web.api_response;

public class KakaoAccount {
	private boolean has_email;
	private boolean is_email_valid;
	private boolean is_email_verified;
	private String email;
	private boolean has_age_range;
	private String age_range;
	private boolean has_birthday;
	private String birthday;
	private boolean has_gender;
	private String gender;
	
	public boolean isHas_email() {
		return has_email;
	}
	public void setHas_email(boolean has_email) {
		this.has_email = has_email;
	}
	public boolean isIs_email_valid() {
		return is_email_valid;
	}
	public void setIs_email_valid(boolean is_email_valid) {
		this.is_email_valid = is_email_valid;
	}
	public boolean isIs_email_verified() {
		return is_email_verified;
	}
	public void setIs_email_verified(boolean is_email_verified) {
		this.is_email_verified = is_email_verified;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isHas_age_range() {
		return has_age_range;
	}
	public void setHas_age_range(boolean has_age_range) {
		this.has_age_range = has_age_range;
	}
	public String getAge_range() {
		return age_range;
	}
	public void setAge_range(String age_range) {
		this.age_range = age_range;
	}
	public boolean isHas_birthday() {
		return has_birthday;
	}
	public void setHas_birthday(boolean has_birthday) {
		this.has_birthday = has_birthday;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public boolean isHas_gender() {
		return has_gender;
	}
	public void setHas_gender(boolean has_gender) {
		this.has_gender = has_gender;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
}
