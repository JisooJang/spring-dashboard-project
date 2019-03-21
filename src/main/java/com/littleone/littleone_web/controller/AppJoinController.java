package com.littleone.littleone_web.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.littleone.littleone_web.command.MemberRegistRequest;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.domain.Thumbnail;
import com.littleone.littleone_web.service.AlarmTalkService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.ThumbnailService;

@Controller
public class AppJoinController {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private ThumbnailService thumbnailService;
	
	@Autowired
	private AlarmTalkService talkService;
	
	@GetMapping("/app/join_personal")
	public String join_personal() {
		return "sign/person_join";
	}

	@ResponseBody	// 개인, 기업 가입시 이메일 중복 확인 ajax
	@PostMapping("/app/join/email_check")
	public String email_check(@RequestParam("email") String email) {
		System.out.println("email_check 호출");
		if (memberService.duplicate_check(email) == true) {	// 개인 중복값이 있으면
			return "duplicate";
		} else {
			return "unique";
		}

	}

	// 닉네임 중복체크 ajax
	@ResponseBody
	@RequestMapping(value="/app/join/nickname_check/{nickname}", method=RequestMethod.GET)
	public String nickname_check(@PathVariable("nickname") String nickname) {
		String result = memberService.duplicate_check_nickname(nickname);
		if(result != null && result.length() > 0) {
			return "duplicate";
		} else {
			return "unique";
		}
	}
}
