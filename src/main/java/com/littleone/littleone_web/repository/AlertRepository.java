package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Alert;

public interface AlertRepository extends Repository<Alert, Integer> {
	public Alert save(Alert group_alert);
	public void delete(int idx);
	
	@Query(value="SELECT * FROM alert WHERE requester = ?1 AND recipient = ?2 AND event_type = '1'", nativeQuery=true)
	public Alert duplicate_check(int requester, int recipient);
	
	public Alert findOne(int idx);
	
	/*@Query(value="SELECT * FROM group_alert WHERE recipient = ?1", nativeQuery=true)
	public List<Group_alert> getListByRecipient(int recipient);*/
	
	@Query(value="SELECT recipient FROM alert WHERE idx = ?1", nativeQuery=true)
	public int findRecipient(int idx);
	
	@Query(value="SELECT requester FROM alert WHERE idx = ?1", nativeQuery=true)
	public int findRequester(int idx);
	
	@Query(value="SELECT idx FROM alert WHERE requester = ?1 AND recipient = ?2 AND event_type = '1'", nativeQuery=true)
	public int findIdx(int requester, int recipient);
	
	@Query(value="DELETE FROM alert WHERE recipient = ?1", nativeQuery=true)
	public void deleteByRecipient(int recipient);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE alert SET read_chk = 't' WHERE idx = ?1", nativeQuery=true)
	public int updateReadChk(int idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE alert SET read_chk = 't' WHERE recipient = ?1", nativeQuery=true)
	public void readAll(int recipient);
}
