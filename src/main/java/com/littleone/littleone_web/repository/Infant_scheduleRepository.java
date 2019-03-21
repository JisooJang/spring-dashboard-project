package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Infant_schedule;

public interface Infant_scheduleRepository extends Repository<Infant_schedule, Integer> {
	public Infant_schedule save(Infant_schedule schedule);
	public Infant_schedule findOne(int idx);
	
	@Query(value="SELECT * FROM infant_schedule WHERE infant_idx = ?1 AND SUBSTRING(event_date_start, 1, 10) = ?2", nativeQuery=true)
	public List<Infant_schedule> findByDate(int infant_idx, String date);
	
	@Query(value="SELECT * FROM infant_schedule WHERE infant_idx = ?1", nativeQuery=true)
	public List<Infant_schedule> findByInfant(int infant_idx);
			
	@Query(value="SELECT * FROM infant_schedule WHERE infant_idx = ?1 AND member_idx = ?2", nativeQuery=true)
	public List<Infant_schedule> findByInfantAndMember(int infant_idx, int member_idx);
	
	@Query(value="SELECT count(1) FROM infant_schedule WHERE infant_idx = ?1 AND member_idx = ?2", nativeQuery=true)
	public int checkSchedule(int infant_idx, int member_idx);
	
	@Query(value="SELECT DISTINCT SUBSTRING(event_date_start, 9, 2) FROM infant_schedule WHERE infant_idx = ?1 AND member_idx = ?2 AND SUBSTRING(event_date_start, 1,7) = ?3", nativeQuery=true)
	public List<String> checkByDate(int infant_idx, int member_idx, String date);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_schedule SET event_date_start = ?2, event_date_end = ?3, title = ?4 WHERE idx = ?1", nativeQuery=true)
	public int update(int schedule_idx, String date_start, String date_end, String title);
	
	public void delete(int idx);
}
