package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Infant;

public interface InfantRepository extends Repository<Infant, Integer> {
	
	public Infant save(Infant infant);
	
	public Infant findOne(int idx);
	
	@Query(value="SELECT * FROM infant WHERE member_idx = ?1", nativeQuery=true)
	public Infant findByMember_idx(int member_idx);
	
	@Query(value="SELECT * FROM infant WHERE group_idx = ?1", nativeQuery=true)
	public List<Infant> findByGroup_idx(int group_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant SET name = ?2, birth = ?3, sex = ?4, weight = ?5, height = ?6, blood_type = ?7 WHERE idx = ?1", nativeQuery=true)
	public int updateInfant(int idx, String name, String birth, char sex, float weight, float height, String blood_type);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant SET weight = ?2, height = ?3 WHERE idx = ?1", nativeQuery=true)
	public int updateInfantTemp(int idx, float weight, float height);
	
	@Query(value="DELETE FROM infant WHERE idx = ?1", nativeQuery=true)
	public void delete(int idx);

}
