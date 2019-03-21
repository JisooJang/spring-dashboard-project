package com.littleone.littleone_web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Member_group;

public interface Member_groupRepository  extends Repository<Member_group, Integer>{

	public Member_group findOne(int member_idx);
	
	@Query(value="SELECT * FROM member_group WHERE member_idx = ?1 AND group_idx = ?2", nativeQuery=true)
	public Member_group findByMemberAndGroup(int member_idx, int group_idx);
	
	public Member_group save(Member_group group);
	
	@Query(value="SELECT * FROM member_group WHERE member_idx = ?1 AND authority = '1'", nativeQuery=true)
	public Member_group check_authority(int member_idx);
	
	@Query(value="SELECT group_idx FROM member_group WHERE member_idx = ?1 OR member_idx = ?2;", nativeQuery=true)
	public int[] same_group_check(int inviter, int recipient);
	
	@Query(value="SELECT * FROM member_group WHERE group_idx = ?1", nativeQuery=true)
	public List<Member_group> findByGroup_idx(int group_idx);
	
	@Query(value="SELECT * FROM member_group WHERE group_idx = ?1 AND authority = '1'", nativeQuery=true)
	public Member_group findByGroup_idxAdmin(int group_idx);
	
	@Query(value="DELETE FROM member_group WHERE member_idx = ?1 AND group_idx = ?2", nativeQuery=true)
	public void delete(int member_idx, int group_idx);
}
