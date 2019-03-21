package com.littleone.littleone_web.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="member")
public class Member implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2245496860096409836L;

	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="email", nullable=false, unique=true)
	private String email;
	
	@Column(name="password", nullable=false)
	@JsonIgnore
	private String password;
	
	@Column(name="name")
	private String name;
	
	@Column(name="nickname")
	private String nickname;
	
	@Column(name="thumbnail")
	private String thumbnail;
	
	@Column(name="phone", nullable=false)
	private String phone;
	
	@Column(name="address1")
	private String address1;
	
	@Column(name="address2")
	private String address2;
	
	@Column(name="zipcode")
	private String zipcode;
	
	@Column(name="birth")
	private String birth;
	
	@Column(name="sex")
	private char sex;
	
	@Column(name="join_date", nullable=false)
	private String join_date;
	
	@Column(name="pw_renew_date", nullable=false)
	private String pw_renew_date;
	
	@Column(name="social_code", nullable=false)
	private char social_code;
	
	@Column(name="country_code", nullable=false)
	private String country_code;
	
	@Column(name="member_type", nullable=false)
	private char member_type;
	
	@JsonIgnore
	@Column(name="dup_info")
	private String dup_info;
	
	/*@ManyToMany
	@JoinTable(name="member_infant", joinColumns=@JoinColumn(name="member_idx"),
	inverseJoinColumns = @JoinColumn(name = "infant_idx"))
	private List<Infant> infants;*/
	@OneToMany(mappedBy="member")
	@JsonBackReference
	private List<Member_infant> member_infant;
	
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String passwordl) {
		this.password = passwordl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public String getJoin_date() {
		return join_date;
	}
	public void setJoin_date(String join_date) {
		this.join_date = join_date;
	}
	public String getPw_renew_date() {
		return pw_renew_date;
	}
	public void setPw_renew_date(String pw_renew_date) {
		this.pw_renew_date = pw_renew_date;
	}
	public char getSocial_code() {
		return social_code;
	}
	public void setSocial_code(char social_code) {
		this.social_code = social_code;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public char getMember_type() {
		return member_type;
	}
	public void setMember_type(char member_type) {
		this.member_type = member_type;
	}
	/*public void addInfants(Infant infant) {
		if(infants == null) {
			infants = new ArrayList<Infant>();
		} 
		infants.add(infant);
	}*/
	public List<Member_infant> getMember_infant() {
		return member_infant;
	}
	public void setMember_infant(List<Member_infant> member_infant) {
		this.member_infant = member_infant;
	}
	
	public String getDup_info() {
		return dup_info;
	}
	public void setDup_info(String dup_info) {
		this.dup_info = dup_info;
	}
	public Member(int idx, String email, String password, String name, String nickname, String thumbnail, String phone,
			String address1, String address2, String zipcode, String birth, char sex, String join_date,
			String pw_renew_date, char social_code, String country_code, char member_type, String dup_info,
			List<Member_infant> member_infant) {
		super();
		this.idx = idx;
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.thumbnail = thumbnail;
		this.phone = phone;
		this.address1 = address1;
		this.address2 = address2;
		this.zipcode = zipcode;
		this.birth = birth;
		this.sex = sex;
		this.join_date = join_date;
		this.pw_renew_date = pw_renew_date;
		this.social_code = social_code;
		this.country_code = country_code;
		this.member_type = member_type;
		this.dup_info = dup_info;
		this.member_infant = member_infant;
	}
	public Member() {
		super();
	}
	
}
