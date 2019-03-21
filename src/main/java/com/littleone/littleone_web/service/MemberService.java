package com.littleone.littleone_web.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.SearchMember;

public interface MemberService {
	public int getIdx(String email);
	public HashMap<Integer, Member> login(String email, String password);
	public Member auth(String email, String password);
	public Member admin_login(String email, String password);
	
	public Member auth_member(String email, String password);
	public Member join(Member member);
	
	public Member findByEmail(String email);
	public String findEmail(String email);
	
	public Member findByIdx(int idx);
	
	public boolean duplicate_check(String email);
	public String encode_password(String password);
	public boolean decode_password(CharSequence rawPassword, String encodedPassword);
	public String get_encoded_password(CharSequence rawPassword);
	public int update_password(int idx, String new_password, String now_date);
	public int update_info(int member_idx, String name, String nickname, String birth, char gender, String phone, String thumbnail_url);
	public int updatePhone(int idx, String phone);
	public String set_now_date();
	public String set_now_time();
	public Member find_social_member(String email, char social_code);
	public boolean find_not_social_member(String email);
	public boolean phone_auth_check();
	public List<String> find_email(String phone);
	
	public void delete(int idx);
	
	public String getUniqId();
	
	public String getUniqState();
	
	public String getUniqFileName(String original_filename);
	
	public boolean checkFileName(String original_filename);
	
	public List<SearchMember> searchMember(String email_pattern, String session_email);
	
	public List<SearchMember> searchMemberByPhone(String phone_number, int idx);
	
	public List<SearchMember> searchMemberByNickname(String nickname, int idx);
	
	public String duplicate_check_nickname(String nickname);
	
	public int findIdxByNickname(String nickname);
	
	public long checkPw_renew_date(String pw_renew_date);
	
	public String requestReplace (String paramValue, String gubun);
	
	public boolean checkinputValue(String value);
	
	public boolean checkNickname(String nickname);
	
	public int checkNickname_mypage(String nickname, int idx);
	
	public String checkDI(String di);
	
	public boolean checkEmail(String email);
	
	public boolean checkPassword(String password);
	
	public String getClientIp(HttpServletRequest request);
	
	public String getClientBrowser(HttpServletRequest request);
	
	public String getOriginalPhone(String phone);
	
	public String getCountryCode(String phone);
	
	public int update_pw_renew_date(int idx, String date);
	
	public int update_pw_renew_date_1month(int idx) throws ParseException;
	
	public int auth_firebase_token(String token, String phone, HttpSession session);
	
	public String set_spring_security_auth(String email, HttpSession session);
	
	public Member findByEmailAndPhone(String email, String phone);
	
	public String removeSessionTempFile(String service, HttpSession session);
	
	public String removeTmpFileS3(String service, HttpSession session);
	
	public String getCookieLocale(HttpServletRequest request);
	
	String getLocaleCode(String phone);
}
