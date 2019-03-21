package com.littleone.littleone_web.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;

public class CustomAuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMngService mngService;
	
	@Autowired
	private PersistentTokenBasedRememberMeServices rememberMeServices;
	
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	private String failureURL = "/login";
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		Member member = memberService.findByEmail(email);
		
		if(member != null) {
			// 아이디는 존재하나 비밀번호 실패의 경우
			mngService.update_login_err_cnt(member.getIdx());	// 로그인 실패 횟수 증가
			int err_cnt = mngService.getLoginErrCount(member.getIdx());
			if(err_cnt >= 5) {
				// 5회 이상 실패시 로그인 차단
				request.setAttribute("login_result", "count_failed");
			} else {
				request.setAttribute("login_result", "failed");
				if(err_cnt > 0 && err_cnt < 5) {
					request.setAttribute("login_error_count", err_cnt);
				}
			}
		} else {
			request.setAttribute("login_result", "all_failed");
		}
		
		request.setAttribute("email", email);
		request.setAttribute("password", password);	
		request.getRequestDispatcher(failureURL).forward(request, response); 	// 로그인 실패 페이지로 포워딩
		
		rememberMeServices.loginFail(request, response);	
	}
	
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
