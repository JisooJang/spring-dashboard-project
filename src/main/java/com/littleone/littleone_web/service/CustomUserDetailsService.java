package com.littleone.littleone_web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.SecurityMember;
import com.littleone.littleone_web.repository.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private MemberRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Member member = repository.findByEmail(email);
		if(member != null) {
			UserDetails userDetails = new SecurityMember(member);
			return userDetails;
		} else {
			throw new UsernameNotFoundException("유저 정보를 찾을 수 없습니다.");
		}
		
	}

}
