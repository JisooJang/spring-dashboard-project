package com.littleone.littleone_web.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.littleone.littleone_web.domain.Member;

public class MemberRegistRequest {
	
	@NotEmpty(message = "이메일은 필수 입력 사항입니다.")
	private String personal_email;
	
	@NotEmpty(message = "비밀번호는 필수 입력 사항입니다.")
	@Size(min = 8, max = 20, message = "길이는 6자리 ~ 20자리로 해야 합니다.")
	private String password;

	@NotEmpty(message = "닉네임은 필수 입력 사항입니다.")
	@Size(min = 1, max = 12, message = "길이는 1자리 ~ 12자리로 해야 합니다.")
	private String nickname;
	
	private String phone;
	
	private String user_name;

	private String user_birthdate;
	
	private String user_gender;
	
	public MemberRegistRequest(String personal_email, String password, String nickname, String phone, String user_name,
			String user_birthdate, String user_gender) {
		super();
		this.personal_email = personal_email;
		this.password = password;
		this.nickname = nickname;
		this.phone = phone;
		this.user_name = user_name;
		this.user_birthdate = user_birthdate;
		this.user_gender = user_gender;
	}

	public MemberRegistRequest(String personal_email, String password, String nickname, String phone, String user_name,
			String user_birthdate, String user_gender, String country_code) {
		super();
		this.personal_email = personal_email;
		this.password = password;
		this.nickname = nickname;
		this.phone = phone;
		this.user_name = user_name;
		this.user_birthdate = user_birthdate;
		this.user_gender = user_gender;
	}

	public MemberRegistRequest() {
		super();
	}

	public String getPersonal_email() {
		return personal_email;
	}
	public void setPersonal_email(String personal_email) {
		this.personal_email = personal_email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_birthdate() {
		return user_birthdate;
	}

	public void setUser_birthdate(String user_birthdate) {
		this.user_birthdate = user_birthdate;
	}

	public String getUser_gender() {
		return user_gender;
	}

	public void setUser_gender(String user_gender) {
		this.user_gender = user_gender;
	}

	public Member switchToMember() {
		// 본 객체를 도메인 객체인 Member로 전환. (DB컬럼과 1:1 대응)
		// setter메소드를 통해 필드 설정.
		// 휴대폰번호의 경우 phone1, 2, 3을 하나로 합쳐 phone 필드에 저장해야하고, 국가코드를 별도로 country_code로 저장해야함
		// join_date 가입날짜 저장 필요
		// 소셜 로그인의 경우 소셜코드 설정 필요
		Member member = new Member();
		member.setEmail(this.personal_email);
		member.setPassword(this.password);
		//member.setCountry_code("82");		//폼에서 휴대폰번호에 선택한 국가번호로 설정할것
		member.setNickname(this.nickname);
		
		if(this.phone != null && this.phone.length() > 10) {
			member.setPhone(this.phone);
		}
		
		member.setPhone(this.phone);
		
		if(user_name != null && user_name.length() > 0) {
			member.setName(this.user_name);
		} else {
			member.setName(null);
		}
		
		if(user_birthdate != null && user_birthdate.length() == 8) {
			member.setBirth(this.user_birthdate);
		} else {
			member.setBirth(null);
		}
		
		if(user_gender != null && user_gender.length() > 0) {
			if(user_gender.equals("f")) {
				member.setSex('f');
			} else if(user_gender.equals("m")) {
				member.setSex('m');
			} else {
				System.out.println("잘못된 성별입니다.");
				return null;
			}	
		} 
		
		return member;
	}
}
