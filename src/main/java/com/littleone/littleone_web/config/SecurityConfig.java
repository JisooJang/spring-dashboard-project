package com.littleone.littleone_web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.littleone.littleone_web.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity	//springSecurityFilterChain 자동 포함
@EnableGlobalAuthentication
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CustomUserDetailsService userDetailsService(){
	      return new CustomUserDetailsService();
	}
	
	@Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
	
	@Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		return new RememberMeTokenRepository();
	}
	
	@Bean
	public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
	    PersistentTokenBasedRememberMeServices persistenceTokenBasedservice = new PersistentTokenBasedRememberMeServices("uniqueAndSecret", userDetailsService(), persistentTokenRepository());
	    persistenceTokenBasedservice.setParameter("auto_login");
	    persistenceTokenBasedservice.setAlwaysRemember(false);
	    persistenceTokenBasedservice.setCookieName("remember-me");
	    persistenceTokenBasedservice.setTokenValiditySeconds(60 * 60 * 24 * 7);		// 토큰 유효시간 1주일 설정
	    return persistenceTokenBasedservice;
	  }
	
	/*@Bean
	public AuthenticationProvider authenticationProvider() {
		return new AuthenticationProvider();
	}
	
	@Bean
	public LogoutSuccessHandler logoutSuccessHandler(){
	   return new LogoutSuccessHandler();
	}*/
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/css/**", "/data/**", "/js/**", "/scss/**", "/images/**", "/templates/**", "/static/**");	// 나중에 css, js등 정적 리소스 경로도 추가할것
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*http
        .authorizeRequests()
            .antMatchers("/", "/join", "/login", "/logout", "/naver_login", "/naver_login/failed", "/naver_login/callback", "/login/googletokensignin", "/find_password", "/find_id", "sign_in").permitAll()		// 지정된 경로는 별도의 /login 페이지로 인증이 필요없음을 명시
            .antMatchers("/mypage/**").authenticated()	// 지정된 경로는 로그인 시에만 접근 가능
            .anyRequest().authenticated()
            .and()
        .logout()
        	.logoutUrl("/logout")
        	.invalidateHttpSession(true)
        	.logoutSuccessUrl("/")
            .permitAll();
		*/
		//http.csrf().disable();		// 구글 로그인 기능시 csrf를 헤더에 넣거나 request param에 넣어서 인증해야함. 일시적으로 비사용으로 선언하고 테스트		
		http
		.headers()
			.cacheControl()		
			.and()
			.contentTypeOptions()
			.and()
			/*.httpStrictTransportSecurity()		// 웹 브라우저가 HTTPS 프로토콜만을 사용해서 서버와 통신하도록 하는 기능
			.and()*/
			.frameOptions()
			.sameOrigin()
			.and()
			
		.authorizeRequests()
			.antMatchers("/", "/join/**", "/naver_login/**", "/join_post", "/notify_email", "/login", "/login/**", "/find_password", "/find_id", "/find_email", "/find_email/**", "/app/s3_upload", "/about").permitAll()
			.antMatchers("/mypage/**", "/dashboard/**").authenticated()	// 지정된 경로는 로그인 시에만 접근 가능
			.and()
			
		.rememberMe()	// 자동로그인 설정
			.key("uniqueAndSecret")
			.tokenValiditySeconds(60 * 60 * 24 * 7)	// 기본 토큰 유효시간 설정(초) - 86400초는 하루. 604800초는 1주일. 설정안하면 기본 2주동안 유효
			.rememberMeParameter("auto_login")	// 로그인 html시 자동로그인 파라미터 이름
			.rememberMeCookieName("remember-me")
			.tokenRepository(persistentTokenRepository())
			.userDetailsService(userDetailsService())
			.rememberMeServices(persistentTokenBasedRememberMeServices())
			//.useSecureCookie(true)		//https 요청만 쿠키사용
			.and()
			
		.csrf()
			.ignoringAntMatchers("/getAuth/niceId/success", "/dynamodb/**", "/notify_email", "/app/**", "/admin/**", "/test/header")
			.and()
		.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login/post")
			.defaultSuccessUrl("/")
			.failureUrl("/login")
			.usernameParameter("email")
			.passwordParameter("password")
			.successHandler(authenticationSuccessHandler())
			.failureHandler(authenticationFailureHandler())
			.and()
		.logout()
	        .logoutUrl("/logout")
	        .invalidateHttpSession(true)
	        .logoutSuccessUrl("/")
	        .deleteCookies("remember-me")	// 자동로그인용 쿠키 삭제(쿠키이름)
	        .permitAll()
	        .and()
		.sessionManagement()
			.sessionFixation().changeSessionId()	// 세션 고정 공격 방지
			.invalidSessionUrl("/login")	 // 세션이 끊겼을때 이동할 경로
			.maximumSessions(1)	// 최대 허용 중복 세션 수
			.expiredUrl("/login_canceled")	// 중복로그인이 일어났을때 이동할 경로
			.maxSessionsPreventsLogin(false);	// true인 경우 기존 세션만 유지, false경우 새로운 세션만 유지(default)	
	}
	
	// 로그인 인증 처리
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(bCryptPasswordEncoder());
    }
 
}
