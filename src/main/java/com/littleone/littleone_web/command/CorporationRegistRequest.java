package com.littleone.littleone_web.command;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.littleone.littleone_web.domain.Corporation;
import com.littleone.littleone_web.domain.Member;

public class CorporationRegistRequest {
	@NotEmpty(message = "이메일은 필수 입력 사항입니다.")
	private String personal_email;
	
	@NotEmpty(message = "비밀번호는 필수 입력 사항입니다.")
	@Size(min = 6, max = 20, message = "길이는 6자리 ~ 20자리로 해야 합니다.")
	private String password;
	
	@NotEmpty(message = "이름은 필수 입력 사항입니다.")
	private String name;
	
	@NotEmpty(message = "휴대폰번호는 필수 입력 사항입니다.")
	private String phone;	// 숫자만 입력. 00000000000 형태로 DB에 들어가야 함. 국가코드를 포함
	
	@NotEmpty(message = "사업자등록번호는 필수 입력 사항입니다.")
	@Size(min = 10, max = 10, message = "길이는 10자리로 해야 합니다.")
	private String corp_num;
	
	@NotEmpty(message = "법인명은 필수 입력 사항입니다.")
	private String corp_name;
	
	@NotEmpty(message = "업종은 필수 선택 사항입니다.")
	private String field_code;
	
	@NotEmpty(message = "이용서비스는 필수 선택 사항입니다.")
	private int[] service_code;
	
	@NotEmpty(message = "담당자 이름은 필수 입력 사항입니다.")
	private String dname;
	
	@NotEmpty(message = "담당자 연락처는 필수 입력 사항입니다.")
	private String dphone;

	public String getPersonal_email() {
		return personal_email;
	}

	public void setPersonal_email(String personal_email) {
		this.personal_email = personal_email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCorp_num() {
		return corp_num;
	}

	public void setCorp_num(String corp_num) {
		this.corp_num = corp_num;
	}

	public String getCorp_name() {
		return corp_name;
	}

	public void setCorp_name(String corp_name) {
		this.corp_name = corp_name;
	}

	public String getField_code() {
		return field_code;
	}

	public void setField_code(String field_code) {
		this.field_code = field_code;
	}

	public int[] getService_code() {
		return service_code;
	}

	public void setService_code(int[] service_code) {
		this.service_code = service_code;
	}
	
	public String getDname() {
		return dname;
	}

	public void setDname(String dname) {
		this.dname = dname;
	}

	public String getDphone() {
		return dphone;
	}

	public void setDphone(String dphone) {
		this.dphone = dphone;
	}

	public Member switchToMember() {
		Member m = new Member();
		m.setEmail(this.personal_email);
		m.setPassword(this.password);
		m.setName(this.name);
		m.setPhone(this.phone);
		m.setCountry_code("82");
		return m;
	}
	
	public Corporation switchToCorporation() {
		Corporation corp = new Corporation();
		// setters...
		corp.setCorp_num(this.corp_num);
		corp.setCorp_name(this.corp_name);
		corp.setDname(this.dname);
		corp.setDphone(this.dphone);
		corp.setApproval('n');
		// field_code, country_code, service_code 값 정형화하여 저장할 것.
		if(field_code.equals("service_business")) { corp.setField_code('1'); }
		else if(field_code.equals("manufacturing_business")) { corp.setField_code('2'); }
		else if(field_code.equals("wholesale_reatil_business")){ corp.setField_code('3'); }
		else if(field_code.equals("etc")) { corp.setField_code('4'); }
		else {
			System.out.println("잘못된 업종 코드입니다.");
		}
		
		if(service_code.length == 2) { corp.setService_code('3');}
		else if(service_code.length == 1) {
			if(service_code[0] == 1) { corp.setService_code('1'); }
			else if(service_code[0] == 2) { corp.setService_code('2'); }
			else { System.out.println("잘못된 서비스 코드입니다."); }
		}
		else {
			System.out.println("잘못된 서비스 코드입니다.");
		}
		
		
		return corp;
	}

}
