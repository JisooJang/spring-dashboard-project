package com.littleone.littleone_web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Dashboard;

public interface DashboardRepository extends Repository<Dashboard, Integer> {
	
	public Dashboard save(Dashboard dashboard);
	public void delete(int member_idx);
	public Dashboard findOne(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE dashboard SET temp = ?2, peepee = ?3, bottle = ?4, infant_info = ?5, group_info = ?6 WHERE member_idx = ?1", nativeQuery=true)
	public int update(int member_idx, String temp, String peepee, String bottle,/* String diary,*/ String infant_info, String group_info);
}
