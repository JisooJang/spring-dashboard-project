package com.littleone.littleone_web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_log;
import com.littleone.littleone_web.service.AlarmTalkService;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MailSenderService;
import com.littleone.littleone_web.service.MemberLogService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.RedisSessionService;

import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MailSenderService mailSender;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private AlarmTalkService talkService;

	@Autowired
	private MemberLogService logService;
	
	@Autowired
	private RedisSessionService sessionService;


	@GetMapping("/admin/login")
	public String admin_login_get() {
		return "sign/admin_login";
	}

	//@Secured("ROLE_ADMIN")
	@PostMapping("/admin/login")
	public String admin_login_post(@RequestParam("email") String email, @RequestParam("password") String password, HttpServletRequest request, HttpSession session) {
		Member member = memberService.admin_login(email, password);
		if(member != null) {
			session.setAttribute("session_email", email);			// 기업 로그인시 세션 속성 이름 "corp"
			session.setAttribute("session_type", "admin");
			session.setAttribute("idx", memberService.getIdx(email));

			Member_log log = new Member_log(session.getId(), member.getIdx(), 'w', memberService.getClientIp(request), memberService.getClientBrowser(request), memberService.set_now_time(), null);
			log = logService.save_log(log);
			if(log == null) {
				System.out.println("로그 db 저장 에러");
				return "log_error";
			}

			return "redirect:/";
		} else {
			System.out.println("관리자 회원 인증 실패");
			return "redirect:/";
		}
	}

	@GetMapping("/admin/logout")
	public String admin_logout(HttpSession session) {
		String sessionId = session.getId();
		
		session.removeAttribute("session_email");
		session.removeAttribute("session_type");
		session.removeAttribute("idx");
		
		sessionService.delete_session(sessionId);
		session.invalidate();
		
		int result = logService.logout(sessionId, memberService.set_now_time());
		if(result != 1) {
			System.out.println("log db update error : " + result);
			return "db_error";
		}
		
		return "redirect:/";
	}

	//@Secured("ROLE_ADMIN")
	@GetMapping("/admin/createTemplate")
	public String get_createTemplate(HttpSession session) {
		String session_type = (String) session.getAttribute("session_type");
		if(session_type != null && session_type.equals("admin")) {
			return "mypage/admin/add_mail_template";
		} else {
			return "redirect:/";
		}
	}

	@Secured("ROLE_ADMIN")
	@ResponseBody
	@PostMapping("/admin/createTemplate")
	public String creatEmailTemplate(@RequestParam("template_name") String template_name, @RequestParam("template_file_name") String template_file_name,
			@RequestParam("mail_subject") String mail_subject, HttpSession session) throws IOException {
		String session_type = (String) session.getAttribute("session_type");
		if(session_type != null && session_type.equals("admin")) {
			String htmlPart = mailSender.get_html_content("./src/main/resources/templates/mail_template/" + template_file_name + ".html");
			mailSender.createTemplate("[Littleone] " + mail_subject, template_name, null, htmlPart);
			return "success";
		} else {
			return "failed";
		}

	}

	@Secured("ROLE_ADMIN")
	@ResponseBody
	@GetMapping("/admin/updateTemplate")
	public String updateTemplate(HttpSession session) throws IOException {
		String session_type = (String) session.getAttribute("session_type");
		if(session_type != null && session_type.equals("admin")) {
			String htmlPart = mailSender.get_html_content("./src/main/resources/templates/mail_template/" + "welcome_join" + ".html");
			mailSender.updateTemplate("sns_leave", htmlPart, "[Littleone] 회원 가입을 축하드립니다.");
			return "success";
		} else {
			return "failed";
		}
	}

	//@Secured("ROLE_ADMIN")
	@ResponseBody
	@GetMapping("/admin/deleteTemplate/{template_name}")
	public String deleteTemplate(@PathVariable("template_name") String template_name, HttpSession session) throws IOException {
		String session_type = (String) session.getAttribute("session_type");
		if(session_type != null && session_type.equals("admin")) {
			mailSender.deleteTemplate(template_name);
			return "success";
		} else {
			return "failed";
		}
	}

	//@Secured("ROLE_ADMIN")
	@GetMapping("/admin/sendNotice")
	public String sendNotice() {
		return "mypage/admin/send_notice";
	}

	//@Secured("ROLE_ADMIN")
	@ResponseBody
	@PostMapping("/admin/sendNotice")
	public String sendNoticePost(@RequestParam("message_text") String message_text, @RequestParam("message_target") String message_target,
			@RequestParam("client_id") String client_id, @RequestParam("event_type") char event_type) {
		Alert alarm = new Alert();
		if(message_target.equals("individual")) {	// 특정 개인에게 공지 전송
			int idx = memberService.getIdx(client_id);
			alarm.setRecipient(idx);
			alarm.setRequester(0); 		// requester가 0이면, 뷰에서 관리자 썸네일, 관리자 회원정보로 보여줄것
			alarm.setRequest_date(memberService.set_now_time());
			alarm.setEvent_type(event_type);

			alarm = alertService.insert(alarm);	// db insert
		} else {	// 전체 공지 전송
			// group_alert 행 추가(브로드캐스트)
			alarm.setRecipient(0);
			alarm.setRequester(0); 		// requester가 0이면, 뷰에서 관리자 썸네일, 관리자 회원정보로 보여줄것
			alarm.setRequest_date(memberService.set_now_time());
			alarm.setEvent_type(event_type);
			alarm = alertService.insert(alarm);	// db insert
		}
		if(alarm != null) {
			return "success";
		} else {
			System.out.println("DB insert 에러");
			return "failed";
		}
	}
}
