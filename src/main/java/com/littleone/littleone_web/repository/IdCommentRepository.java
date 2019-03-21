package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Id_comment;

public interface IdCommentRepository extends Repository <Id_comment, Integer> {

	@Query(value="SELECT count(*) FROM id_comment WHERE diary_idx = ?1", nativeQuery=true)
	public int getCommentCount(int diary_idx);
	
	@Query(value="DELETE FROM id_comment WHERE diary_idx = ?1", nativeQuery=true)
	public void deleteByDiary(int diary_idx);
	
	public Id_comment save(Id_comment comment);
	
	public Id_comment findOne(int idx);
	
	public void delete(int idx);
	
	@Query(value="SELECT * FROM id_comment WHERE diary_idx = ?1 ORDER BY date_created DESC", nativeQuery=true)
	public List<Id_comment> findCommentsByDiary(int diary_idx);
	
	@Query(value="SELECT count(*) FROM comment WHERE writer_idx = ?1", nativeQuery=true)
	public int countComment(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE id_comment SET contents = ?2, date_updated = ?3 WHERE idx = ?1", nativeQuery=true)
	public int updateComment(int comment_idx, String contents, String date_updated);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE id_comment SET likes = likes + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikes(int comment_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE id_comment SET likes = likes - 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikesM(int comment_idx);
}
