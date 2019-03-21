package com.littleone.littleone_web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Member_mng;

public interface Member_managementRepository extends Repository<Member_mng, Integer> {
	
	public Member_mng save(Member_mng member_management);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member_mng SET last_login_date = ?2 WHERE member_idx = ?1", nativeQuery=true)
	public int updateLast_login_date(int idx, String last_login_date);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member_mng SET login_err_cnt = login_err_cnt + 1 WHERE member_idx = ?1", nativeQuery=true)
	public int update_login_err_cnt(int member_idx);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member_mng SET introduction = ?2 WHERE member_idx = ?1", nativeQuery=true)
	public int update_introduction(int member_idx, String introduction);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member_mng SET login_err_cnt = 0 WHERE member_idx = ?1", nativeQuery=true)
	public int reset_login_err_cnt(int member_idx);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member_mng SET dormant_chk = '0' WHERE member_idx = ?1", nativeQuery=true)
	public int update_dormant_chk_false(int member_idx);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member_mng SET unit = ?2 WHERE member_idx = ?1", nativeQuery=true)
	public int update_unit(int member_idx, char unit);
	
	@Query(value="SELECT login_err_cnt FROM member_mng WHERE member_idx = ?1", nativeQuery=true)
	public int getLoginErrCount(int member_idx);
	
	public void delete(int member_idx);
	
	public Member_mng findOne(int member_idx);

}
