package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Member;

public interface MemberRepository extends Repository<Member, Integer> {
	
	public Member findOne(int idx);
	public List<Member> findAll();
	public Member findByEmail(String email);
	
	@Query(value="SELECT * FROM member WHERE email = ?1 AND phone = ?2", nativeQuery=true)
	public Member findByEmailAndPhone(String email, String phone);
	
	@Query(value="SELECT password FROM member WHERE email = ?1", nativeQuery=true)
	public String findPasswordByEmail(String email);
	
	@Query(value="SELECT email FROM member WHERE email = ?1", nativeQuery=true)
	public String findEmail(String email);
	
	public Member findByEmailAndPassword(String email, String password);
	public Member findByIdxAndPassword(int idx, String password);
	
	@Query(value="SELECT * FROM member WHERE email = ?1 AND social_code = 'x'", nativeQuery=true)
	public Member findBySocial_codeX(String email);
	
	@Query(value="SELECT * FROM member WHERE email = ?1 AND member_type = 'p' AND social_code = 'x'", nativeQuery=true)
	public Member findByEmailPersonal(String email);
	
	@Query(value="SELECT * FROM member WHERE email = ?1 AND member_type = 'c' AND social_code = 'x'", nativeQuery=true)
	public Member findByEmailCorp(String email);
	
	@Query(value="SELECT * FROM member WHERE email = ?1 AND member_type = 'a'", nativeQuery=true)
	public Member admin_login(String email);
	
	public Member save(Member member);
	
	@Query(value="SELECT idx FROM member WHERE email = ?1", nativeQuery=true)
	public String findIdxByEmail(String email);	// email로 기본키 찾기
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE member SET password = ?2, pw_renew_date = ?3 WHERE idx = ?1", nativeQuery=true)
	public int updatePassword(int idx, String password, String now_date);	// 수정된 행의 갯수 반환
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE member SET phone = ?2,  WHERE idx = ?1", nativeQuery=true)
	public int updatePhone(int idx, String phone);	// 수정된 행의 갯수 반환
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE member SET name = ?2, nickname= ?3, birth = ?4, sex = ?5, phone = ?6, thumbnail = ?7 WHERE idx = ?1", nativeQuery=true)
	public int updateInfo(int member_idx, String name, String nickname, String birth, char gender, String phone, String thumbnail_url);	// 회원 정보 수정
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE member SET pw_renew_date = ?2 WHERE idx = ?1", nativeQuery=true)
	public int updatePwRenewDate(int idx, String date);	// 회원 정보 수정
	
	@Query(value="SELECT * FROM member WHERE email = ?1 AND social_code = ?2", nativeQuery=true)
	public Member find_social_member(String email, char social_code);
	
	@Query(value="SELECT email FROM member WHERE phone = ?1", nativeQuery=true)
	public List<String> find_email(String phone);
	
	public void delete(int idx);
	
	@Query(value="SELECT * FROM member WHERE member_type = 'p' AND email like %?1%", nativeQuery=true)
	public List<Member> searchMember(String email_pattern);
	
	@Query(value="SELECT nickname FROM member WHERE nickname = ?1", nativeQuery=true)
	public String duplicate_check_nickname(String nickname);
	
	@Query(value="SELECT nickname FROM member WHERE nickname = ?1 AND idx != ?2", nativeQuery=true)
	public String duplicate_check_nicname_mypage(String nickname, int idx);
	
	@Query(value="SELECT idx FROM member WHERE nickname = ?1", nativeQuery=true)
	public int findIdxByNickname(String nickname);
	
	@Query(value="SELECT dup_info FROM member WHERE dup_info = ?1", nativeQuery=true)
	public String checkDI(String di);
}
