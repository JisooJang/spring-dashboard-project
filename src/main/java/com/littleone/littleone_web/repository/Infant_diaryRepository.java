package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import com.littleone.littleone_web.domain.Infant_diary;

public interface Infant_diaryRepository extends Repository<Infant_diary, Integer> {

	public Infant_diary save(Infant_diary diary);
	
	@Query(value="SELECT idx, infant_idx, member_idx, subject, contents, event_date, SUBSTRING(date_created,1,10) AS date_created, SUBSTRING(date_updated,1,10) AS date_updated, hashtag, share, hits, likes FROM infant_diary WHERE idx = ?1", nativeQuery=true)
	public Infant_diary findOne(int idx);
	
	@Query(value="SELECT * FROM infant_diary WHERE infant_idx = ?1", nativeQuery=true)
	public List<Infant_diary> findByInfant_idx(int infant_idx);
	
	@Query(value="SELECT * FROM infant_diary WHERE infant_idx = ?1 AND member_idx = ?2", nativeQuery=true)
	public List<Infant_diary> findByInfantAndMember(int infant_idx, int member_idx);
	
	@Query(value="SELECT SUBSTRING(MAX(event_date), 1, 10) FROM infant_diary WHERE infant_idx = ?1", nativeQuery=true)
	public String checkDiary(int infant_idx);
	
	@Query(value="SELECT * FROM infant_diary WHERE infant_idx = ?1 AND public_check != '3'", nativeQuery=true)
	public List<Infant_diary> findByInfantPublic(int infant_idx);
	
	@Transactional
	@Query(value="SELECT idx, infant_idx, member_idx, subject, contents, SUBSTRING(event_date,1,10) AS event_date, DATE_FORMAT(date_created, '%H:%i') AS date_created, DATE_FORMAT(date_updated, '%H:%i') AS date_updated, hashtag, share, hits, likes FROM infant_diary WHERE infant_idx = ?1 AND SUBSTRING(event_date,1,10) = ?2 ORDER BY DATE_FORMAT(date_created, '%H:%i:%s') DESC", nativeQuery=true)
	public List<Infant_diary> findByDateDashboard(int infant_idx, String date);
	
	@Query(value="SELECT * FROM infant_diary WHERE infant_idx = ?1 AND member_idx = ?2 AND public_check = '3' AND SUBSTRING(event_date,1,10) = ?3", nativeQuery=true)
	public List<Infant_diary> findByDatePrivate(int infant_idx, int member_idx, String date);
	
	@Query(value="DELETE FROM infant_diary WHERE idx = ?1", nativeQuery=true)
	public void delete(int idx);
	
	@Query(value="DELETE FROM infant_diary WHERE diary_idx = ?1", nativeQuery=true)
	public void deleteByDiary_idx(int diary_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_diary SET subject = ?2, contents = ?3, date_updated = ?4, hashtag = ?5, share = ?6 WHERE idx = ?1", nativeQuery=true)
	public int update(int idx, String subject, String contents, String date_updated, String hashtag, char share);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_diary SET subject = ?2, contents = ?3, date_updated = ?4, hashtag = ?5 WHERE idx = ?1", nativeQuery=true)
	public int updateGallery2(int idx, String subject, String contents, String date_updated, String hashtag);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_diary SET subject = ?2, contents = ?3, date_updated = ?4 WHERE idx = ?1", nativeQuery=true)
	public int updateGallery(int idx, String subject, String contents, String date_updated);
	
	@Query(value="SELECT DISTINCT SUBSTRING(event_date, 9, 2) FROM infant_diary WHERE infant_idx = ?1 AND member_idx = ?2 AND SUBSTRING(event_date, 1,7) = ?3", nativeQuery=true)
	public List<String> checkByDate(int infant_idx, int member_idx, String date);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_diary SET hits = hits + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateHits(int diary_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_diary SET likes = likes + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikes(int diary_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_diary SET likes = likes - 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikesM(int diary_idx);
	
	@Query(value="SELECT count(*) FROM infant_diary WHERE member_idx = ?1", nativeQuery=true)
	public int getGalleryCountMember(int member_idx);
}
