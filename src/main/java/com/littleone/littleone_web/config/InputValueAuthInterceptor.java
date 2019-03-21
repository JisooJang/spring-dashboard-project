package com.littleone.littleone_web.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MemberService;



public class InputValueAuthInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		// 컨트롤러 실행 직전에 동작
		// 반환 값이 true일 경우 정상적으로 컨트롤러 코드 진행, false일 경우 컨트롤러 진입 x
		
		// 입력값 검증. sql injection
		// 입력값이 존재하는 경우, length > 1인경우 특수문자, sql문 필터링
		// select, insert, update, delete, truncate, 작은따옴표, 큰 따옴표, =, 공백, ; 
		
		/*Enumeration<String> names = request.getAttributeNames();
		while(names.hasMoreElements()) {
			String element1 = request.getParameter(names.nextElement());
			if(element1 != null && element1.length() > 0) {
				String element = element1.toLowerCase();
				if(element.contains(";") || element.contains("select") || element.contains("update") || element.contains("delete")
						|| element.contains("insert") || element.contains("truncate") || element.contains("'") || element.contains("\"")
						|| element.contains("=") || element.contains("*")) {
					return false;
				}
			}
		}*/
		
		String request_uri = request.getRequestURL().toString();
		String locale = request.getParameter("lang");
		
		HttpSession session = request.getSession();

		if(request_uri.contains("/device/")) {
			session.setAttribute("menu", "device");
		} else if(request_uri.contains("/dashboard")) {
			session.setAttribute("menu", "dashboard");
		} else if(request_uri.contains("/community")) {
			session.setAttribute("menu", "community");
		} else if(request_uri.contains("/gallery")) {
			session.setAttribute("menu", "gallery");
		} else {
			session.setAttribute("menu", "none");
		}
		
		if(locale != null && locale.trim().length() > 0) {
			session.setAttribute("lang_value", locale);
		}
		
		return true;
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
