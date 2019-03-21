package com.littleone.littleone_web.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.littleone.littleone_web.api_response.GcaptchaResponse;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.domain.Member_log;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MemberLogService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.RedisSessionService;

@PropertySource("classpath:api_key.properties")
public class CustomAuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberMngService mngService;
	
	@Autowired
	private GroupService groupService;

	@Autowired
	private RedisSessionService sessionService;

	@Autowired
	private MemberLogService logService;
	
	@Autowired
	private PersistentTokenBasedRememberMeServices rememberMeServices;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private String failureURL = "/login";
	
	@Value("${google_recaptcha_request_url}")
	private String google_recaptcha_request_url;
	
	@Value("${google_recaptcha_secret_key}")
	private String google_recaptcha_secret_key;


	public CustomAuthenticationSuccessHandler() {
		super();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		/*HttpSession session = request.getSession();
		String email = request.getParameter("email");
		String original_dest = request.getParameter("original_dest");
		String g_recaptcha_response = request.getParameter("recaptcha-token"); 
		boolean google_captcha_result = false;
		Member member = memberService.findByEmail(email);
		int err_cnt = memberService.getLoginErrCount(member.getIdx());

		int result_type = 0;

		if(member.getMember_type() != 'p' && member.getMember_type() != 'c') {
			System.out.println("잘못된 멤버타입 또는 관리자계정");
			result_type = 1;
		}

		System.out.println("로그인 성공 sessionId = " + session.getId());
		if(err_cnt >= 5) {	// 로그인성공했으나 기존 에러 기록이 5회이상일경우
			result_type = 2;
			request.setAttribute("email", email);
			if(google_captcha(g_recaptcha_response) == true) {
				memberService.reset_login_err_cnt(member.getIdx());
				google_captcha_result = true;
				result_type = 6;
			}	
		} 
		if(err_cnt < 5 || (err_cnt >= 5 && google_captcha_result == true)) {
			// 로그인 로그 저장
			Member_log log = new Member_log(session.getId(), member.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
			log = logService.save_log(log);
			if(log == null) {
				System.out.println("로그 db 저장 에러");
				result_type = 3;
			}

			// last_login_date 필드 설정
			memberService.update_login_date(member.getIdx(), memberService.set_now_date());	// 현재 날짜로 설정

			// 필요한 세션값들 저장
			session.setAttribute("session_email", request.getParameter("email"));		// 개인 로그인시 세션 속성 이름 "personal", 기업 로그인시 세션 속성 이름 "corp"
			if(member.getMember_type() == 'p') {
				session.setAttribute("session_type", "personal");
			} else if(member.getMember_type() == 'c') {
				session.setAttribute("session_type", "corp");
			} 
			session.setAttribute("idx", member.getIdx());
			session.setAttribute("nickname", member.getNickname());
			String thumbnail = member.getThumbnail();
			if(thumbnail != null && thumbnail.length() > 0) {
				session.setAttribute("session_thumbnail", thumbnail);
			}

			// 회원이 그룹이 존재할시 group_idx 세션에 저장
			Member_group group = groupService.findMember_group(member.getIdx());
			if(group != null) {
				session.setAttribute("group_idx", group.getGroup_idx());
			}

			// 다른 페이지 요청중 세션이 끊겨 재로그인하는 경우, 원래 요청 페이지로 리다이렉트 처리
			if(original_dest != null && original_dest.length() > 0) {
				System.out.println("redirect:" + original_dest);
				result_type = 4;
			} else {
				// 일반 로그인 요청의 경우

				// 비밀번호 변경 날짜가 100일이 지난경우
				if(memberService.checkPw_renew_date(member.getPw_renew_date()) >= 100) {
					result_type = 5;

				} else {
					result_type = 6;
				}
			}
		}*/

		int result_type = get_result_code(request, response,authentication);
		setView(result_type, request, response, authentication);
	}

	public void setView(int result_type, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		switch(result_type) {
		case 1:
			request.setAttribute("login_result", "member_type_failed");
			request.getRequestDispatcher(failureURL).forward(request, response); 	// 로그인 실패 페이지로 포워딩
			break;
		case 2:
			request.setAttribute("login_result", "count_failed");
			request.getRequestDispatcher(failureURL).forward(request, response); 	// 로그인 실패 페이지로 포워딩
			break;
		case 3:
			request.setAttribute("login_result", "log_failed");
			request.getRequestDispatcher(failureURL).forward(request, response); 	// 로그인 실패 페이지로 포워딩
			break;
		case 4:	// 로그인성공 + 다른 페이지 요청중 세션이 끊겨 재로그인하는 경우, 원래 요청 페이지로 리다이렉트 처리
			redirectStrategy.sendRedirect(request, response, (String)session.getAttribute("dest"));
			//rememberMeServices.loginSuccess(request, response, authentication);
			session.removeAttribute("dest");
			break;
		case 5:	// 비밀번호 변경 날짜가 100일이 지난경우
			request.setAttribute("login_result", "password_renew_required");
			request.getRequestDispatcher("/login").forward(request, response);	// forwarding
			break;
		case 6:	// 일반 로그인 성공
			redirectStrategy.sendRedirect(request, response, "/");
			//rememberMeServices.loginSuccess(request, response, authentication);
			break;
		case 7:	// 로그인은 성공했으나 휴면회원처리
			request.setAttribute("dormant_chk", "true");
			request.getRequestDispatcher("/login").forward(request, response);	// forwarding
		}
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	// 구글 recaptcha rest 통신
	public boolean google_captcha(String g_recaptcha_response) {
		RestTemplate template = new RestTemplate();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("secret", google_recaptcha_secret_key);
		params.add("response", g_recaptcha_response);
		GcaptchaResponse response = template.postForObject(google_recaptcha_request_url, params, GcaptchaResponse.class);
		return response.isSuccess();
	}

	public int get_result_code(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(60 * 30);	// 로그인 세션 만료시간 30분으로 설정
		
		String email = request.getParameter("email");
		String original_dest = (String) session.getAttribute("dest");
		String g_recaptcha_response = request.getParameter("recaptcha-token"); 
		String auto_login = request.getParameter("auto_login");
		boolean google_captcha_result = false;
		Member member = memberService.findByEmail(email);
		Member_mng mng = mngService.findOne(member.getIdx());
		int err_cnt = mngService.getLoginErrCount(member.getIdx());

		if(member.getMember_type() != 'p' && member.getMember_type() != 'c') {
			return 1;
		}
		
		if(mng.getDormant_chk() == '1') {	
			// 휴면 회원인경우 (마지막 로그인 날짜가 1년전)
			session.setAttribute("dormant_member_idx", member.getIdx());
			request.setAttribute("last_login_date", mng.getLast_login_date());
			return 7;
		}
		
		if(err_cnt >= 5) {	// 로그인성공했으나 기존 에러 기록이 5회이상일경우
			request.setAttribute("email", email);
			if(google_captcha(g_recaptcha_response) == true) {
				mngService.reset_login_err_cnt(member.getIdx());
				google_captcha_result = true;
			} else {
				return 2;
			}
		} 
		if(err_cnt < 5 || (err_cnt >= 5 && google_captcha_result == true)) {
			// 로그인 로그 저장
			if(err_cnt > 0) {
				mngService.reset_login_err_cnt(member.getIdx());
			}
			
			String db_session_id = session.getId();
			Member_log log = new Member_log(db_session_id, member.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
			log = logService.save_log(log);
			session.setAttribute("db_session_id", db_session_id);
			if(log == null) {
				return 3;
			}

			// last_login_date 필드 설정
			mngService.update_login_date(member.getIdx(), memberService.set_now_date());	// 현재 날짜로 설정

			// 필요한 세션값들 저장
			session.setAttribute("session_email", request.getParameter("email"));		// 개인 로그인시 세션 속성 이름 "personal", 기업 로그인시 세션 속성 이름 "corp"
			if(member.getMember_type() == 'p') {
				session.setAttribute("session_type", "personal");
			} else if(member.getMember_type() == 'c') {
				session.setAttribute("session_type", "corp");
			} 
			session.setAttribute("idx", member.getIdx());
			session.setAttribute("nickname", member.getNickname());
			String thumbnail = member.getThumbnail();
			if(thumbnail != null && thumbnail.length() > 0) {
				session.setAttribute("session_thumbnail", thumbnail);
			}

			// 회원이 아이가 존재할시 infant_idx 세션에 저장
			List<Member_infant> mi = groupService.findByMember_idx(member.getIdx());
			boolean mi_chk = false;
			if(mi != null && mi.size() > 0) {
				session.setAttribute("infant_idx", mi.get(0).getInfant().getIdx());
				mi_chk = true;
			}
			
			// 회원이 그룹이 존재할시 group_idx, infant_idx 세션에 저장. 단 infant_idx는 위에서 아이정보를 세션에 저장하지 않았으면 별도 저장
			Member_group group = groupService.findMember_group(member.getIdx());
			if(group != null) {
				session.setAttribute("group_idx", group.getGroup_idx());
				if(mi_chk == false) {
					session.setAttribute("infant_idx", groupService.findInfantIdx(group.getGroup_idx()));
				}
			}
			
			if(mng != null) {
				session.setAttribute("point", mng.getPoint());
				session.setAttribute("grade", mng.getGrade());
			}

			// 다른 페이지 요청중 세션이 끊겨 재로그인하는 경우, 원래 요청 페이지로 리다이렉트 처리
			if(original_dest != null && original_dest.length() > 0) {
				System.out.println("redirect:" + original_dest);
				return 4;
			} else {
				// 일반 로그인 요청의 경우
				if(memberService.checkPw_renew_date(member.getPw_renew_date()) >= 100) {		// 비밀번호 변경 날짜가 100일이 지난경우
					request.setAttribute("pw_renew_date", member.getPw_renew_date());
					return 5;
				} else {
					return 6;	// 성공 코드
				}
			}
		}
		return 6;
	}

}
