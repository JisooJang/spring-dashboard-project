package com.littleone.littleone_web.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name="member_infant")
@IdClass(Member_infant_id.class)
public class Member_infant {
	
	@Id
	@ManyToOne
	@JoinColumn(name = "member_idx")
	@JsonManagedReference
	private Member member;
	
	/*@Id
	@Column(name="member_idx")
	private int member_idx;*/
	
	@Id
	@ManyToOne
	@JoinColumn(name = "infant_idx")
	private Infant infant;
	
	public Member_infant() {
		super();
	}

	public Member_infant(Member member, Infant infant) {
		super();
		this.member = member;
		this.infant = infant;
	}

	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	
	/*public int getMember_idx() {
		return member_idx;
	}

	public void setMember_idx(int member_idx) {
		this.member_idx = member_idx;
	}*/
	
	public Infant getInfant() {
		return infant;
	}

	public void setInfant(Infant infant) {
		this.infant = infant;
	}

}
