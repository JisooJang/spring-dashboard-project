package com.littleone.littleone_web.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
public class LocaleConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("locale/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setDefaultLocale(Locale.KOREAN); // 기본값 강제 한국어 설정.
		resolver.setCookieName("lang");
		return resolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		// request로 넘어오는 language parameter를 받아서 locale로 설정
		lci.setParamName("lang");
		return lci;
	}
	
	@Bean
	public DashBoardInterceptor dashboardInterceptor() {
		return new DashBoardInterceptor();
	}
	
	@Bean
	public InputValueAuthInterceptor inputValueAuthInterceptor() {
		return new InputValueAuthInterceptor();
	}
	
	@Bean
	public HandlerInterceptor csrfTokenAddingInterceptor() {
		return new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
				// TODO Auto-generated method stub
				CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if(token != null && modelAndView != null) {
					modelAndView.addObject(token.getParameterName(), token);
				}
				
			}

			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
					Exception ex) throws Exception {
				// TODO Auto-generated method stub
				
			}
			
		};
	}

	// 인터셉터 추가
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CertificationInterceptor())
		.addPathPatterns("/mypage", "/mypage/**", "/dashboard", "/dashboard/**", "/gallery", "/gallery/**", "/community", "/community/**")
		.excludePathPatterns("/", "/join", "/login", "/login/post", "/logout", "/naver_login", "/naver_login/**", "/find_password", "/find_id", "/googletokensignin", "/googletokensignin/invite_family", "/device/**")
		.excludePathPatterns("/auth/google_login", "/find_email", "/find_password_auth/**", "/social_login/auth", "/join_personal", "/join_personal/family_invite/**", "/join_enterprise", "/join_business/**", "/join/email_check", "/join_post", "/join_post/family_invite/**")
		.excludePathPatterns("/resources/**", "/css/**", "/data/**", "/js/**", "/scss/**", "/images/**", "/templates/**", "/static/**");
		
		registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
		//registry.addInterceptor(dashboardInterceptor()).addPathPatterns("/dashboard/**").excludePathPatterns("/dashboard/select/**");
		registry.addInterceptor(dashboardInterceptor()).addPathPatterns("/dashboard", "/dashboard/babyBook/**", "/dashboard/add_schedule", "/dashboard/select_next/**", "/dashboard/select_before/**");
		
		registry.addInterceptor(inputValueAuthInterceptor()).addPathPatterns("/**");
		
		//registry.addInterceptor(csrfTokenAddingInterceptor()).addPathPatterns("/**");
	}
}