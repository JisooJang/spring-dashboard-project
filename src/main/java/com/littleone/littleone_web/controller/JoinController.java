package com.littleone.littleone_web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

import com.littleone.littleone_web.command.CorporationRegistRequest;
import com.littleone.littleone_web.command.MemberRegistRequest;
import com.littleone.littleone_web.domain.Corporation;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.domain.Thumbnail;
import com.littleone.littleone_web.service.AlarmTalkService;
import com.littleone.littleone_web.service.CorporationService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MailSenderService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.ThumbnailService;

@Controller
public class JoinController {
	// 1. 일반 가입, facebook계정으로 가입, naver계정으로 가입

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMngService mngService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private CorporationService corporationService;

	@Autowired
	private ThumbnailService thumbnailService;

	@Autowired
	private MailSenderService mailSenderService;

	@Autowired
	private AlarmTalkService talkService;
	
	//private Member foreign_member_tmp = null;
	
	/*public void setForeign_member_tmp(Member member) {
		this.foreign_member_tmp = member;
	}

	public Member getForeign_member_tmp() {
		return foreign_member_tmp;
	}*/

	@RequestMapping(value="/join", method=RequestMethod.GET)
	public String join() {
		return "sign/join_index";
	}

	@RequestMapping(value="/join_personal", method=RequestMethod.GET)
	public String join_personal(Model model, HttpServletRequest request) {
		model.addAttribute("locale", memberService.getCookieLocale(request));
		return "sign/person_join";
	}

	// 가족 초대 메일을 통해 가입하는 경우
	@RequestMapping(value="/join_personal/family_invite/{idx}", method=RequestMethod.GET)
	public String join_personal_invite(@PathVariable("idx") int inviter_idx, Model model, HttpServletRequest request) {
		model.addAttribute("inviter_idx", inviter_idx);
		model.addAttribute("locale", memberService.getCookieLocale(request));
		return "sign/person_join";
	}

	/*@RequestMapping(value="/join_enterprise", method=RequestMethod.GET)
	public String join_enterprise() {	// 사업자번호 인증페이지로 리턴
		return "sign/enterprise_join";
	}*/

	@GetMapping("/join_business/privacypolicy")
	public String privacy_policy() {
		return "terms/privacy_policy";
	}

	@GetMapping("/join_business/termsofuse")
	public String terms_of_use() {
		return "terms/terms_of_use";
	}

	@ResponseBody	// 개인, 기업 가입시 이메일 중복 확인 ajax
	@RequestMapping(value="/join/email_check", method=RequestMethod.POST)
	public String email_check(@RequestParam("email") String email) {
		System.out.println("email_check 호출");
		if (memberService.duplicate_check(email) == true) {	// 개인 중복값이 있으면
			//에러메시지 전송
			System.out.println("중복됨");
			return "duplicate";
		} else {
			System.out.println("unique");
			return "unique";
		}

	}

	// 닉네임 중복체크 ajax
	@ResponseBody
	@RequestMapping(value="/join/nickname_check/{nickname}", method=RequestMethod.GET)
	public String nickname_check(@PathVariable("nickname") String nickname) {
		String result = memberService.duplicate_check_nickname(nickname);
		if(result != null && result.length() > 0) {
			return "duplicate";
		} else {
			return "unique";
		}
	}

	@ResponseBody
	@RequestMapping(value="/join_post", method=RequestMethod.POST)
	public String join(@ModelAttribute @Valid MemberRegistRequest memberRegistRequest, BindingResult bindingResult, @RequestParam("token") String token, HttpSession session) throws IOException {
		String now_date = memberService.set_now_date();

		// 1. 커맨드 객체 값 검증. Hibernate-Validator 사용 : 모델에서 어노테이션으로 값 검증
		if(bindingResult.hasErrors()) {
			System.out.println(bindingResult.getErrorCount());
			return "join";	// DB에 추가하지 않고 폼으로 되돌아감
		}
		/*if(memberService.checkEmail(memberRegistRequest.getPersonal_email()) == false) {	// 정규표현식 에러. 나중에 고칠것
			return "email_error";
		}*/
		if(memberService.auth_firebase_token(token, memberRegistRequest.getPhone(), session) != 1) {
			System.out.println("휴대폰 인증 에러");
			return "firebase_auth_error";
		}
		
		if(memberService.checkNickname(memberRegistRequest.getNickname()) == false) {
			return "nickname_error";
		}

		if(memberService.checkPassword(memberRegistRequest.getPassword()) == false) {
			return "password_error";
		}
		Member member = memberRegistRequest.switchToMember();

		if(!memberService.checkinputValue(member.getEmail()) || !memberService.checkinputValue(member.getPassword()) || !memberService.checkinputValue(member.getNickname())) {
			System.out.println("입력값 특수문자 검증 에러");
			return "special";
		}

		member.setPassword(memberService.encode_password(member.getPassword()));	// 비밀번호 암호화하여 저장
		member.setJoin_date(now_date);			// 오늘 날짜로 설정
		member.setPw_renew_date(now_date); 	// 오늘 날짜로 설정
		member.setSocial_code('x');
		member.setMember_type('p');
		member.setCountry_code(memberService.getLocaleCode(member.getPhone()));
		
		member = memberService.join(member);
		if(member == null) {
			System.out.println("Member 데이터 삽입 오류");
			return "db";
		}

		int idx = memberService.getIdx(member.getEmail());
		Member_mng manage = new Member_mng();
		manage.setMember_idx(idx);
		manage.setPoint(0);
		manage.setGrade('1');
		manage.setDormant_chk('0');
		manage.setUnit('c');
		
		if(mngService.insert(manage) == null) {	// DB에 저장
			System.out.println("Member_management 데이터 삽입 오류");
			return "db";
		}

		Thumbnail tn = new Thumbnail();
		tn.setMember_idx(idx);
		tn.setOriginal_filename("default-person.gif");
		tn.setServer_filename("default-person.gif");
		tn.setFile_size(4501);
		tn.setCategory('1');

		if(thumbnailService.insert(tn) == null) {
			System.out.println("Thumbnail 데이터 삽입 오류");
			return "db";
		}
		// 내국인 가입의 경우 가입 축하 알림톡 발송
		if(memberService.getCountryCode(member.getPhone()).equals("+82")) {
			System.out.println("내국인");
			talkService.send_welcome_msg("0" + memberService.getOriginalPhone(member.getPhone()), member.getName());
		} else {	
			// 외국인가입
			/*String temp_token = memberService.getUniqState() + memberService.getUniqState();
			List<String> dest = new ArrayList<String>();
			dest.add(member.getEmail());
			
			Map<String, String> template_data = new HashMap<String, String>();
			template_data.put("nickname", member.getNickname());
			template_data.put("auth_url", "http://littleones.kr/confirm_signup/" + temp_token);
			
			mailSenderService.sendMail(dest, "smart@littleone.kr", "signup_foreigners", template_data);
			
			session.setAttribute("confirm_signup_token", temp_token); 	// 세션에 토큰 임시 저장. 세션 만료후, 비밀번호 변경후 삭제됨	
*/			
			System.out.println("외국인");
			List<String> dest = new ArrayList<String>();
			dest.add(member.getEmail());
			Map<String, String> template_data = new HashMap<String, String>();
			template_data.put("member_name", member.getName());
			mailSenderService.sendMail(dest, "smart@littleone.kr", "signup_foreigners", template_data);
		}
		
		return "success";	
	}
	
	// 외국인 가입 이메일 인증 url 클릭시
	/*@GetMapping("/confirm_signup/{temp_token}")
	public void confirm_signup_foreigners(@PathVariable("temp_token") String temp_token, HttpServletResponse response, HttpSession session) {
		String session_token = (String) session.getAttribute("confirm_signup_token");
		
		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("text/html; charset=UTF-8");
			out = response.getWriter();
			out.println("<script type='text/javascript' charset='utf-8'>");
			if(temp_token.equals(session_token)) {
				Member member = getForeign_member_tmp();
				memberService.join(getForeign_member_tmp());
				Member_mng manage = new Member_mng();
				manage.setMember_idx(member.getIdx());
				manage.setPoint(0);
				manage.setGrade('1');
				manage.setDormant_chk('0');
				memberService.insert(manage);

				Thumbnail tn = new Thumbnail();
				tn.setMember_idx(member.getIdx());
				tn.setOriginal_filename("default-person.gif");
				tn.setServer_filename("default-person.gif");
				tn.setFile_size(4501);
				tn.setCategory('1');
				thumbnailService.insert(tn);
				
				out.println("alert('Your registration has been successfully completed.');");
				session.removeAttribute("confirm_signup_token");
			} else {
				out.println("alert('Invalid authentication url.');");
			}
			out.println("window.location.href='http://littleones.kr';");
			out.println("</script>");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}			
	}*/

	// 가족초대메일을 통해 가입하는 경우
	@ResponseBody
	@PostMapping("/join_post/family_invite/{inviter_idx}")
	public String join(@ModelAttribute @Valid MemberRegistRequest memberRegistRequest, BindingResult bindingResult, 
			@PathVariable("inviter_idx") int inviter_idx) throws IOException {
		String now_date = memberService.set_now_date();

		// 1. 커맨드 객체 값 검증. Hibernate-Validator 사용 : 모델에서 어노테이션으로 값 검증
		if(bindingResult.hasErrors()) {
			System.out.println(bindingResult.getErrorCount());
			return "join";	// DB에 추가하지 않고 폼으로 되돌아감
		}
		/*if(memberService.checkEmail(memberRegistRequest.getPersonal_email()) == false) {	// 정규표현식 에러. 나중에 고칠것
					return "email_error";
				}*/
		if(memberService.checkNickname(memberRegistRequest.getNickname()) == false) {
			return "nickname_error";
		}

		if(memberService.checkPassword(memberRegistRequest.getPassword()) == false) {
			return "password_error";
		}
		
		Integer inviter_group_idx = groupService.findGroupIdx(inviter_idx);
		if(inviter_group_idx != null) {
			if(groupService.findByGroup_idx(inviter_group_idx).size() >= 5) {	// 그룹원 자리가 5명 이상으로 다 찬경우
				return "group_size_error";
			}
		}
		
		
		Member member = memberRegistRequest.switchToMember();
		

		if(!memberService.checkinputValue(member.getEmail()) || !memberService.checkinputValue(member.getPassword()) || !memberService.checkinputValue(member.getNickname())) {
			System.out.println("입력값 특수문자 검증 에러");
			return "special";
		}
		
		member.setPassword(memberService.encode_password(member.getPassword()));	// 비밀번호 암호화하여 저장
		member.setJoin_date(now_date);		// 오늘 날짜로 설정
		member.setPw_renew_date(now_date); 	// 오늘 날짜로 설정
		member.setSocial_code('x');
		member.setMember_type('p');

		member = memberService.join(member);
		if(member == null) {
			System.out.println("Member 데이터 삽입 오류");
			return "join";
		}

		int idx = memberService.getIdx(member.getEmail());
		Member_mng manage = new Member_mng();
		manage.setMember_idx(idx);
		manage.setPoint(0);
		manage.setGrade('1');
		manage.setDormant_chk('0');
		manage.setUnit('c');
		if(mngService.insert(manage) == null) {	// DB에 저장
			System.out.println("Member_management 데이터 삽입 오류");
			return "join";
		}

		Thumbnail tn = new Thumbnail();
		tn.setMember_idx(idx);
		tn.setOriginal_filename("default-person.gif");
		tn.setServer_filename("default-person.gif");
		tn.setFile_size(4501);
		tn.setCategory('1');

		if(thumbnailService.insert(tn) == null) {
			System.out.println("Thumbnail 데이터 삽입 오류");
			return "join";
		}

		// 가족 그룹 생성(DB insert)
		
		
		if(inviter_group_idx == null) {
			//inviter_idx가 그룹이 없으면 (그룹 새로 생성후 그룹맺기)
			groupService.invite_family_group_new(inviter_idx, member.getIdx());
		} else {
			// 기존 그룹에 새로운 멤버만 추가
			groupService.invite_family_group(inviter_group_idx, member.getIdx());
		}
		

		// 내국인 가입의 경우 가입 축하 알림톡 발송
		if(memberService.getCountryCode(member.getPhone()).equals("+82")) {
			talkService.send_welcome_msg("0" + memberService.getOriginalPhone(member.getPhone()), member.getName());
		} else {
			List<String> dest = new ArrayList<String>();
			dest.add(member.getEmail());
			Map<String, String> template_data = new HashMap<String, String>();
			template_data.put("member_name", member.getName());
			mailSenderService.sendMail(dest, "smart@littleone.kr", "signup_foreigners", template_data);
		}

		return "success";
	}

	@RequestMapping(value="/join_business/auth", method=RequestMethod.GET)
	public String auth_corp_num(@RequestParam("corp_num") String corp_num, @RequestParam("corp_name") String corp_name,
			@RequestParam("phone") String phone, @RequestParam("name") String name, Model model) {		// 사업자가입 클릭 후 사업자 번호 인증페이지 처리
		// 사업자번호 인증 api에서 사업자번호 인증시 corp_name을 자동으로 입력해주고, api를 통해 대표자 이름을 얻어와 name과 일치한지 체크
		// 휴대폰인증 api를 통해 실명확인이 되었는지 인증 체크
		// 위 인증이 모두 완료되면 아래 뷰로 리턴
		model.addAttribute("corp_num", corp_num);
		model.addAttribute("corp_name", corp_name);
		model.addAttribute("phone", phone);
		model.addAttribute("name", name);
		return "sign/enterprise_join2";
	}

	/* 사업자 가입 */
	/*@RequestMapping(value="/join_business", method=RequestMethod.POST)
	public String joinbyBusiness_post(@ModelAttribute @Valid CorporationRegistRequest request, BindingResult bindingResult) {
		String mail_html = "";
		String now_date = memberService.set_now_date();

		if(corporationService.validation_check(request) == false) {
			System.out.println("입력값 검증 에러");
			return "error";
		}

		Member member = request.switchToMember();
		Corporation corp = request.switchToCorporation();

		// 나중에 휴대폰 인증 연결시 기업회원의 경우 휴대폰 인증 명의와 사업자번호 인증의 대표자명과 일치해야 함.
		member.setPassword(memberService.encode_password(member.getPassword()));
		member.setJoin_date(now_date);		// 오늘날짜로 설정
		member.setPw_renew_date(now_date);	// 오늘날짜로 설정
		//member.setThumbnail("https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/default-person.gif");
		member.setMember_type('c');
		member.setSocial_code('x');

		if(memberService.join(member) == null) {
			// DB insert
			System.out.println("member db insert error");
			return "error";
		}

		int member_idx = memberService.getIdx(member.getEmail());
		corp.setMember_idx(member_idx);
		corp.setApproval('n');

		if(corporationService.join(corp) == null) {
			// DB insert
			System.out.println("corp db insert error");
			return "error";
		}

		int idx = memberService.getIdx(member.getEmail());
		Member_mng mng = new Member_mng();
		mng.setMember_idx(idx);
		mng.setPoint(0);
		mng.setGrade('1');
		mng.setDormant_chk('0');
		if(mngService.insert(mng) == null) {	// DB insert
			System.out.println("member_mng db insert error");
			return "error";
		}

		Thumbnail tn = new Thumbnail();
		tn.setMember_idx(idx);
		tn.setOriginal_filename("default-person.gif");
		tn.setServer_filename("default-person.gif");
		tn.setFile_size(4501);
		tn.setCategory('1');

		if(thumbnailService.insert(tn) == null) {
			// thumbnail DB에 추가
			System.out.println("Thumbnail 데이터 삽입 오류");
			return "join";
		}
		return "redirect:/";	// 기업 가입 완료 페이지 뷰로 리턴
	}

	// 사업자번호 중복체크
	@ResponseBody
	@RequestMapping(value="/corp_num_check/{corp_num}", method=RequestMethod.GET)
	public String corp_num_duplicate_check(@PathVariable("corp_num") String corp_num) {
		if(corporationService.findByCorp_num(corp_num) != null) {
			return "failed";
		} else {
			return "success";
		}
	}*/

	// 나이스 휴대폰인증 ajax
	/*@ResponseBody
	@RequestMapping(value="/getAuth/niceId", method=RequestMethod.GET)
	public String nice_auth(HttpSession session) {
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
		String sSiteCode = "BH361";			// NICE로부터 부여받은 사이트 코드
		String sSitePassword = "yeVKJRm6399g";		// NICE로부터 부여받은 사이트 패스워드
		String sRequestNumber = niceCheck.getRequestNO(sSiteCode);        	// 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로 
		session.setAttribute("REQ_SEQ" , sRequestNumber);	// 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.

		String sAuthType = "";      	// 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서

		String popgubun 	= "N";		//Y : 취소버튼 있음 / N : 취소버튼 없음
		String customize 	= "";		//없으면 기본 웹페이지 / Mobile : 모바일페이지

		String sGender = ""; 			//없으면 기본 선택 값, 0 : 여자, 1 : 남자 

		// CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
		//리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url : http://www.~
		//String sReturnUrl = "http://localhost:8080/getAuth/niceId/success";      // 성공시 이동될 URL
		//String sErrorUrl = "http://localhost:8080/getAuth/niceId/failed";          // 실패시 이동될 URL
		String sReturnUrl = "http://littleones.kr/getAuth/niceId/success";      // 성공시 이동될 URL
		String sErrorUrl = "http://littleones.kr/getAuth/niceId/failed";          // 실패시 이동될 URL

		// 입력될 plain 데이타를 만든다.
		String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
				"8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
				"9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
				"7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
				"7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
				"11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
				"9:CUSTOMIZE" + customize.getBytes().length + ":" + customize + 
				"6:GENDER" + sGender.getBytes().length + ":" + sGender;

		String sMessage = "";
		String sEncData = null;

		int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
		if( iReturn == 0 )
		{
			sEncData = niceCheck.getCipherData();
		}
		else if( iReturn == -1)
		{
			sMessage = "암호화 시스템 에러입니다.";
		}    
		else if( iReturn == -2)
		{
			sMessage = "암호화 처리오류입니다.";
		}    
		else if( iReturn == -3)
		{
			sMessage = "암호화 데이터 오류입니다.";
		}    
		else if( iReturn == -9)
		{
			sMessage = "입력 데이터 오류입니다.";
		}    
		else
		{
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}

		if(sEncData != null) {
			return sEncData;
		} else {
			System.out.println("sMessage: " + sMessage);
			return sMessage;
		}

	}

	// nice 휴대폰인증 성공시 redirect되는 url
	@RequestMapping(value="/getAuth/niceId/success", method=RequestMethod.POST)
	public String nice_auth_success(HttpServletRequest request, HttpServletResponse response, Model model, HttpSession session) {
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();

		String sEncodeData = memberService.requestReplace(request.getParameter("EncodeData"), "encodeData");

		String sSiteCode = "BH361";				// NICE로부터 부여받은 사이트 코드
		String sSitePassword = "yeVKJRm6399g";			// NICE로부터 부여받은 사이트 패스워드

		String sCipherTime = "";			// 복호화한 시간
		String sRequestNumber = "";			// 요청 번호
		String sResponseNumber = "";		// 인증 고유번호
		String sAuthType = "";				// 인증 수단
		String sName = "";					// 성명
		String sDupInfo = "";				// 중복가입 확인값 (DI_64 byte)
		String sConnInfo = "";				// 연계정보 확인값 (CI_88 byte)
		String sBirthDate = "";				// 생년월일(YYYYMMDD)
		String sGender = "";				// 성별
		String sNationalInfo = "";			// 내/외국인정보 (개발가이드 참조)
		String sMobileNo = "";				// 휴대폰번호
		String sMobileCo = "";				// 통신사
		String sMessage = null;
		String sPlainData = "";

		int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

		if( iReturn == 0 )
		{
			sPlainData = niceCheck.getPlainData();
			sCipherTime = niceCheck.getCipherDateTime();	// 복호화시간

			// 데이타를 추출합니다.
			java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);

			sRequestNumber  = (String)mapresult.get("REQ_SEQ");	// CP 요청번호
			sResponseNumber = (String)mapresult.get("RES_SEQ");	// 처리결과 고유번호
			sAuthType		= (String)mapresult.get("AUTH_TYPE");	// 인증수단 - M : 휴대폰 / C : 신용카드 / X : 공인인증서
			sName			= (String)mapresult.get("NAME");	// 실명
			//sName			= (String)mapresult.get("UTF8_NAME"); //charset utf8 사용시 주석 해제 후 사용
			sBirthDate		= (String)mapresult.get("BIRTHDATE");	// 생년월일 - YYYYMMDD
			sGender			= (String)mapresult.get("GENDER");		// 성별 코드 - 0:여성 / 1:남성
			sNationalInfo  	= (String)mapresult.get("NATIONALINFO");	// 국적 정보 - 0:내국인 / 1:외국인
			sDupInfo		= (String)mapresult.get("DI");		// 개인 회원의 중복가입여부 확인을 위해 사용되는 값(크기: 64byte)
			sConnInfo		= (String)mapresult.get("CI");		// 주민등록번호와 1:1로 매칭되는 고유키(크기: 88byte)
			sMobileNo		= (String)mapresult.get("MOBILE_NO");	// 휴대폰번호
			sMobileCo		= (String)mapresult.get("MOBILE_CO");	// 통신사정보

			String session_sRequestNumber = (String)session.getAttribute("REQ_SEQ");
			if(!sRequestNumber.equals(session_sRequestNumber))
			{
				sMessage = "세션값이 다릅니다. 올바른 경로로 접근하시기 바랍니다.";
				sResponseNumber = "";
				sAuthType = "";
			} else {
				session.removeAttribute("REQ_SEQ");
			}
		}
		else if( iReturn == -1)
		{
			sMessage = "복호화 시스템 에러입니다.";
		}    
		else if( iReturn == -4)
		{
			sMessage = "복호화 처리오류입니다.";
		}    
		else if( iReturn == -5)
		{
			sMessage = "복호화 해쉬 오류입니다.";
		}    
		else if( iReturn == -6)
		{
			sMessage = "복호화 데이터 오류입니다.";
		}    
		else if( iReturn == -9)
		{
			sMessage = "입력 데이터 오류입니다.";
		}    
		else if( iReturn == -12)
		{
			sMessage = "사이트 패스워드 오류입니다.";
		}    
		else
		{
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}

		if(sMessage != null) {
			System.out.println(sMessage);
		}

		PrintWriter out = null;
		try {
			response.setCharacterEncoding("UTF-8"); 
			response.setContentType("text/html; charset=UTF-8");
			out = response.getWriter();
			Integer session_idx = (Integer) session.getAttribute("idx");
			if(session_idx != null) {
				// 개인정보수정시 휴대폰 인증
				Member m = memberService.findByIdx(session_idx.intValue());
				out.println("<head>\n" +
						"<script\n" +
						"  src=\"https://code.jquery.com/jquery-3.3.1.min.js\"\n" +
						"  integrity=\"sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=\"\n" +
						"  crossorigin=\"anonymous\"></script></head>");
				out.println("<script type='text/javascript' charset='utf-8'>");

				if(!m.getName().equals(sName) || !m.getBirth().equals(sBirthDate) || m.getSex() != sGender.charAt(0)) {
					// 조건문을 기존 DB값의 DI값과 같은지로 변경해야할지 고려할것.
					out.println("alert('기존에 가입된 명의와 동일한 명의의 번호로만 인증 가능합니다.');");
				} else {
					out.println("var userName = '" + sName + "';");
					out.println("var userBirthDate = '" + sBirthDate + "';");
					out.println("var userMobileNo = '" + sMobileNo + "';");
					out.println("var userGender = '" + sGender + "';");
					out.println("var dupInfo = '" + sDupInfo + "';");
					out.println("window.opener.document.querySelector('input[name=phone]').value = userMobileNo;");
					out.println("window.opener.document.querySelector('input[name=phone2]').value = userMobileNo;");
					out.println("window.opener.document.querySelector('input[name=phone_autorization]').value = 'true';");
					out.println("window.opener.$('#auth_button').text('인증됨');");
					out.println("window.opener.$('#auth_button').addClass('authorized');");
				}
				out.println("self.close();");
				out.println("</script>");
			} else {
				// 일반 회원가입 휴대폰 인증
				out.println("<head>\n" +
						"<script\n" +
						"  src=\"https://code.jquery.com/jquery-3.3.1.min.js\"\n" +
						"  integrity=\"sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=\"\n" +
						"  crossorigin=\"anonymous\"></script></head>");
				out.println("<script type='text/javascript' charset='utf-8'>");

				// DB에 이미 가입된 정보가 있으면
				if() {
					out.println("alert('기존에 가입된 명의와 동일한 명의의 번호로만 인증 가능합니다.');");
					out.println("self.close();");
					out.println("</script>");
				} else {
					...
				}

				out.println("var userName = '" + sName + "';");
				out.println("var userBirthDate = '" + sBirthDate + "';");
				out.println("var userMobileNo = '" + sMobileNo + "';");
				out.println("var userGender = '" + sGender + "';");
				out.println("var dupInfo = '" + sDupInfo + "';");
				out.println("window.opener.document.querySelector('input[name=user_name]').value = userName;");
				out.println("window.opener.document.querySelector('input[name=user_birthdate]').value = userBirthDate;");
				out.println("window.opener.document.querySelector('input[name=phone]').value = userMobileNo;");
				out.println("window.opener.document.querySelector('input[name=\"auth_phone\"]').value = 'true';");
				out.println("window.opener.document.querySelector('input[name=di]').value = dupInfo;");
				out.println("window.opener.document.getElementById('user_name').value = userName;");
				out.println("window.opener.document.getElementById('phone').value = userMobileNo;");
				out.println("window.opener.document.getElementById('user_birthdate').value = userBirthDate;");
				out.println("window.opener.document.querySelector('input[name=user_gender]').value = userGender;");
				out.println("window.opener.$('#auth_button').text('인증됨');");
				out.println("window.opener.$('#auth_button').addClass('authorized');");
				out.println("window.opener.$('.validation_check.api_info').css('color','#ea8255');");
				out.println("window.opener.$('#auth_div #phone').css('font-size','16px');");
				out.println("window.opener.$('.api_info').fadeIn(500);");
				out.println("self.close();");
				out.println("</script>");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}

		return null;
	}

	// nice 휴대폰인증 실패시 redirect되는 url
	@RequestMapping(value="/getAuth/niceId/failed", method=RequestMethod.POST)
	public String nice_auth_failed(HttpServletRequest request, HttpServletResponse response, Model model, HttpSession session) {
		NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();

		String sEncodeData = memberService.requestReplace(request.getParameter("EncodeData"), "encodeData");

		String sSiteCode = "";				// NICE로부터 부여받은 사이트 코드
		String sSitePassword = "";			// NICE로부터 부여받은 사이트 패스워드
		String sCipherTime = "";			// 복호화한 시간
		String sRequestNumber = "";			// 요청 번호
		String sErrorCode = "";				// 인증 결과코드
		String sAuthType = "";				// 인증 수단
		String sMessage = "";
		String sPlainData = "";

		int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

		if( iReturn == 0 )
		{
			sPlainData = niceCheck.getPlainData();
			sCipherTime = niceCheck.getCipherDateTime();

			// 데이타를 추출합니다.
			java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);

			sRequestNumber 	= (String)mapresult.get("REQ_SEQ");
			sErrorCode 		= (String)mapresult.get("ERR_CODE");
			sAuthType 		= (String)mapresult.get("AUTH_TYPE");
		}
		else if( iReturn == -1)
		{
			sMessage = "복호화 시스템 에러입니다.";
		}    
		else if( iReturn == -4)
		{
			sMessage = "복호화 처리오류입니다.";
		}    
		else if( iReturn == -5)
		{
			sMessage = "복호화 해쉬 오류입니다.";
		}    
		else if( iReturn == -6)
		{
			sMessage = "복호화 데이터 오류입니다.";
		}    
		else if( iReturn == -9)
		{
			sMessage = "입력 데이터 오류입니다.";
		}    
		else if( iReturn == -12)
		{
			sMessage = "사이트 패스워드 오류입니다.";
		}    
		else
		{
			sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
		}

		PrintWriter out = null;
		try {
			out = response.getWriter();
			response.setContentType("text/html; charset=UTF-8");
			out.println("<script>");	// 부모창에 휴대폰번호 데이터 전달 type='text/javascript'
			out.println("alert('본인 인증 오류입니다. 오류내용 : '" + sMessage + ")"); 	// sMobileNo로 수정할것
			out.println("self.close();");
			out.println("</script>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(out != null) {
				out.flush();
				out.close();
			}
		}

		return null;
	}
*/
}


