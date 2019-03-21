package com.littleone.littleone_web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Corporation;

public interface CorporationRepository extends Repository<Corporation, Integer> {

	public Corporation findOne(int member_idx);
	public Corporation save(Corporation corporation);
	
	@Query(value="SELECT * FROM corporation WHERE corp_num = ?1", nativeQuery=true)	
	public Corporation findByCorp_num(String corp_num);
	
	public void delete(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE corporation SET field_code = ?2, service_code = ?3 WHERE member_idx = ?1", nativeQuery=true)	
	public int updateInfo(int idx, char field_code, char service_code);
	
	@Query(value="SELECT corp_num FROM corporation WHERE member_idx = ?1", nativeQuery=true)	
	public String getCorpNum(int idx);
	
	@Query(value="SELECT approval FROM corporation WHERE member_idx = ?1", nativeQuery=true)
	public char getApproval(int idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE corporation SET approval = 'y' WHERE member_idx = ?1", nativeQuery=true)	
	public int set_approval_y(int idx);
}
