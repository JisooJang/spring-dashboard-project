package com.littleone.littleone_web.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
public class SecurityMember extends User {
	private static final long serialVersionUID = 1L;
	
	public SecurityMember(Member member) {
		//String username, String password, Collection<? extends GrantedAuthority> authorities
		super(member.getEmail(), member.getPassword(), SecurityMember.makeGrantedAuthority());
	}
	
	public static List<GrantedAuthority> makeGrantedAuthority() {
		List<GrantedAuthority> list = new ArrayList<>();
		GrantedAuthority authority_user = new SimpleGrantedAuthority("ROLE_USER");
		GrantedAuthority authority_corp = new SimpleGrantedAuthority("ROLE_CORP");
		GrantedAuthority authority_admin = new SimpleGrantedAuthority("ROLE_ADMIN");
		list.add(authority_user);
		list.add(authority_corp);
		list.add(authority_admin);
		return list;
		
	}
}
