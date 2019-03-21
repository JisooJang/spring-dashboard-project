package com.littleone.littleone_web.config;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.SecurityMember;
import com.littleone.littleone_web.service.MemberService;

public class AuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
	
	@Autowired
	private MemberService memberService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// userDetailService의 loadUserByUsername에서 리턴한 유저 정보를 조회하여 받아와 authenticate메소드에서 유저가 입력한 비밀번호와 대조.
		// 인증에 성공하게 되면 authenticate메소드에서 authentication 객체를 리턴해야한다.
		
		// TODO Auto-generated method stub
		String email = authentication.getName();
		String password = authentication.getCredentials().toString();
		HashMap<Integer, Member> map = memberService.login(email, password);
		
		if(map.get(1) != null) {
			// 로그인 성공
			return new UsernamePasswordAuthenticationToken(email, password, SecurityMember.makeGrantedAuthority());
		} else {
			// 로그인 실패
			/*if(map.get(0) != null) {
				// 아이디는 존재하나 비밀번호 실패의 경우		
				
			}*/
			System.out.println("로그인 실패");
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}

}
