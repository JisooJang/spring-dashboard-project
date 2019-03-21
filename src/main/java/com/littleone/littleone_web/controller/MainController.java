package com.littleone.littleone_web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.littleone.littleone_web.api_response.IpinfodbResponse;
import com.littleone.littleone_web.domain.Alarm;
import com.littleone.littleone_web.domain.Notify_email;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MailSenderService;
import com.littleone.littleone_web.service.MemberService;

@PropertySource("classpath:api_key.properties")
@Controller
public class MainController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private MailSenderService mailService;

	private RestTemplate restTemplate;

	@Value("${ipinfodb_key}")
	private String ipinfodb_key;

	@ResponseBody
	@GetMapping("/test_regex/{expression}")
	public String test_regex(@PathVariable("expression") String expression) {
		String regex  = "^[a-zA-Z가-힣0-9]*$";	// 일반문자
		if(expression.matches(regex)) {
			return "success";
		} else {
			return "failed";
		}
	}

	@RequestMapping("/")
	public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam(value="lang", required=false) String lang) {
		boolean check = false;
		
		if(lang != null && lang.length() == 2) {
			if(lang.equals("ko") || lang.equals("ja") || lang.equals("en") || lang.equals("zh")) {
				check = true;
				session.setAttribute("lang_value", lang);
			} else {
			}

		} else {
			Cookie[] cookies = request.getCookies();
			if(cookies != null && cookies.length > 0) {
				for(Cookie cookie : cookies) {
					if(cookie.getName().equals("lang")) {
						check = true;
						session.setAttribute("lang_value", cookie.getValue());
					}
				}
			}
		}

		if(check == false) {
			// 사용자 ip로 국가정보를 받아와서 언어 쿠키 셋팅
			String lang_val = null;
			restTemplate = new RestTemplate();
			String user_ip = memberService.getClientIp(request);
			IpinfodbResponse api_response = restTemplate.getForObject("http://api.ipinfodb.com/v3/ip-country/?key=" + ipinfodb_key + "&ip=" + user_ip + "&format=json", IpinfodbResponse.class);
			Cookie cookie = null;
			String country_name = api_response.getCountryName();
			if(country_name.equals("-") || country_name.equals("ERROR")) {
				cookie = new Cookie("lang", "ko");
				lang_val = "ko";
			} else {
				if(country_name.equals("Korea, Republic of")) {
					System.out.println("KR;Korea");
					cookie = new Cookie("lang", "ko");
					lang_val = "ko";
				} else if(country_name.equals("United States")) {
					System.out.println("US;United States");
					cookie = new Cookie("lang", "en");
					lang_val = "en";
				} else if(country_name.equals("China")) {
					System.out.println("CN;China");
					cookie = new Cookie("lang", "zh");
					lang_val = "zh";
				} else if(country_name.equals("Japan")) {
					System.out.println("JP;Japan");
					cookie = new Cookie("lang", "ja");
					lang_val = "ja";
				} else {
					System.out.println("else..");
					cookie = new Cookie("lang", "ko");
					lang_val = "ko";
				}
			}
			response.addCookie(cookie);
			session.setAttribute("lang_value", lang_val);
			
		}
		model.addAttribute("index_check", true);
		
		return "index";
	}

	@ResponseBody
	@RequestMapping("/subscribe_email")
	public String subscribe_mail(@RequestParam("email") String email) {
		// 입력받은 이메일을 영구적인 데이터 공간에 저장.
		// 결과 메시지 또는 뷰 응답
		if(email.trim().length() == 0 || email.indexOf("@") == -1) {
			return "input_failed";
		}
		Notify_email m = mailService.save_notify_email(email);
		if(m != null) {
			return "success";
		} else {
			return "failed";
		}
	}

	@ResponseBody
	@RequestMapping("/inquireByEmail")
	public String inquireByEmail(@RequestParam("name") String writer_name, @RequestParam("email") String writer_email,
			@RequestParam("subject") String subject, @RequestParam("contents") String contents) {
		Collection<String> des = new ArrayList<String>();
		des.add("smart@littleone.kr");

		Map<String, String> template_data = new HashMap<String, String>();
		template_data.put("writer_email", writer_email);
		template_data.put("writer_name", writer_name);
		template_data.put("subject", subject);
		template_data.put("contents", contents);

		mailService.sendMail(des, "j.jang@littleone.kr", "inquiryMailTemplate", template_data);

		return "success";
	}

	@RequestMapping("/app")
	public String downloadByQRCode(Model model) {
		return "";
	}

	@RequestMapping("/product")
	public String productHome(){
		return "index";
	}

	@ResponseBody	// 모바일용 햄버거 메뉴 ajax 통신
	@RequestMapping("/mobile_header")
	public String mobile_header(HttpSession session) {
		// 필요한값 : 로그인된 이메일(세션 사용), 개인별 알림 정보, 프로필 이미지
		// 나중에 필요한 값들을 전부 객체에 넣어서 객체 자체를 리턴
		if(session.getAttribute("idx") == null) {
			return "";
		}
		int member_idx = (int) session.getAttribute("idx");
		String thumbnail_url = memberService.findByIdx(member_idx).getThumbnail();
		if(thumbnail_url != null && thumbnail_url.length() > 0) {
			return thumbnail_url;	// 일단 썸네일 이미지만 return 해줌
		} else { 
			return "";
		}
	}

	// 알람더보기 기능
	@GetMapping("/alarm_more")
	public String alarm_more(Model model, HttpSession session)  {
		int idx = (int) session.getAttribute("idx");
		List<Alarm> alarm_list = groupService.getListByRecipient(idx);
		model.addAttribute("alarm_list", alarm_list);
		return "alarm/index";
	}

	@ResponseBody
	@GetMapping("/alarm/readAll")
	public String alarm_readAll(HttpSession session) {
		int recipient = (int) session.getAttribute("idx");
		alertService.deleteByRecipient(recipient);
		//alertService.readAll(recipient);
		return "success";
	}

	@ResponseBody
	@GetMapping("/regex/{string}")
	public String regex_test(@PathVariable("string") String temp) {
		if(memberService.checkPassword(temp)) {
			return "success";
		} else {
			return "failed";
		}
	}

	// 프로모션사이트 알림 메일 구독
	@ResponseBody
	@PostMapping("/notify_email")
	public String notify_email(@RequestParam("email") String email, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "http://littleone.kr");	//Cross-Origin Read Blocking 외부 특정 도메인 허용 
		if(email.trim().length() == 0 || email.indexOf("@") == -1) {
			return "input_failed";
		}

		Notify_email m = mailService.save_notify_email(email);
		if(m != null) {
			return "success";
		} else {
			return "failed";
		}
	}

	@ResponseBody
	@GetMapping("/test/header")
	public String test(@RequestHeader(value="member_idx") String member_idx, @RequestHeader(value="session_id") String session_id, HttpServletRequest request) {
		String member_idx2 = request.getHeader("member_idx");
		String session_id2 = request.getHeader("session_id");

		//String member_idx = request.getParameter("member_idx");
		//String session_id = request.getParameter("session_id");
		return "success";
	}

	@GetMapping("/about")
	public String intro_littleone() {
		return "about/about";
	}

	@GetMapping("/download/app")
	public String qrcode() {
		return "qrcodepage/index";
	}
}
