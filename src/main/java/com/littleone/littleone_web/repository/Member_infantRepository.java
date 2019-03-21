package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.domain.Member_infant_id;

public interface Member_infantRepository extends CrudRepository<Member_infant, Member_infant_id> {
	
	@Query(value="SELECT * FROM member_infant WHERE member_idx = ?1", nativeQuery=true)
	public List<Member_infant> findByMember_idx(int member_idx);
	
	public Member_infant save(Member_infant member_infant);
	
	@Query(value="SELECT * FROM member_infant WHERE member_idx = ?1 AND infant_idx = ?2", nativeQuery=true)
	public Member_infant find(int member_idx, int infant_idx);
}
