package com.littleone.littleone_web.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.littleone.littleone_web.service.MemberService;

public class CertificationInterceptor extends HandlerInterceptorAdapter {

	private void saveDest(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String query = request.getQueryString();

		if(query == null || query.equals("null")) {
			query = "";
		} else {
			query = "?"+query;
		}

		String dest = uri+query;
		System.out.println("dest : " + dest);
		if(request.getMethod().equals("GET")) {
			request.getSession().setAttribute("dest", dest);
		}
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		// 컨트롤러 실행 직전에 동작
		// 반환 값이 true일 경우 정상적으로 컨트롤러 코드 진행, false일 경우 컨트롤러 진입 x
		HttpSession session = request.getSession();
		if(session.getAttribute("idx") != null && session.getAttribute("session_email") != null 
				&& session.getAttribute("session_type") != null) {
			return true;
		} else {
			saveDest(request);
			response.sendRedirect("/login");
			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		// 컨트롤러 진입 후 view가 랜더링 되기 전 수행
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}
}
