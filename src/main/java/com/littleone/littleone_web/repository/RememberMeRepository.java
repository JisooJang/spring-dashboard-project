package com.littleone.littleone_web.repository;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.RememberMe;

public interface RememberMeRepository extends Repository<RememberMe, String> {
	public RememberMe save(RememberMe rm);
	public RememberMe findOne(String series);
	
	@Query(value="DELETE FROM remember_me WHERE username = ?1", nativeQuery=true)
	public void deleteByUsername(String username);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE remember_me SET token = ?2, last_used = ?3 WHERE series = ?1", nativeQuery=true)
	public int updateToken(String series, String token, Date last_used);
}
