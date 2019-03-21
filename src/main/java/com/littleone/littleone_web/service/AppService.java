package com.littleone.littleone_web.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Member_log;

@Service
public class AppService {
	
	@Autowired
	private MemberLogService logService;
	
	public boolean check_auth_app(HttpServletRequest request) {		
		// 헤더 사용시 언더바 사용 불가. member-idx, session-id로 헤더 추가하여 검증활것
		
		String member_idx = request.getParameter("member_idx");
		String session_id = request.getParameter("session_id");
		
		System.out.println(member_idx + " : " + session_id);
		if(member_idx == null || session_id == null) {
			return false;
		}
		
		Member_log log = logService.authSession(session_id, Integer.parseInt(member_idx));
		if(log == null) {
			System.out.println("로그가 없음");
		} else {
			System.out.println("로그존재 : 로그인 날짜 = " + log.getLogin_date());
		}

		if(log != null && member_idx != null && session_id != null && log.getMember_idx() == Integer.parseInt(member_idx) && log.getLogout_date() == null && log.getLog_type() == 'a') {
			System.out.println("인증 성공");
			return true;
		} else {
			System.out.println("인증 실패");
			return false;
		}
	}
	
	public boolean check_auth_app(String member_idx, String session_id) {
		System.out.println(member_idx + " : " + session_id);
		if(member_idx == null || session_id == null) {
			return false;
		}
		
		Member_log log = logService.authSession(session_id, Integer.parseInt(member_idx));
		if(log == null) {
			System.out.println("로그가 없음");
		} else {
			System.out.println("로그존재 : 로그인 날짜 = " + log.getLogin_date());
		}

		if(log != null && member_idx != null && session_id != null && log.getMember_idx() == Integer.parseInt(member_idx) && log.getLogout_date() == null && log.getLog_type() == 'a') {
			System.out.println("인증 성공");
			return true;
		} else {
			System.out.println("인증 실패");
			return false;
		}
	}
}
