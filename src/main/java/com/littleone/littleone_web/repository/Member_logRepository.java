package com.littleone.littleone_web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Member_log;

public interface Member_logRepository extends Repository<Member_log, String> {
	
	public Member_log save(Member_log log);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE member_log SET logout_date = ?2 WHERE session_id = ?1", nativeQuery=true)
	public int logout(String session_id, String logout_date);
	
	@Query(value="SELECT session_id, member_idx, log_type, ip_address, browser, login_date, logout_date FROM member_log WHERE session_id = ?1", nativeQuery=true)
	public Member_log findOne(String session_id);
	
	@Query(value="SELECT session_id, member_idx, log_type, ip_address, browser, login_date, logout_date FROM member_log WHERE session_id = ?1 AND member_idx =?2 AND log_type = 'a' AND logout_date IS NULL", nativeQuery=true)
	public Member_log find(String session_id, int member_idx);
	
	@Query(value="SELECT session_id, member_idx, log_type, ip_address, browser, login_date, logout_date FROM member_log WHERE session_id = ?1 AND member_idx =?2 AND log_type = 'w' AND logout_date IS NULL", nativeQuery=true)
	public Member_log findWeb(String session_id, int member_idx);
}
