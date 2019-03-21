package com.littleone.littleone_web.config;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.littleone.littleone_web.domain.GroupInfo;
import com.littleone.littleone_web.service.MemberLogService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.MemberServiceImpl;
import com.littleone.littleone_web.service.RedisSessionService;

public class CustomHttpSessionListener implements HttpSessionListener {

	private MemberLogService logService = null;

	private MemberService memberService = null;

	private RedisSessionService sessionService = null;

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		// 사용자 수동 로그아웃이 아닌 세션이 timeout 되었을때 들어오는 메소드
		HttpSession session = se.getSession();
		String sessionId = null;
		
		if(session == null) {
		} else if(session.getAttribute("idx") == null) {
			// org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN timeout			
		} else {
			// 회원 로그인 세션 timeout. 웹인 경우에만 아래 설정
			String db_session_id = (String) session.getAttribute("db_session_id");
			if(db_session_id != null) { 
				// 웹 세션인경우
				
				logService = new MemberLogService();
				memberService = new MemberServiceImpl();

				sessionId = session.getId();

				/*Member_log log = logService.findOne(db_session_id);	// nullPointerException
				if(log != null && log.getLog_type() == 'w') {	// 웹을 통한 로그 기록만 로그아웃 처리
					int result = logService.logout(sessionId, memberService.set_now_time());
					if(result != 1) {
						// db rollback
						System.out.println("log db update error : " + result);
					}
				}*/

				int member_idx = (int)session.getAttribute("idx");
				GroupInfo groupInfoBySession = (GroupInfo) session.getAttribute("dashboard_info");
				if(groupInfoBySession != null) {
					System.out.println("static 회원 map 변수 지움");
					session.removeAttribute("dashboard_info");
				}

				if(session.getAttribute("product_tmp_file") != null) {
					session.removeAttribute("product_tmp_file");
				}
				if(session.getAttribute("service_tmp_file") != null) {
					session.removeAttribute("service_tmp_file");
				}
			}

		}
	}


}
