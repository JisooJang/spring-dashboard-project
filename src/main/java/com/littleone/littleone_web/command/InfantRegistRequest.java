package com.littleone.littleone_web.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class InfantRegistRequest {
	
	@NotEmpty(message = "이름은 필수 입력 사항입니다.")
	private String name;
	
	@NotEmpty(message = "생년월일은 필수 입력 사항입니다.")
	private String birth;
	
	@NotEmpty(message = "성별은 필수 선택 사항입니다.")
	private char sex;		// m 또는 f
	
	@NotEmpty(message = "국적은 필수 선택 사항입니다.")
	private String nationality;
	
	private String weight;	// DB insert시 float로 형변환 필요
	private String height;	// DB insert시 float로 형변환 필요
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBirth() {
		return birth;
	}
	public void setBirth(String birth) {
		this.birth = birth;
	}
	public char getSex() {
		return sex;
	}
	public void setSex(char sex) {
		this.sex = sex;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	

}
