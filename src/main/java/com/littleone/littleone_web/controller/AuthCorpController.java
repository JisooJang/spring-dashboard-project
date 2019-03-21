package com.littleone.littleone_web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.littleone.littleone_web.domain.CorpAuthInfo;
import com.littleone.littleone_web.domain.Corp_approval;
import com.littleone.littleone_web.domain.Corporation;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.service.AmazonS3Service;
import com.littleone.littleone_web.service.CorpApprovalService;
import com.littleone.littleone_web.service.CorporationService;
import com.littleone.littleone_web.service.MemberService;

@Controller
public class AuthCorpController {

	/*@Autowired
	private MemberService memberService;
	
	@Autowired
	private CorporationService corpService;
	
	@Autowired
	private CorpApprovalService corpApprovalService;
	
	@Autowired
	private AmazonS3Service amazonS3Service;
	
	@RequestMapping(value="/authentication/business_license", method=RequestMethod.GET)
	public String auth_corp(HttpSession session, Model model) {
		if(session.getAttribute("session_type") != null && session.getAttribute("session_type").equals("corp")) {
			int idx = (int) session.getAttribute("idx");
			Member m = memberService.findByIdx(idx);
			Corporation c = corpService.findByIdx(idx);
			String corp_num = c.getCorp_num();
			String corp_num2 = corp_num.substring(0, 3) + '-' + corp_num.substring(3,6) + '-' + corp_num.substring(6);
			model.addAttribute("corp_name", c.getCorp_name());
			model.addAttribute("corp_num", corp_num2);
			model.addAttribute("name", m.getName());
			model.addAttribute("date", memberService.set_now_date());
			
			Corp_approval co = corpApprovalService.findByIdx(idx);
			if(co != null) {
				model.addAttribute("license_url", co.getLicense_url());
			}
			return "/mypage/crn/crn";
		} else {
			System.out.println("잘못된 접근입니다.");
			return "redirect:/";
		}
		
	}

	@ResponseBody
	@RequestMapping(value="/authentication/business_license", method=RequestMethod.POST)
	public String auth_corp_post(@RequestParam("license_file") MultipartFile file, HttpSession session) throws IOException {
		if(session.getAttribute("session_type") != null && session.getAttribute("session_type").equals("corp")) {
			// 1. 파일 정보를 가져옴
			String original_file_name = file.getOriginalFilename();	// 사용자가 첨부한 원본 파일 이름
			int index = original_file_name.indexOf('.');
			int file_size = (int) file.getSize();	// type casting 더 알아볼것. long으로 할지?
			String extension = original_file_name.substring(index+1);
			
			String corp_num = corpService.getCorpNum((int) session.getAttribute("idx"));
			String new_name = corp_num + "." + extension;	// 첨부된 확장자를 뒤에 붙임

			if(original_file_name != null && original_file_name.length() > 0) {	// 썸네일이 첨부되었으면
				amazonS3Service.putThumbnail("business_auth", new_name, file.getInputStream(), amazonS3Service.getMetadata(file));
				String img_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/business_auth/" + new_name;
				// corp_approval_list insert
				Corp_approval c = new Corp_approval((int)session.getAttribute("idx"), img_url);
				if(corpApprovalService.save(c) != null) {
					return "success";
				} else {
					System.out.println("DB insert 오류");
					return "failed";
				}
				
			} else {
				System.out.println("잘못된 파일 첨부");
				return "failed";
			}
		} else {
			System.out.println("잘못된 접근입니다.");
			return "redirect:/";
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value="/authentication/canceled", method=RequestMethod.GET)
	public String cancel_auth(HttpSession session) {
		if(session.getAttribute("idx") != null) {
			corpApprovalService.delete((int)session.getAttribute("idx"));
			return "success";
		} else {
			System.out.println("세션이 존재하지 않음");
			return "failed";
		}
	}
	
	@RequestMapping(value="/authentication/business_license/list", method=RequestMethod.GET)
	public String list_approved(Model model) {
		// 관리자로 로그인시 사용 가능 메소드
		List<CorpAuthInfo> info = corpApprovalService.findAllInfo();
		model.addAttribute("approval_list", info);
		return "mypage/admin/license_list";
	}
	
	@ResponseBody
	@RequestMapping(value="/authentication_approved/{idx}", method=RequestMethod.GET)
	public String set_approved(@PathVariable("idx") String member_idx) {
		// 관리자로 로그인시 사용 가능 메소드
		int result = corpService.set_approval_y(Integer.parseInt(member_idx));
		if(result == 1) { 
			// corp_approval 행 삭제
			corpApprovalService.delete(Integer.parseInt(member_idx));
			return "success"; 
		}
		else { 
			// DB rollback
			return "failed"; 
		}
	}*/
}
