package com.littleone.littleone_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.xerces.util.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.littleone.littleone_web.api_request.KakaoTokenRequest;
import com.littleone.littleone_web.api_response.GetProfileByNaverLogin;
import com.littleone.littleone_web.api_response.GetTokenByNaverLogin;
import com.littleone.littleone_web.api_response.GoogleUser;
import com.littleone.littleone_web.api_response.KaKaoProfileResponse;
import com.littleone.littleone_web.api_response.NaverProfileResponse;
import com.littleone.littleone_web.domain.GroupInfo;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_log;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.service.CorporationService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MailSenderService;
import com.littleone.littleone_web.service.MemberLogService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.RedisSessionService;

@PropertySource("classpath:api_key.properties")
@Controller
public class LoginController {
	// 1. 일반 DB 로그인, naver 로그인 , google 로그인 기능

	final int RESPONSE_UNAUTHORIZED = 401;
	final int RESPONSE_SUCCESS = 200;

	@Value("${NAVER_CLIENT_ID}")
	private String NAVER_CLIENT_ID;
	
	@Value("${NAVER_CLIENT_SECRET}")
	private String NAVER_CLIENT_SECRET;
	
	@Value("${NAVER_CALLBACK_URL}")
	private String NAVER_CALLBACK_URL;
	
	@Value("${NAVER_CALLBACK_INVITE_URL}")
	private String NAVER_CALLBACK_INVITE_URL;
	
	@Value("${NAVER_FAIL_URL}")
	private String NAVER_FAIL_URL;
	
	@Value("${NAVER_APP_CLIENT_ID}")
	private String NAVER_APP_CLIENT_ID;
	
	@Value("${NAVER_APP_SECRET}")
	private String NAVER_APP_SECRET;

	
	
	@Value("${GOOGLE_CLIENT_ID}")
	private String GOOGLE_CLIENT_ID;
	
	@Value("${GOOGLE_CLIENT_SECRET}")
	private String GOOGLE_CLIENT_SECRET;
	
	
	
	@Value("${KAKAO_APP_KEY}")
	private String KAKAO_APP_KEY;
	
	@Value("${KAKAO_REDIRECT_URL}")
	private String KAKAO_REDIRECT_URL;
	
	@Value("${KAKAO_CLIENT_SECRET}")
	private String KAKAO_CLIENT_SECRET;
	
	@Value("${KAKAO_REST_API_KEY}")
	private String KAKAO_REST_API_KEY;

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMngService memberMngService;

	@Autowired
	private MemberMngService mngService;

	@Autowired
	private CorporationService corpService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private RedisSessionService sessionService;

	@Autowired
	private MemberLogService logService;
	
	@Autowired
	PersistentTokenBasedRememberMeServices rememberMeService;

	@Autowired
	private MailSenderService mailSender;
	
	private RestTemplate restTemplate = new RestTemplate();

	//private Member member;

	private Map<String, Member> temp_social_member = new HashMap<String, Member>();


	@GetMapping("/auth/google_login")
	public String social_join_phone() {
		return "sign/social/google_join";
	}

	@GetMapping("/login")
	public String login_get() {
		return "sign/login";
	}

	// 중복로그인 처리
	@GetMapping("/login_canceled")
	public void login_canceled(HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = null;

		try {
			out = response.getWriter();
			out.println("<script type='text/javascript' charset='utf-8'>");
			out.println("alert('중복 로그인이 인식되어 로그아웃 처리 되었습니다.');");
			out.println("window.location.href='./login';");
			out.println("</script>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	@PostMapping("/login")
	public String login_post() {    // 로그인 에러메시지 처리(에러메시지 post 전송)
		return "sign/login";
	}

	/*@PostMapping("/login/post")
	public String login_post(HttpServletRequest request, @RequestParam("email") String email, @RequestParam("password") String password, 
			@RequestParam(name="original_dest", required=false) String original_dest, Model model, HttpSession session) throws ParseException {

		if(!memberService.checkinputValue(email) || !memberService.checkinputValue(password)) {
			return "error";
		}

		String now_date = memberService.set_now_date();
		HashMap<Integer, Member> map = memberService.login(email, password);

		if(map.get(1) != null) {
			Member member = map.get(1); 
			if(member.getMember_type() != 'p' && member.getMember_type() != 'c') {
				System.out.println("잘못된 멤버타입");
				return "error";
			}

			// 중복 로그인 체크후 기존 세션 중지
			// 전체세션에서 해당 idx값이 있는지 검사. 있으면 해당 세션ID를 가져옴
			String sessionId = sessionService.findByIdx(map.get(1).getIdx());
			//sessionService.delete_session(sessionId);


			// 로그인 로그 저장
			Member_log log = new Member_log(member.getIdx(), memberService.getClientIp(request), memberService.getClientBrowser(request), '1', memberService.set_now_time());
			log = logService.save_log(log);
			if(log == null) {
				System.out.println("로그 db 저장 에러");
				return "log_error";
			}

			// last_login_date 필드 설정
			memberService.update_login_date(member.getIdx(), now_date);	// 현재 날짜로 설정

			session.setAttribute("session_email", email);		// 개인 로그인시 세션 속성 이름 "personal", 기업 로그인시 세션 속성 이름 "corp"
			if(member.getMember_type() == 'p') {
				session.setAttribute("session_type", "personal");
			} else if(member.getMember_type() == 'c') {
				session.setAttribute("session_type", "corp");
			} 
			session.setAttribute("idx", member.getIdx());
			String thumbnail = member.getThumbnail();
			if(thumbnail != null && thumbnail.length() > 0) {
				session.setAttribute("session_thumbnail", thumbnail);
			}

			// 회원이 그룹이 존재할시 group_idx 세션에 저장
			Member_group group = groupService.findMember_group(member.getIdx());
			if(group != null) {
				session.setAttribute("group_idx", group.getGroup_idx());
			}

			// 비밀번호 변경 날짜가 100일이 지난경우
			if(memberService.checkPw_renew_date(member.getPw_renew_date()) >= 100) {
				model.addAttribute("login_result", "password_renew_required");
				return "sign/login";
			}

			// 다른 페이지 요청중 세션이 끊겨 재로그인하는 경우, 원래 요청 페이지로 리다이렉트 처리
			if(original_dest != null && original_dest.length() > 0) {
				System.out.println("redirect:" + original_dest);
				session.removeAttribute("dest");
				return "redirect:" + original_dest;
			} else {
				return "redirect:/";
			}

		} else {
			// 로그인 실패
			if(map.get(0) != null) {
				// 아이디는 존재하나 비밀번호 실패의 경우
				memberService.update_login_err_cnt(map.get(0).getIdx());	// 로그인 실패 횟수 증가
				int err_cnt = memberService.getLoginErrCount(map.get(0).getIdx());
				if(err_cnt >= 5) {
					// 5회 이상 실패시 로그인 차단
					model.addAttribute("login_result", "count_failed");
					return "sign/login";
				}
				if(err_cnt > 0) {
					model.addAttribute("login_error_count", err_cnt);
				}
			}
			model.addAttribute("email", email);
			model.addAttribute("password", password);
			model.addAttribute("login_result", "failed");

			return "sign/login";
		}
	}*/

	@ResponseBody
	@PostMapping("/login/dormant_chk_false")
	public String dormant_chk_false(@RequestParam("member_idx") int member_idx, HttpServletRequest request, HttpSession session) {
		if ((int) session.getAttribute("dormant_member_idx") == member_idx) {
			mngService.update_dormant_chk_false(member_idx);
			session.removeAttribute("dormant_member_idx");

			Member m = memberService.findByIdx(member_idx);

			Member_log log = new Member_log(session.getId(), m.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
			log = logService.save_log(log);

			mngService.update_login_date(m.getIdx(), memberService.set_now_date());
			session.setAttribute("session_email", m.getEmail());

			if (m.getMember_type() == 'p') {
				session.setAttribute("session_type", "personal");
			} else if (m.getMember_type() == 'c') {
				session.setAttribute("session_type", "corp");
			}

			session.setAttribute("nickname", m.getNickname());
			session.setAttribute("idx", member_idx);

			String thumbnail = m.getThumbnail();
			if (thumbnail != null && thumbnail.length() > 0) {
				session.setAttribute("session_thumbnail", thumbnail);
			}

			Member_group group = groupService.findMember_group(m.getIdx());
			if (group != null) {
				session.setAttribute("group_idx", group.getGroup_idx());
			}

			Member_mng mng = mngService.findOne(m.getIdx());
			if (mng != null) {
				session.setAttribute("point", mng.getPoint());
				session.setAttribute("grade", mng.getGrade());
			}

			return "success";
		} else {
			return "failed";
		}
	}

	@ResponseBody
	@GetMapping("/login/password_renew_delay")	// 비밀번호 4주간 표시안함
	public String pw_renew_delay(HttpSession session) throws ParseException {
		Integer member_idx = (Integer) session.getAttribute("idx");
		
		if (member_idx != null) {
			memberService.update_pw_renew_date_1month(member_idx);
			return "success";
		} else {
			return "auth_failed";
		}
	}


	@GetMapping("/logout")    // 사용자가 수동으로 로그아웃 버튼을 눌렀을때 처리
	public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// 세션 삭제, redis에 기록된 세션 삭제
		String sessionId = session.getId();

		String db_session_id = (String) session.getAttribute("db_session_id");
		int member_idx = (int) session.getAttribute("idx");

		// 리멤버미 쿠키가 존재하면 삭제 처리
		Cookie[] cookies = request.getCookies(); 
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("remember-me")) {
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}

		// 로그아웃 로그 저장
		int result = logService.logout(db_session_id, memberService.set_now_time());
		if (result != 1) {
			return "db_error";
		}

		GroupInfo groupInfo = (GroupInfo) session.getAttribute("dashboard_info");
		if (groupInfo != null) {
			session.removeAttribute("dashboard_info");
		}
		
		// 자동로그인이 설정되어있을 경우, 리멤버미 토큰 로그아웃 처리
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		rememberMeService.logout(request, response, authentication);

		// 세션 삭제
		sessionService.delete_session(sessionId);
		session.invalidate();
		
		return "logout";
	}

	@GetMapping("/find_email")    // 비밀번호, 아이디 찾기 뷰로 요청
	public String get_find_email(Model model, HttpServletRequest request) {
		// firebase locale setting
		model.addAttribute("locale", memberService.getCookieLocale(request));
		return "find_password/find";
	}

	// 아이디찾기에서 휴대폰인증 성공시 받은 토큰을 서버 세션에 저장 (모든 휴대폰인증시 토큰 비교용으로 공통으로 사용됨)
	@ResponseBody
	@PostMapping("/find_email/send_token") 
	public String get_firebase_success_token(@RequestParam("token") String token, HttpServletResponse response, HttpSession session) {
		//response.addHeader("Access-Control-Allow-Origin", "http://littleone.kr");
		session.setAttribute("firebase_success_token", token);
		return "success";
	}

	@ResponseBody
	@PostMapping("/find_email")    // 비밀번호 찾기 뷰로 요청
	public String find_email(@RequestParam("token") String token, @RequestParam("phone") String phone, HttpSession session) {
		String session_token = (String) session.getAttribute("firebase_success_token");
		if (memberService.auth_firebase_token(token, phone, session) == 1) {
			List<String> result_list = memberService.find_email(phone);    // 검색된 이메일 리스트 반환
			if(result_list == null || result_list.size() == 0) {
				return "no_result";
			}

			if(result_list.size() > 1) {
				StringBuffer results = new StringBuffer();
				for(int i=0 ; i < result_list.size() ; i++) {
					results.append(result_list.get(i));
					if(i < (result_list.size() - 1)) {
						results.append(",");
					}
				}
				return results.toString();
			} else {
				return result_list.get(0);
			}

		} else {
			return "auth_failed";
		}

		/*for (int i = 0; i < result_list.size(); i++) {        // @기준으로 왼쪽의 아이디 뒤에서부터 3분의1만 별표처리
            int index = result_list.get(i).indexOf('@');
            int sub_count = (index - 1) / 3;
            StringBuffer left = new StringBuffer();
            left.append(result_list.get(i).substring(0, (index - 1) - sub_count));
            //String left = result_list.get(i).substring(0, (index-1)-sub_count);
            for (int j = index - 1; j >= (index - 1) - sub_count; j--) {
                //left += '*';
                left.append("*");
            }
            String right = result_list.get(i).substring(index, result_list.get(i).length());

            result_list2.add(left.toString() + right);
        }
        model.addAttribute("result_list", result_list2);
		 */           
	}

	@GetMapping("/find_password")    // 비밀번호, 아이디 찾기 뷰로 요청
	public String get_find_password(Model model, HttpServletRequest request) {
		// firebase locale setting
		model.addAttribute("locale", memberService.getCookieLocale(request));
		return "find_password/find_password";
	}

	@ResponseBody
	@PostMapping("/find_password/byPhone")	// 비밀번호찾기에서 휴대폰 인증시 요청되는 ajax 요청
	public String find_password_byPhone(@RequestParam("token") String token, @RequestParam("phone") String phone, @RequestParam("email") String email, HttpSession session) {
		int result = memberService.auth_firebase_token(token, phone, session);
		if(result == 1) {
			Member m = memberService.findByEmailAndPhone(email, phone);
			if(m != null) {
				if(m.getSocial_code() != 'x') {
					return "social_member";
				}
				session.setAttribute("pwfind_email", email);    // 세션에 이메일 임시 저장. 세션 만료후, 비밀번호 변경후 삭제됨
				session.setMaxInactiveInterval(60 * 30);    // 세션만료 30분 설정
				return "success";
			} else {
				return "no_user_result";
			}
			//return "find_password/find_ok";    // 비밀번호 변경 페이지로 이동
		} else {
			return "auth_failed";
		}

	}

	// 위에서 휴대폰인증 성공("success") 리턴시 비밀번호 변경 페이지로 이동
	@GetMapping("/find_password/byPhone/success")
	public String findPasswordByPhoneSuccess(HttpSession session) {
		String find_email = (String) session.getAttribute("pwfind_email");	
		if(find_email != null) {
			return "find_password/find_ok"; 

		} else {
			return "error/500page";
		}

	}

	@ResponseBody    // 이메일 입력후 비밀번호 찾기 버튼 클릭시 ajax 처리 (메일 전송)
	@PostMapping("/find_password")        // 유효시간 설정 필요. 설정한 시간이 지나면 인증 불가
	public String post_find_password(@RequestParam("pwfind_email") String email, HttpSession session) throws IOException {
		// 1. 메일이 가입된 메일에 있는지 체크
		// 2. 메일로 임시비밀번호를  보내주고, 메일 내 html 코드에서 인증버튼 클릭시 DB에 해당 회원의 임시 비밀번호로 변경
		// 3. 동일한 세션아이디 / 이메일로 찾기 3번이상시 제한
		Member member = memberService.findByEmail(email);
		if (member != null && member.getSocial_code() == 'x') {
			String temp_token = memberService.getUniqState() + memberService.getUniqState();
			String subject = "[Littleone] 비밀번호 찾기 요청";
			String password_modify_url = "https://littleone.kr/find_password_auth/" + temp_token;

			Collection<String> des = new ArrayList<String>();
			des.add(email);

			Map<String, String> template_data = new HashMap<String, String>();
			template_data.put("password_modify_url", password_modify_url);
			template_data.put("member_name", member.getName());

			mailSender.sendFindPWMailBySES(des, "smart@littleone.kr", "findPasswordTemplate", template_data);

			
			session.setAttribute("temp_token", temp_token);    // 세션에 토큰 임시 저장. 세션 만료후, 비밀번호 변경후 삭제됨 (이메일로 비번 찾기시에만 추가되는 세션값)
			session.setAttribute("pwfind_email", email);    // 세션에 이메일 임시 저장. 세션 만료후, 비밀번호 변경후 삭제됨
			session.setMaxInactiveInterval(60 * 30);    // 세션만료 30분 설정
			return "success";
		} else {
			return "failed";
		}
	}

	@GetMapping("/find_password_auth/{token}")    // 임시비밀번호를 전송한 메일에서 인증 URL을 눌렀을 때 처리
	public String post_find_password_auth(@PathVariable("token") String temp_token, HttpSession session) {
		String session_token = (String) session.getAttribute("temp_token");
		if (temp_token.equals(session_token)) {
			return "find_password/find_ok";    // 비밀번호 변경 페이지로 이동
		} else {
			return "error/404page";
		}

	}

	/* // find_password/find_ok 페이지에서 폼작성후 submit 요청시
    @ResponseBody
    @PostMapping("/find_password_auth/{token}")
    public String find_password_modify(@PathVariable("token") String temp_token, @RequestParam("new_password") String new_password, HttpSession session) {
        String session_token = (String) session.getAttribute("temp_token");
        String email = (String) session.getAttribute("pwfind_email");
        if (email != null && temp_token.equals(session_token)) {
            int member_idx = memberService.getIdx(email);
            int result = memberService.update_password(member_idx, new_password, memberService.set_now_date());

            // 세션 삭제
            session.removeAttribute("temp_token");
            session.removeAttribute("pwfind_email");
            sessionService.delete_session(session.getId());

            if (result == 1) {
                System.out.println("비밀번호를 성공적으로 변경하였습니다.");
                return "success"; // 비밀번호 변경 완료 페이지로 이동
            } else {
                System.out.println("변경 오류. 변경된 행의 갯수 : " + result);
                // DB 롤백 필요
                return "db_error";
            }
        } else {
            System.out.println("세션에 이메일이 없음");
            return "no_session_error";
        }
    }*/


	// 폰으로 찾을때, 이메일로 찾을때 공통경로로 처리할것.
	@ResponseBody
	@PostMapping("/find_password/success")
	public String findPasswordByPhoneSuccessPost(@RequestParam("new_password") String new_password, HttpSession session, Model model) {
		String email = (String) session.getAttribute("pwfind_email");
		if (email != null) {

			if(memberService.checkPassword(new_password) == false) {
				return "password_validation_error";
			}

			int member_idx = memberService.getIdx(email);
			int result = memberService.update_password(member_idx, new_password, memberService.set_now_date());

			// 세션 삭제
			session.removeAttribute("pwfind_email");

			if(session.getAttribute("temp_token") != null) {
				session.removeAttribute("temp_token");
			}

			if (result == 1) {
				return "success"; // 비밀번호 변경 완료 페이지로 이동
			} else {
				// DB 롤백 필요
				return "db_error";
			}
		} else {
			return "no_session_error";
		}

	}

	@GetMapping("/find_password_auth/test")    // 임시비밀번호를 전송한 메일에서 인증 URL을 눌렀을 때 처리
	public String post_find_password_auth_test() {
		return "find_password/find_ok";    // 비밀번호 변경 페이지로 이동
	}

	@GetMapping("/naver_login")
	public String naver_login(HttpSession session) {    //1. 네이버 로그인 버튼 클릭시 이동할 네이버 로그인 URL 생성
		StringBuffer url = new StringBuffer();
		url.append("https://nid.naver.com/oauth2.0/authorize?response_type=code");
		String redirect_url = null;
		String state = memberService.getUniqState();

		try {
			redirect_url = URLEncoder.encode(NAVER_CALLBACK_URL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		url.append("&client_id=" + NAVER_CLIENT_ID);
		url.append("&state=" + state);    //url 인코딩 필요
		url.append("&redirect_uri=" + redirect_url);    //url 인코딩 필요

		session.setAttribute("state", state);    // 세션에 state 코드 저장

		return "redirect:" + url.toString();    // 네이버아이디로 로그인 인증 요청 page로 redirect
	}

	@GetMapping("/naver_login/family_invite/{inviter_idx}")
	public String naver_login_family_invite(@PathVariable("inviter_idx") int inviter_idx, HttpSession session) {
		StringBuffer url = new StringBuffer();
		String redirect_url = null;
		String state = memberService.getUniqState();

		try {
			redirect_url = URLEncoder.encode(NAVER_CALLBACK_INVITE_URL, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		url.append("&client_id=" + NAVER_CLIENT_ID);
		url.append("&state=" + state);    //url 인코딩 필요
		url.append("&redirect_uri=" + redirect_url);    //url 인코딩 필요

		session.setAttribute("state", state);    // 세션에 state 코드 저장
		session.setAttribute("inviter_idx", inviter_idx);    // 초대자 회원번호 저장

		return "redirect:" + url.toString();    // 네이버아이디로 로그인 인증 요청 page로 redirect
	}

	@GetMapping("/naver_login/failed")
	public void naver_login_failed(HttpServletResponse response) {
		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.println("<script>alert('잘못된 네이버 로그인 인증입니다.'); location.href='https://littleone.kr/login';</script>");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			out.flush();
			out.close();
		}
	}

	@RequestMapping("/naver_login/callback")
	public String naver_login_callback(HttpServletRequest request, @RequestParam("state") String state, @RequestParam("code") String code, HttpSession session, Model model) {     
		// 세션 또는 별도의 저장 공간에서 상태 토큰을 가져옴
		String storedState = (String) session.getAttribute("state");

		if (!state.equals(storedState)) {    // 토큰 비교
			//401 unauthorized
			return "redirect:" + NAVER_FAIL_URL;
		} else {
			//200 success
			StringBuffer success_url = new StringBuffer();
			success_url.append("https://nid.naver.com/oauth2.0/token?");
			success_url.append("client_id=" + NAVER_CLIENT_ID);
			success_url.append("&client_secret=" + NAVER_CLIENT_SECRET);
			success_url.append("&grant_type=authorization_code");
			success_url.append("&state=" + state);
			success_url.append("&code=" + code);

			GetTokenByNaverLogin response = restTemplate.getForObject(success_url.toString(), GetTokenByNaverLogin.class);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + response.getAccess_token());        // 헤더 설정 필요
			HttpEntity<Void> requestEntity = new HttpEntity<>((Void) null, headers);

			ResponseEntity<GetProfileByNaverLogin> profileResponse = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, requestEntity, GetProfileByNaverLogin.class);
			GetProfileByNaverLogin profile = profileResponse.getBody();        // 응답이 성공적으로 오면 사용자 프로필이 성공적으로 저장됨.

			NaverProfileResponse profile_response = profile.getResponse();
			
			String email = profile_response.getEmail();
			int idx = memberService.getIdx(email);

			session.removeAttribute("state");	// 네이버 로그인 인증값 세션에서 지움

			if (memberService.findByEmail(email) == null) {    // 소셜로 처음 가입한 상태
				//profile 정보를 기반으로 member_db에 추가
				Member member = new Member();
				String now_date = memberService.set_now_date();
				member.setEmail(email);
				member.setPassword(memberService.encode_password("1234"));        // 비밀번호 어떻게 저장할지 고려해볼 것

				member.setName(profile_response.getName());
				member.setJoin_date(now_date);
				member.setPw_renew_date(now_date);
				member.setSocial_code('n');
				member.setMember_type('p');
				member.setCountry_code("ko");    // 국가코드 어떻게 저장할지 고려해볼 것

				temp_social_member.put(session.getId(), member);

				// 폼 초기값 설정
				model.addAttribute("social_type", "naver");
				model.addAttribute("naver_name", profile_response.getName());
				model.addAttribute("naver_email", email);
				
				// firebase locale setting
				model.addAttribute("locale", memberService.getCookieLocale(request));
				
				String user_birth = profile_response.getBirthday();
				if(user_birth != null && user_birth.trim().length() > 0) {
					String naver_birth_month = user_birth.substring(0, 2);
					String naver_birth_day = user_birth.substring(3);
					
					model.addAttribute("naver_birth_month", naver_birth_month);
					model.addAttribute("naver_birth_day", naver_birth_day);
				}
				String naver_gender = profile_response.getGender();
				if(naver_gender != null && naver_gender.trim().length() > 0) {
					model.addAttribute("naver_gender", naver_gender);
				}

				String naver_nickname = profile_response.getNickname();
				if(naver_nickname != null && naver_nickname.trim().length() > 0) {
					model.addAttribute("naver_nickname", naver_nickname);
				}

				String naver_thumbnail = profile_response.getProfile_image();
				if(naver_thumbnail != null && naver_thumbnail.trim().length() > 0) {
					member.setThumbnail(naver_thumbnail);
				}

				return "sign/social/social_join";

			} else {    // 소셜로 로그인
				Member member = memberService.findByIdx(idx);

				// 로그인 로그 저장
				String db_session_id = session.getId();
				session.setAttribute("db_session_id", db_session_id);
				Member_log log = new Member_log(db_session_id, member.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
				log = logService.save_log(log);
				if (log == null) {
					return "log_error";
				}

				Member_mng mng = mngService.findOne(idx);
				if(mng != null) {
					session.setAttribute("point", mng.getPoint());
					session.setAttribute("grade", mng.getGrade());
				}

				session.setAttribute("session_email", email);
				session.setAttribute("session_type", "personal_s");
				session.setAttribute("nickname", member.getNickname());
				session.setAttribute("idx", idx);

				if (member.getThumbnail() != null && member.getThumbnail().length() > 0) {
					session.setAttribute("session_thumbnail", member.getThumbnail());
				}

				// 회원이 그룹이 존재할시 group_idx 세션에 저장
				Member_group group = groupService.findMember_group(member.getIdx());
				if (group != null) {
					session.setAttribute("group_idx", group.getGroup_idx());
				}

				memberService.set_spring_security_auth(email, session);

				return "redirect:/";
			}
		}
	}

	@RequestMapping("/naver_login/callback/family_invite")
	public String naver_login_callback_invite(HttpServletRequest request, @RequestParam("state") String state, @RequestParam("code") String code, HttpSession session, Model model) {
		// 세션 또는 별도의 저장 공간에서 상태 토큰을 가져옴
		String storedState = (String) session.getAttribute("state");

		if (!state.equals(storedState)) {    // 토큰 비교
			//401 unauthorized
			return "redirect:" + NAVER_FAIL_URL;
		} else {
			//200 success
			String success_url = "https://nid.naver.com/oauth2.0/token?";
			success_url += "client_id=" + NAVER_CLIENT_ID;
			success_url += "&client_secret=" + NAVER_CLIENT_SECRET;
			success_url += "&grant_type=authorization_code";
			success_url += "&state=" + state;
			success_url += "&code=" + code;

			GetTokenByNaverLogin response = restTemplate.getForObject(success_url, GetTokenByNaverLogin.class);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + response.getAccess_token());        // 헤더 설정 필요
			HttpEntity<Void> requestEntity = new HttpEntity<>((Void) null, headers);

			ResponseEntity<GetProfileByNaverLogin> profileResponse = restTemplate.exchange("https://openapi.naver.com/v1/nid/me", HttpMethod.GET, requestEntity, GetProfileByNaverLogin.class);
			GetProfileByNaverLogin profile = profileResponse.getBody();        // 응답이 성공적으로 오면 사용자 프로필이 성공적으로 저장됨.

			NaverProfileResponse profile_response = profile.getResponse();
			
			String email = profile_response.getEmail();
			int idx = memberService.getIdx(email);
			if (memberService.findByEmail(email) == null) {    // 소셜로 처음 가입한 상태
				//profile 정보를 기반으로 member_db에 추가
				Member member = new Member();
				String now_date = memberService.set_now_date();
				member.setEmail(email);
				member.setPassword(memberService.encode_password("1234"));        // 비밀번호 어떻게 저장할지 고려해볼 것

				// 이메일, 이름, 성별, 생일 네이버로부터 제공받는 정보
				member.setName(profile_response.getName());
				member.setJoin_date(now_date);
				member.setPw_renew_date(now_date);
				member.setSocial_code('n');
				member.setMember_type('p');
				member.setCountry_code("ko");    // 국가코드 어떻게 저장할지 고려해볼 것

				temp_social_member.put(session.getId(), member);

				// 폼 초기값 설정
				model.addAttribute("social_type", "naver");
				model.addAttribute("naver_name", profile_response.getName());
				
				// firebase locale setting
				model.addAttribute("locale", memberService.getCookieLocale(request));

				String user_birth = profile_response.getBirthday();
				if(user_birth != null && user_birth.trim().length() > 0) {
					model.addAttribute("naver_birth", user_birth);
				}
				String naver_gender = profile_response.getGender();
				if(naver_gender != null && naver_gender.trim().length() > 0) {
					model.addAttribute("naver_gender", naver_gender);
				}

				String naver_thumbnail = profile_response.getProfile_image();
				if(naver_thumbnail != null && naver_thumbnail.trim().length() > 0) {
					member.setThumbnail(naver_thumbnail);
				}

				return "sign/social/social_join";

			} else {    // 소셜로 로그인
				Member member = memberService.findByIdx(idx);

				// 로그인 로그 저장
				String db_session_id = session.getId();
				session.setAttribute("db_session_id", db_session_id);
				Member_log log = new Member_log(db_session_id, member.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
				log = logService.save_log(log);
				if (log == null) {
					return "log_error";
				}

				Member_mng mng = mngService.findOne(idx);
				if(mng != null) {
					session.setAttribute("point", mng.getPoint());
					session.setAttribute("grade", mng.getGrade());
				}

				session.setAttribute("session_email", email);
				session.setAttribute("session_type", "personal_s");
				session.setAttribute("nickname", member.getNickname());
				session.setAttribute("idx", idx);

				if (member.getThumbnail() != null && member.getThumbnail().length() > 0) {
					session.setAttribute("session_thumbnail", member.getThumbnail());
				}

				if (session.getAttribute("inviter_idx") != null) {        // 가족 그룹 초대로 가입한 경우
					int inviter_idx = (int) session.getAttribute("inviter_idx");
					if (groupService.same_group_check(inviter_idx, member.getIdx()) == false) {
						groupService.invite_family_group(inviter_idx, member.getIdx());
					}
					session.removeAttribute("inviter_idx");

				}

				// 회원이 그룹이 존재할시 group_idx 세션에 저장
				Member_group group = groupService.findMember_group(member.getIdx());
				if (group != null) {
					session.setAttribute("group_idx", group.getGroup_idx());
				}

				memberService.set_spring_security_auth(email, session);

				return "redirect:/";
			}
		}
	}
	
	
	// 카카오로 로그인하기 버튼 클릭시
	@GetMapping("/login/kakao")
	public String kakao_login(HttpSession session) {
		StringBuffer rest_url = new StringBuffer();
		String state = memberService.getUniqState();
		
		rest_url.append("https://kauth.kakao.com/oauth/authorize?client_id=");
		rest_url.append(KAKAO_APP_KEY);
		rest_url.append("&redirect_uri=");
		rest_url.append(KAKAO_REDIRECT_URL);
		rest_url.append("&response_type=code");
		rest_url.append("&state=");
		rest_url.append(state);
		rest_url.append("&encode_state=1");
		
		session.setAttribute("kakao_state", state);
		return "redirect:" + rest_url.toString();
	}
	
	@GetMapping("/login/kakao/callback")
	public String kakao_login_callback(@RequestParam("code") String code, @RequestParam("state") String state, HttpSession session, Model model) {
		String session_state = (String) session.getAttribute("kakao_state");
		if(session_state.equals(state)) {
			MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>(); 
			request.add("grant_type", "authorization_code");
			request.add("client_id", KAKAO_REST_API_KEY);
			request.add("redirect_uri", KAKAO_REDIRECT_URL);
			request.add("code", code);
			request.add("client_secret", KAKAO_CLIENT_SECRET);
			
			String url = "https://kauth.kakao.com/oauth/token";
			KakaoTokenRequest tokenInfo = null;
			tokenInfo = restTemplate.postForObject(url, request, KakaoTokenRequest.class);     // 토큰 유효성 검사. 유효한 토큰이면 정상적으로 사용자 정보가 user객체로 전달됨
			
			
			String access_token = tokenInfo.getAccess_token();
			String get_profile_url = "https://kapi.kakao.com/v2/user/me";
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + access_token);        // 헤더 설정 필요
			headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			
			HttpEntity<Void> requestEntity = new HttpEntity<>((Void) null, headers);

			ResponseEntity<KaKaoProfileResponse> profileResponse = restTemplate.exchange(get_profile_url, HttpMethod.GET, requestEntity, KaKaoProfileResponse.class);
			KaKaoProfileResponse profile = profileResponse.getBody();        // 응답이 성공적으로 오면 사용자 프로필이 성공적으로 저장됨.
			
			// 계정에 가입된 이메일이면 로그인 처리, 신규 회원이면 가입 뷰로 리턴
			// 이메일 정보가 없는 회원일경우 예외처리.
			
			String email = profile.getKakao_account().getEmail();
			if(profile.getKakao_account().isHas_email()) {
				model.addAttribute("naver_email", profile.getKakao_account().getEmail());
			}
			
			model.addAttribute("naver_nickname", profile.getProperties().getNickname());
			
			String birth = profile.getKakao_account().getBirthday();
			if(birth != null && birth.length() == 4) {
				model.addAttribute("naver_birth_month", birth.substring(0, 2));
				model.addAttribute("naver_birth_day", birth.substring(2));
			}
			
			String gender = profile.getKakao_account().getGender();
			if(gender != null) {
				if(gender.equals("female")) {
					model.addAttribute("naver_gender", "F");
				} else if(gender.equals("male")) {
					model.addAttribute("naver_gender", "M");
				}
			}
			
			Member m = new Member();
			m.setEmail(email);
			m.setThumbnail(profile.getProperties().getThumbnail_image());
			m.setSocial_code('k');
			
			temp_social_member.put(session.getId(), m);
			
			return "sign/social/social_join";
		} else {
			// 인증 에러
			return "state_failed";
		}
		
	}

	@ResponseBody
	@PostMapping("/social_login/auth")
	public String social_login_auth(HttpServletRequest request, @RequestParam("phone") String social_phone, @RequestParam("user_name") String user_name,
			@RequestParam("user_birthdate") String user_birthdate, @RequestParam("user_gender") String user_gender,
			@RequestParam("nickname") String nickname, @RequestParam("token") String token, HttpSession session) {
		String google_user_locale = (String) session.getAttribute("google_user_locale");
		// 구글 최초 가입자중 국내 유저일경우
		/*if (google_user_locale != null && google_user_locale.equals("ko")) {
            if (dupInfo == null || dupInfo.length() == 0) {
                System.out.println("di 입력값 필요");
                return "dup_required";
            }
        }*/
		// 네이버 최초 가입일경우
		/*if (naver_user != null && naver_user.equals("true")) {
            if (dupInfo == null || dupInfo.length() == 0) {
                System.out.println("di 입력값 필요");
                return "dup_required";
            }
        }*/

		// 구글을 통해 가입한경우 세션에 있는 회원정보값 지움
		if(session.getAttribute("new_google_name") != null) {
			session.removeAttribute("new_google_name");
		}
		if (memberService.auth_firebase_token(token, social_phone, session) != 1) {
			return "firebase_auth_failed";
		}

		if (social_phone == null || social_phone.length() < 11 || user_name == null || user_name.length() < 1 || user_birthdate == null
				|| user_birthdate.length() != 8 || user_gender == null || user_gender.length() < 1) {
			return "auth_info_failed";
		}

		if (nickname != null && nickname.length() > 0 && memberService.checkNickname(nickname) == false) {
			return "nickname_dup";
		}

		Member member = temp_social_member.get(session.getId());

		member.setName(user_name);
		member.setPhone(social_phone);
		member.setBirth(user_birthdate);
		member.setNickname(nickname);

		if (user_gender.equals("f")) {
			member.setSex('f');
		} else if (user_gender.equals("m")) {
			member.setSex('m');
		}
		//member.setNickname(nickname);

		member = memberService.join(member);

		temp_social_member.remove(session.getId());

		int idx = member.getIdx();
		if (member != null) {
			Member_mng mng = new Member_mng(idx, 0, '1', memberService.set_now_date(), 0, '0');
			mng.setUnit('c');
			mng = mngService.insert(mng);
			if (mng == null) {
				return "db";
			}
			session.setAttribute("point", mng.getPoint());
			session.setAttribute("grade", mng.getGrade());

			
			Integer inviter_idx = (Integer) session.getAttribute("inviter_idx");
			if (inviter_idx != null) {   
				// 가족 그룹 초대로 가입한 경우
				Integer inviter_group_idx = groupService.findGroupIdx(inviter_idx);
				if(inviter_group_idx != null) {
					if(groupService.findByGroup_idx(inviter_group_idx).size() >= 5) {
						return "group_size_error";
					}
				}
				
				if(inviter_group_idx == null) {
					//inviter_idx가 그룹이 없으면 (그룹 새로 생성후 그룹맺기)
					groupService.invite_family_group_new(inviter_idx, member.getIdx());
				} else {
					// 기존 그룹에 새로운 멤버만 추가
					groupService.invite_family_group(inviter_group_idx, idx);
				}
				session.removeAttribute("inviter_idx");
				session.removeAttribute("tmp_locale");
				session.removeAttribute("tmp_google_email");
			}

			String db_session_id = session.getId();
			session.setAttribute("db_session_id", db_session_id);
			Member_log log = new Member_log(db_session_id, member.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
			log = logService.save_log(log);
			if (log == null) {
				return "log_error";
			}

			if(member.getThumbnail() != null && member.getThumbnail().trim().length() > 0) {
				session.setAttribute("session_thumbnail", member.getThumbnail());
			}

			session.setAttribute("session_email", member.getEmail());
			session.setAttribute("session_type", "personal_s");
			session.setAttribute("nickname", member.getNickname());
			session.setAttribute("idx", idx);

			session.removeAttribute("google_user_locale");  

			memberService.set_spring_security_auth(member.getEmail(), session);
		}
		return "success";
	}

	public String naver_login_callback(HttpServletRequest request) {
		String code = request.getParameter("code");
		String state = request.getParameter("state");

		StringBuffer apiURL = new StringBuffer();
		String redirectURI = null;

		try {
			redirectURI = URLEncoder.encode("http://littleones.kr/Littleone", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		apiURL.append("https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&");
		apiURL.append("client_id=" + NAVER_APP_CLIENT_ID);
		apiURL.append("&client_secret=" + NAVER_APP_SECRET);
		apiURL.append("&redirect_uri=" + redirectURI);
		apiURL.append("&code=" + code);
		apiURL.append("&state=" + state);

		return "";
	}

	@ResponseBody
	@PostMapping("/googletokensignin")
	public String google_login(HttpServletRequest request, @RequestParam("idtoken") String idtokenString, HttpSession session, Model model) {
		Member member = null;
		/*
		HttpTransport transport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
			    .setAudience(Collections.singletonList(clientID))
			    // if multiple clients access the backend:
			    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
			    .build();

		GoogleIdToken idToken = null;
		try {
			idToken = verifier.verify(idtokenString);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (idToken != null) {
		  Payload payload = idToken.getPayload();

		  // Print user identifier
		  String userId = payload.getSubject();
		  System.out.println("User ID: " + userId);

		  // Get profile information from payload
		  String email = payload.getEmail();
		  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		  String name = (String) payload.get("name");
		  String pictureUrl = (String) payload.get("picture");
		  String locale = (String) payload.get("locale");
		  String familyName = (String) payload.get("family_name");
		  String givenName = (String) payload.get("given_name");

		  // 1. 먼저 DB Member 테이블에 해당 소셜계정의 회원이 있는지 체크. 있으면 세션에만 저장후 로그인 처리
		  // 2. DB에 해당 회원이 없으면 새로 insert
		  // Member객체의 setter 메소드를 통해 구글에서 받아온 회원 정보들을 설정. (이름, 이메일). 나머지 필수값은 임시값으로 설정하여 저장
		  memberService.join(member);
		  // 세션에 저장

		} else {
		  System.out.println("Invalid ID token.");
		}
		 */

		String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=";
		url += idtokenString;
		GoogleUser user = null;
		user = restTemplate.getForObject(url, GoogleUser.class);     // 토큰 유효성 검사. 유효한 토큰이면 정상적으로 사용자 정보가 user객체로 전달됨

		if (user != null && user.getAud().equals(GOOGLE_CLIENT_ID)) {    // 토큰 유효성 검사 후에 추가적으로 clientId가 요청한 clientID와 일치하는지 체크
			
			member = memberService.findByEmail(user.getEmail());
			
			/*if(member != null && member.getSocial_code() == 'x') {	// 일반 회원 가입 이력이 있으면
				return "email_dup";
			}
			
			member = memberService.find_social_member(user.getEmail(), 'g'); */
			
			if (member == null) {
				// 멤버테이블에 없는 신규 구글 로그인 유저일 경우 휴대폰 인증 페이지로 이동
				String now_date = memberService.set_now_date();
				member = new Member();
				member.setEmail(user.getEmail());
				member.setPassword(memberService.encode_password("1234"));        // 비밀번호 어떻게 저장할지 고려해볼 것
				member.setName(user.getName());
				member.setJoin_date(now_date);
				member.setPw_renew_date(now_date);
				member.setSocial_code('g');
				member.setMember_type('p');
				member.setCountry_code(user.getLocale());    // 국가코드 어떻게 저장할지 고려해볼것

				if(user.getPicture() != null && user.getPicture().length() > 0) {
					member.setThumbnail(user.getPicture());
				}

				//this.member = member;
				temp_social_member.put(session.getId(), member);

				// 구글 가입인경우, 최초 회원정보 기본값을 채우기위해 세션에 구글회원정보저장. 가입완료시 세션에서 지워줘야함.
				session.setAttribute("new_google_name", member.getName());

				model.addAttribute("social_type", "google");    // 소셜로 최초 로그인시 휴대폰 인증이 필요함을 구분
				if (member.getCountry_code().equals("ko")) {
					session.setAttribute("google_user_locale", "ko");
				}
				
				// firebase locale setting
				session.setAttribute("tmp_locale", memberService.getCookieLocale(request));
				
				// 임시 이메일 셋팅
				session.setAttribute("tmp_google_email", member.getEmail());
				return "social_join";

			} else {
				//이미 가입된 구글 유저이므로, 로그인 세션에 저장
				int idx = memberService.getIdx(member.getEmail());

				// 로그인 로그 저장
				String db_session_id = session.getId();
				session.setAttribute("db_session_id", db_session_id);
				Member_log log = new Member_log(db_session_id, idx, 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
				log = logService.save_log(log);
				if (log == null) {
					return "log_error";
				}

				session.setAttribute("session_email", member.getEmail());
				session.setAttribute("session_type", "personal_s");	// 소셜 로그인 세션
				session.setAttribute("idx", idx);
				session.setAttribute("nickname", member.getNickname());
				
				if (member.getThumbnail() != null && member.getThumbnail().length() > 0) {
					session.setAttribute("session_thumbnail", member.getThumbnail());
				}
				
				Member_mng mng = memberMngService.findOne(idx);
				if(mng != null) {
					session.setAttribute("point", mng.getPoint());
					session.setAttribute("grade", mng.getGrade());
				}

				// 회원이 그룹이 존재할시 group_idx 세션에 저장
				Member_group group = groupService.findMember_group(member.getIdx());
				if (group != null) {
					session.setAttribute("group_idx", group.getGroup_idx());
				}

				memberService.set_spring_security_auth(member.getEmail(), session);

				return "social_login";
			}
		} else {
			return "error";
		}
	}

	@ResponseBody
	@PostMapping("/googletokensignin/invite_family")
	public String google_login_invite(HttpServletRequest request, @RequestParam("idtoken") String idtokenString, @RequestParam("inviter_idx") int inviter_idx,
			HttpSession session, Model model) {
		Member member = null;
		/*
		HttpTransport transport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
			    .setAudience(Collections.singletonList(clientID))
			    // if multiple clients access the backend:
			    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
			    .build();

		GoogleIdToken idToken = null;
		try {
			idToken = verifier.verify(idtokenString);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (idToken != null) {
		  Payload payload = idToken.getPayload();

		  // Print user identifier
		  String userId = payload.getSubject();
		  System.out.println("User ID: " + userId);

		  // Get profile information from payload
		  String email = payload.getEmail();
		  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		  String name = (String) payload.get("name");
		  String pictureUrl = (String) payload.get("picture");
		  String locale = (String) payload.get("locale");
		  String familyName = (String) payload.get("family_name");
		  String givenName = (String) payload.get("given_name");

		  // 1. 먼저 DB Member 테이블에 해당 소셜계정의 회원이 있는지 체크. 있으면 세션에만 저장후 로그인 처리
		  // 2. DB에 해당 회원이 없으면 새로 insert
		  // Member객체의 setter 메소드를 통해 구글에서 받아온 회원 정보들을 설정. (이름, 이메일). 나머지 필수값은 임시값으로 설정하여 저장
		  memberService.join(member);
		  // 세션에 저장

		} else {
		  System.out.println("Invalid ID token.");
		}
		 */

		String url = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idtokenString;
		GoogleUser user = null;
		user = restTemplate.getForObject(url, GoogleUser.class);     // 토큰 유효성 검사. 유효한 토큰이면 정상적으로 사용자 정보가 user객체로 전달됨

		if (user != null && user.getAud().equals(GOOGLE_CLIENT_ID)) {    // 토큰 유효성 검사 후에 추가적으로 clientId가 요청한 clientID와 일치하는지 체크
			member = memberService.findByEmail(user.getEmail());
			/*if(member != null && member.getSocial_code() == 'x') {
				return "email_dup";
			}*/
			
			session.setAttribute("inviter_idx", inviter_idx);
			//member = memberService.find_social_member(user.getEmail(), 'g');	// 일반 회원 가입 이력이 있으면
			if (member == null) {
				// 멤버테이블에 없는 신규 구글 로그인 유저일 경우 휴대폰 인증 페이지로 이동
				String now_date = memberService.set_now_date();
				member = new Member();
				member.setEmail(user.getEmail());
				member.setPassword(memberService.encode_password("1234"));        // 비밀번호 어떻게 저장할지 고려해볼 것
				member.setName(user.getName());
				member.setThumbnail(user.getPicture());
				member.setJoin_date(now_date);
				member.setPw_renew_date(now_date);
				member.setSocial_code('g');
				member.setMember_type('p');
				member.setCountry_code(user.getLocale());    // 국가코드 어떻게 저장할지 고려해볼것

				temp_social_member.put(session.getId(), member);

				model.addAttribute("social_type", "google");    // 소셜로 최초 로그인시 휴대폰 인증이 필요함을 구분
				if (member.getCountry_code().equals("ko")) {
					session.setAttribute("google_user_locale", "ko");
				}
				
				// firebase locale setting
				session.setAttribute("tmp_locale", memberService.getCookieLocale(request));
				
				return "social_join";

			} else { // 이미 소셜가입된 유저
				// member_group에 두 회원의 group_idx가 같은지 체크(이미 그룹 처리 되어있는지)
				if (groupService.same_group_check(inviter_idx, member.getIdx()) == false) {
					groupService.invite_family_group(inviter_idx, member.getIdx());
				}

				//세션에 저장
				int idx = memberService.getIdx(member.getEmail());

				// 로그인 로그 저장
				Member_log log = new Member_log(session.getId(), idx, 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
				log = logService.save_log(log);
				if (log == null) {
					return "log_error";
				}

				session.setAttribute("session_email", member.getEmail());
				session.setAttribute("session_type", "personal_s");	// 소셜 로그인 세션
				session.setAttribute("idx", idx);
				session.setAttribute("nickname", member.getNickname());
				
				if (member.getThumbnail() != null && member.getThumbnail().length() > 0) {
					session.setAttribute("session_thumbnail", member.getThumbnail());
				}
				
				Member_mng mng = memberMngService.findOne(idx);
				if(mng != null) {
					session.setAttribute("point", mng.getPoint());
					session.setAttribute("grade", mng.getGrade());
				}

				// 회원이 그룹이 존재할시 group_idx 세션에 저장
				Member_group group = groupService.findMember_group(member.getIdx());
				if (group != null) {
					session.setAttribute("group_idx", group.getGroup_idx());
				}

				memberService.set_spring_security_auth(member.getEmail(), session);

				return "social_login";
			}
		} else {
			return "error";
		}
	}

	// 비밀번호 변경 ajax
	@ResponseBody
	@PostMapping("/updatePassword")
	public String update_password(@RequestParam("new_password") String new_password, HttpSession session) {
		int idx = (int) session.getAttribute("idx");
		memberService.update_password(idx, new_password, memberService.set_now_date());
		return "1";
	}
}
