package com.littleone.littleone_web.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="infant")
public class Infant implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="idx")
	@GeneratedValue
	private int idx;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="birth", nullable=false)
	private String birth;
	
	@Column(name="sex", nullable=false)
	private char sex;
	
	@Column(name="nationality", nullable=true)
	private String nationality;
	
	@Column(name="weight", nullable=true)
	private float weight;
	
	@Column(name="height", nullable=true)
	private float height;
	
	@Column(name="blood_type", nullable=true)
	private String blood_type;
	
	@OneToOne
	@JoinColumn(name = "idx")
	private Infant_thumbnail thumbnail;
	
	/*@ManyToMany
	@JoinTable(name="member_infant", joinColumns=@JoinColumn(name="infant_idx"),
	inverseJoinColumns = @JoinColumn(name = "member_idx"))
	private List<Member> members = new ArrayList<Member>();*/
	
	@JsonIgnore
	@OneToMany(mappedBy="infant")
	private List<Member_infant> member_infant;
	
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
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
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public String getBlood_type() {
		return blood_type;
	}
	public void setBlood_type(String blood_type) {
		this.blood_type = blood_type.toLowerCase();
	}
	public Infant_thumbnail getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Infant_thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}
	public List<Member_infant> getMember_infant() {
		return member_infant;
	}
	public void setMember_infant(List<Member_infant> member_infant) {
		this.member_infant = member_infant;
	}
}
