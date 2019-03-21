package com.littleone.littleone_web.service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.amazonaws.util.StringUtils;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.domain.SearchMember;
import com.littleone.littleone_web.domain.TmpFileInfo;
import com.littleone.littleone_web.repository.MemberRepository;
import com.littleone.littleone_web.repository.Member_managementRepository;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AmazonS3Service S3Service;
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public HashMap<Integer, Member> login(String email, String password) {
		// TODO Auto-generated method stub
		// 1. email로 특정 멤버를 db에서 불러온다.
		// 2. passwordEncoder를 이용해 매개변수 password와 DB의 encoded 패스워드를 matches() 메소드를 통해 비교한다.
		HashMap<Integer, Member> map = new HashMap<Integer, Member>();

		boolean result = false;
		Member member = memberRepository.findByEmail(email);
		if(member != null) {
			result = passwordEncoder.matches(password, member.getPassword());
			if(result) { 
				map.put(1, member);
			} else {
				map.put(0, member);	// 비밀번호 실패
			}
		} else {
			return null;	// 이메일 실패
		}
		return map;
	}

	@Override
	public Member auth(String email, String password) {
		// TODO Auto-generated method stub
		// 1. email로 특정 멤버를 db에서 불러온다.
		// 2. passwordEncoder를 이용해 매개변수 password와 DB의 encoded 패스워드를 matches() 메소드를 통해 비교한다.
		boolean result = false;
		Member member = memberRepository.findByEmail(email);
		if(member != null) {
			result = passwordEncoder.matches(password, member.getPassword());
			if(result) { 
				return member;
			} else {
				return null;	// 비밀번호 실패
			}
		} else {
			return null;	// 이메일 실패
		}
	}

	@Override
	public Member join(Member member) {
		// TODO Auto-generated method stub
		return memberRepository.save(member);
	}

	@Override
	public boolean duplicate_check(String email) {
		// TODO Auto-generated method stub
		String member = memberRepository.findEmail(email);
		if(member == null) { return false; }	// 존재하지 않음
		else { return true; }	// 이미 존재
	}

	@Override
	public String encode_password(String password) {
		// TODO Auto-generated method stub
		return passwordEncoder.encode(password);
	}

	@Override
	public boolean decode_password(CharSequence rawPassword, String encodedPassword) {
		// TODO Auto-generated method stub
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	public String get_encoded_password(CharSequence rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public String set_now_date() {
		// TODO Auto-generated method stub
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		return date.format(new Date());
	}

	@Override
	public Member find_social_member(String email, char social_code) {
		// TODO Auto-generated method stub
		return memberRepository.find_social_member(email, social_code);
	}

	@Override
	public String findEmail(String email) {
		// TODO Auto-generated method stub
		return memberRepository.findEmail(email);
	}

	@Override
	public int update_password(int idx, String new_password, String now_date) {
		// TODO Auto-generated method stub
		String encoded_password = passwordEncoder.encode(new_password);
		return memberRepository.updatePassword(idx, encoded_password, now_date);

	}

	@Override
	public List<String> find_email(String phone) {
		// TODO Auto-generated method stub
		return memberRepository.find_email(phone);
	}

	@Override
	public Member auth_member(String email, String password) {
		// TODO Auto-generated method stub
		return memberRepository.findByEmailAndPassword(email, password);
	}

	@Override
	public int getIdx(String email) {
		// TODO Auto-generated method stub
		String idx = memberRepository.findIdxByEmail(email);
		if(idx == null) {
			return 0;
		} else {
			return Integer.parseInt(idx);
		}
	}

	@Override
	public Member findByIdx(int idx) {
		// TODO Auto-generated method stub
		return memberRepository.findOne(idx);
	}

	@Override
	public void delete(int idx) {
		// TODO Auto-generated method stub
		memberRepository.delete(idx);
	}

	@Override
	public String getUniqId() {
		// 랜덤 토큰값 생성후 반환
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}

	@Override
	public String getUniqFileName(String original_filename) {	// 확장자까지 저장됨
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		Calendar c = Calendar.getInstance();
		int tmp = original_filename.indexOf('.');
		String right = original_filename.substring(tmp + 1, original_filename.length());
		String file_name = sdf.format(c.getTime()) + "_" + getUniqId().substring(0, 10) + "." + right.toLowerCase();	// 이미지 서버에 저장될 파일 이름
		return file_name;
	}

	@Override
	public boolean find_not_social_member(String email) {
		// TODO Auto-generated method stub
		if(memberRepository.findBySocial_codeX(email) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean checkFileName(String original_filename) {
		// TODO Auto-generated method stub
		// 파일명 조건 검사
		if(original_filename.contains("/")) {
			System.out.println("파일명에는 / 사용이 불가합니다.");
			return false;
		}
		if(original_filename.indexOf('.') != original_filename.lastIndexOf('.')) {
			System.out.println("파일명에는 . 사용이 불가합니다.");
			return false;
		}
		String extension_l = original_filename.substring(original_filename.indexOf('.') + 1).toLowerCase();
		if(extension_l.equals("jpg") || extension_l.equals("jpeg") || extension_l.equals("png") || extension_l.equals("gif")) {
			System.out.println("확장자 체크 완료");
		} else {
			System.out.println(extension_l + " 확장자는 업로드 불가합니다.");
			return false;
		}

		return true;
	}

	@Override
	public Member admin_login(String email, String password) {
		// TODO Auto-generated method stub
		Member m =  memberRepository.admin_login(email);
		if(m != null && passwordEncoder.matches(password, m.getPassword())) {
			return m;
		} else {
			return null;
		}
	}

	@Override
	public List<SearchMember> searchMember(String email_pattern, String session_email) {
		// TODO Auto-generated method stub
		//return memberRepository.searchMember(email_pattern);
		String subquery = "select substring_index(member.email,'@', 1) from member";	// '@'기준으로 이메일의 아이디만 선택
		String sqlString = "select new com.littleone.littleone_web.domain.SearchMember(m.email, m.name, m.thumbnail, m.idx)"
				+ " FROM Member m WHERE m.member_type = 'p' AND email like :email_pattern AND email != :session_email";
		TypedQuery<SearchMember> query = em.createQuery(sqlString, SearchMember.class);
		query.setParameter("email_pattern", "%" + email_pattern + "%");
		query.setParameter("session_email", session_email);
		return query.getResultList();
	}

	@Override
	public boolean phone_auth_check() {	// 휴대폰 기입후 인증완료되었는지 체크
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<SearchMember> searchMemberByPhone(String phone_number, int idx) {
		// TODO Auto-generated method stub
		String subquery = "select substring_index(member.email,'@', 1) from member";	// '@'기준으로 이메일의 아이디만 선택
		String sqlString = "select new com.littleone.littleone_web.domain.SearchMember(m.phone, m.name, m.thumbnail, m.idx)"
				+ " FROM Member m WHERE m.member_type = 'p' AND phone = :phone_number AND idx != :idx";
		TypedQuery<SearchMember> query = em.createQuery(sqlString, SearchMember.class);
		query.setParameter("phone_number", phone_number);
		query.setParameter("idx", idx);
		return query.getResultList();
	}

	@Override
	public List<SearchMember> searchMemberByNickname(String nickname, int idx) {
		// TODO Auto-generated method stub
		String sqlString = "select new com.littleone.littleone_web.domain.SearchMember(m.phone, m.nickname, m.thumbnail, m.idx)"
				+ " FROM Member m WHERE m.member_type = 'p' AND nickname = :nickname AND idx != :idx";
		TypedQuery<SearchMember> query = em.createQuery(sqlString, SearchMember.class);
		query.setParameter("nickname", nickname);
		query.setParameter("idx", idx);
		return query.getResultList();
	}

	@Override
	public int updatePhone(int idx, String phone) {
		// TODO Auto-generated method stub
		return memberRepository.updatePhone(idx, phone);
	}

	@Override
	public String set_now_time() {
		// TODO Auto-generated method stub
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return date.format(new Date());
	}

	@Override
	public String getUniqState() {
		// CSRF 방지를 위한 상태 토큰 생성 코드
		// 상태 토큰은 추후 검증을 위해 세션에 저장되어야 한다.
		// 랜덤 토큰값 생성후 반환
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString();
	}

	@Override
	public String duplicate_check_nickname(String nickname) {
		// TODO Auto-generated method stub
		return memberRepository.duplicate_check_nickname(nickname);
	}

	@Override
	public int findIdxByNickname(String nickname) {
		// TODO Auto-generated method stub
		return memberRepository.findIdxByNickname(nickname);
	}

	@Override
	public long checkPw_renew_date(String pw_renew_date) {
		// TODO Auto-generated method stub
		SimpleDateFormat  formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today=  formatter.format(new Date());
		Date today_date = null, pw_date = null;
		try {
			today_date = formatter.parse(today);
			pw_date = formatter.parse(pw_renew_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long diff = (today_date.getTime() - pw_date.getTime()) / (24 * 60 * 60 * 1000);
		return diff;
	}

	@Override
	public String requestReplace (String paramValue, String gubun) {
		String result = "";
		if (paramValue != null) {	
			paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			paramValue = paramValue.replaceAll("\\*", "");
			paramValue = paramValue.replaceAll("\\?", "");
			paramValue = paramValue.replaceAll("\\[", "");
			paramValue = paramValue.replaceAll("\\{", "");
			paramValue = paramValue.replaceAll("\\(", "");
			paramValue = paramValue.replaceAll("\\)", "");
			paramValue = paramValue.replaceAll("\\^", "");
			paramValue = paramValue.replaceAll("\\$", "");
			paramValue = paramValue.replaceAll("'", "");
			paramValue = paramValue.replaceAll("@", "");
			paramValue = paramValue.replaceAll("%", "");
			paramValue = paramValue.replaceAll(";", "");
			paramValue = paramValue.replaceAll(":", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll("#", "");
			paramValue = paramValue.replaceAll("--", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll(",", "");

			if(gubun != "encodeData"){
				paramValue = paramValue.replaceAll("\\+", "");
				paramValue = paramValue.replaceAll("/", "");
				paramValue = paramValue.replaceAll("=", "");
			}

			result = paramValue;

		}
		return result;
	}

	@Override
	public boolean checkinputValue(String value) {
		// TODO Auto-generated method stub
		if(value.contains("'") || value.contains("\"") || value.contains("-") || value.contains("`")) {
			return false;
		} else { 
			return true;
		}
	}

	@Override
	public String checkDI(String di) {
		// TODO Auto-generated method stub
		return memberRepository.checkDI(di);
	}

	@Override
	public Member findByEmail(String email) {
		// TODO Auto-generated method stub
		return memberRepository.findByEmail(email);
	}

	@Override
	public boolean checkNickname(String nickname) {
		// TODO Auto-generated method stub
		//중복값 체크, 닉네임 한글 1~8자, 영문 4~12자 띄어쓰기 불가, _를 제외한 특수문자 불가, 비속어 필터링
		String en = "^[a-zA-Z0-9]*$";
		String ko = "^[가-힣0-9]*$";
		if(duplicate_check_nickname(nickname) != null) {
			return false;	// 중복값
		}
		
		if(nickname.contains(" ")) {
			return false;	// 띄어쓰기 불가
		}

		// 한글일 경우
		if(nickname.matches(ko)) {
			System.out.println("한글");
			if(nickname.length() < 1 || nickname.length() > 8) {
				return false;
			}
		} else if(nickname.matches(en)) { // 영문일 경우
			System.out.println("영문");
			if(nickname.length() < 1 || nickname.length() > 16) {
				return false;
			}
		} else {
			System.out.println("한글x영문x");
		}

		String regex  = "^[a-zA-Z가-힣0-9]*$"; 	// 특수문자 필터링
		if(nickname.matches(regex)) {
			return true; 
		} else {
			return false; // 특수문자 포함
		}
	}

	@Override
	public int checkNickname_mypage(String nickname, int idx) {
		// TODO Auto-generated method stub
		//중복값 체크, 닉네임 한글 1~8자, 영문 4~12자 띄어쓰기 불가, _를 제외한 특수문자 불가, 비속어 필터링
		if(memberRepository.duplicate_check_nicname_mypage(nickname, idx) != null) {
			return 1;	// 중복값 존재
		}
		
		if(nickname.contains(" ")) {
			return 3; 	// 공백 불가
		}

		String regex  = "[^\\uAC00-\\uD7A3xfe0-9a-zA-Z\\\\s]\n"; 	// 특수문자 필터링
		String en = "^[a-zA-Z0-9]*$";
		String ko = "^[가-힣0-9]*$";

		// 한글일 경우
		if(nickname.matches(ko)) {
			System.out.println("한글");
			if(nickname.length() < 1 || nickname.length() > 8) {	// 1~8자
				return 2;
			}
		} else if(nickname.matches(en)) { // 영문일 경우
			System.out.println("영문");
			if(nickname.length() < 1 || nickname.length() > 16) {	// 1~16자
				return 2;
			}
		} else {
			System.out.println("한글x영문x");
		}
		// 한글 영문 조합 고려 필요

		if(nickname.matches(regex)) {
			System.out.println("특문포함");
			return 3; // 특수문자 포함
		} else {
			return 0;
		}
	}

	public boolean checkEmail(String email) {
		System.out.println(email);
		String regex = "^[_a-zA-Z0-9-\\\\.]+@[\\\\.a-zA-Z0-9-]+\\\\.[a-zA-Z]+$";
		if(email.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkPassword(String password) {
		// 알파벳 + 숫자 (+ 특수문자) 조합 8~20자리
		//String regex = "^[a-zA-Z0-9]*$";	//영문 + 숫자
		String regex = "([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])";	// 영문 + 숫자 + 특수문자 조합
		if(password.length() < 8 || password.length() > 20) {
			return false;
		}
		if(password.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getClientIp(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("Proxy-Client-IP");
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("WL-Proxy-Client-IP"); 
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("HTTP_CLIENT_IP"); 
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("X-Real-IP");  
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getHeader("X-RealIP");  
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
			ip = request.getRemoteAddr(); 
		}
		return ip;
	}

	@Override
	public String getClientBrowser(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String browser = request.getHeader("user-agent");
		return browser;
	}

	@Override
	public String getOriginalPhone(String phone) {
		// TODO Auto-generated method stub
		if(StringUtils.beginsWithIgnoreCase(phone, "+1")) {
			System.out.println("UnitedStates");
			return phone.split("[+]1")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+44")) {
			System.out.println("United Kingdom");
			return phone.split("[+]44")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+1")) {
			System.out.println("Canada");
			return phone.split("[+]1")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+61")) {
			System.out.println("Australia");
			return phone.split("[+]61")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+65")) {
			System.out.println("Singapore");
			return phone.split("[+]65")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+852")) {
			System.out.println("Hongkong");
			return phone.split("[+]852")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+81")) {
			System.out.println("Japan");
			return phone.split("[+]81")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+86")) {
			System.out.println("China");
			return phone.split("[+]86")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+886")) {
			System.out.println("Taiwan");
			return phone.split("[+]886")[1];
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+82")) {
			System.out.println("Korea");
			return phone.split("[+]82")[1];
		} else {
			System.out.println("no code");
			return phone;
		}
		
	}

	@Override
	public String getCountryCode(String phone) {
		if(StringUtils.beginsWithIgnoreCase(phone, "+1")) {
			System.out.println("UnitedStates");
			return "+1";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+44")) {
			System.out.println("United Kingdom");
			return "+44";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+1")) {
			System.out.println("Canada");
			return "+1";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+61")) {
			System.out.println("Australia");
			return "+61";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+65")) {
			System.out.println("Singapore");
			return "+65";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+852")) {
			System.out.println("Hongkong");
			return "+852";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+81")) {
			System.out.println("Japan");
			return "+81";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+86")) {
			System.out.println("China");
			return "+86";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+886")) {
			System.out.println("Taiwan");
			return "+886";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+82")) {
			System.out.println("Korea");
			return "+82";
		} else {
			System.out.println("no code");
			return phone;
		}
	}
	
	@Override
	public String getLocaleCode(String phone) {
		if(StringUtils.beginsWithIgnoreCase(phone, "+1")) {
			System.out.println("UnitedStates");
			return "en";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+81")) {
			System.out.println("Japan");
			return "ja";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+86")) {
			System.out.println("China");
			return "zh";
		} else if(StringUtils.beginsWithIgnoreCase(phone, "+82")) {
			System.out.println("Korea");
			return "ko";
		} else {
			System.out.println("no code");
			return "ko";
		}
	}

	@Override
	public int update_pw_renew_date(int idx, String date) {
		// TODO Auto-generated method stub
		return memberRepository.updatePwRenewDate(idx, date);
	}

	@Override
	public int update_pw_renew_date_1month(int idx) throws ParseException {
		// TODO Auto-generated method stub
		
		//String date_str = findByIdx(idx).getPw_renew_date();
		String date_str = set_now_date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = df.parse(date_str);
		
		// date를 2주후로 변경
		Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -2);
        String date2 = df.format(cal.getTime());
		
		return memberRepository.updatePwRenewDate(idx, date2);
	}

	@Override
	public int update_info(int member_idx, String name, String nickname, String birth, char gender, String phone,
			String thumbnail_url) {
		// TODO Auto-generated method stub
		return memberRepository.updateInfo(member_idx, name, nickname, birth, gender, phone, thumbnail_url);
	}

	@Override
	public int auth_firebase_token(String token, String phone, HttpSession session) {
		// TODO Auto-generated method stub
		String session_token = (String) session.getAttribute("firebase_success_token");
		System.out.println(token + " : " + session_token);
		if (session_token != null && session_token.equals(token) && phone != null && phone.length() > 0) {
			session.removeAttribute("firebase_success_token");
    		return 1;
        } else {
        	return 0;
        }
	}

	@Override
	public String set_spring_security_auth(String email, HttpSession session) {
		// TODO Auto-generated method stub
		UserDetails userDetails = userDetailsService.loadUserByUsername(email); 
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities()); 
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(authentication);
		
		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext); 
		return null;
	}

	@Override
	public Member findByEmailAndPhone(String email, String phone) {
		// TODO Auto-generated method stub
		return memberRepository.findByEmailAndPhone(email, phone);
	}

	@Override
	public String removeSessionTempFile(String service, HttpSession session) {
		// TODO Auto-generated method stub
		if(service.equals("product")) {
			if(session.getAttribute("product_tmp_file") != null) { session.removeAttribute("product_tmp_file"); }
		} else if(service.equals("service")) {
			if(session.getAttribute("service_tmp_file") != null) { session.removeAttribute("service_tmp_file"); }
		} else if(service.equals("gallery")) {
			if(session.getAttribute("gallery_tmp_file") != null) { session.removeAttribute("gallery_tmp_file"); }
		} else if(service.equals("diary")) {
			if(session.getAttribute("diary_tmp_file") != null) { session.removeAttribute("diary_tmp_file"); }
		} else {
			System.out.println("잘못된 서비스 이름");
			return "failed";
		}
		return "success";
	}

	@Override
	public String removeTmpFileS3(String service, HttpSession session) {
		// TODO Auto-generated method stub
		List<TmpFileInfo> file_info;
		
		if(service.equals("product")) {
			if(session.getAttribute("product_tmp_file") != null) { 
				file_info = (List<TmpFileInfo>) session.getAttribute("product_tmp_file");
				for(int i=0 ; i<file_info.size() ; i++) {
					S3Service.deleteThumbnail("community/qna_tmp", file_info.get(i).getServer_file_name());
				}
			}
		} else if(service.equals("service")) {
			if(session.getAttribute("service_tmp_file") != null) {
				file_info = (List<TmpFileInfo>) session.getAttribute("service_tmp_file");
				for(int i=0 ; i<file_info.size() ; i++) {
					S3Service.deleteThumbnail("community/service_tmp", file_info.get(i).getServer_file_name());
				}
			}
		} else if(service.equals("gallery")) {
			if(session.getAttribute("gallery_tmp_file") != null) {
				file_info = (List<TmpFileInfo>) session.getAttribute("gallery_tmp_file");
				for(int i=0 ; i<file_info.size() ; i++) {
					S3Service.deleteThumbnail("community/gallery_tmp", file_info.get(i).getServer_file_name());
				}
			}
		} else {
			System.out.println("잘못된 서비스 이름");
			return "failed";
		}
		return null;
	}
	
	public boolean between_number(int start_date, int end_date, int target_date) {
		if(start_date <= target_date && end_date >= target_date) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getCookieLocale(HttpServletRequest request) {
		// TODO Auto-generated method stub
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("lang")) {
					return cookie.getValue();
				}
			}
		} 
		return "ko";
	}

}
