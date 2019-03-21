package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Comment;

public interface CommentRepository extends Repository<Comment, Integer> {

	public Comment findOne(int idx);
	public Comment save(Comment comment);
	
	@Transactional
	@Query(value="DELETE FROM comment WHERE idx = ?1", nativeQuery=true)
	public void delete(int idx);
	
	@Query(value="SELECT idx, board_idx, writer_idx, category, contents, likes, DATE_FORMAT(date_created, '%Y-%m-%d %T') as date_created, DATE_FORMAT(date_updated, '%Y-%m-%d %T') as date_updated FROM comment WHERE board_idx = ?1 ORDER BY date_created DESC", nativeQuery=true)
	public List<Comment> findByBoard_idx(int board_idx);
	
	@Query(value="SELECT count(*) FROM comment WHERE board_idx = ?1", nativeQuery=true)
	public int getCommentCount(int board_idx);
	
	@Query(value="SELECT * FROM comment WHERE writer_idx = ?1", nativeQuery=true)
	public List<Comment> findByWriter_idx(int writer_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE comment SET likes = likes + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikes(int comment_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE comment SET likes = likes - 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikesM(int comment_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE comment SET contents = ?2, date_updated = ?3 WHERE idx = ?1", nativeQuery=true)
	public int updateComment(int comment_idx, String contents, String date_updated);
	
	@Query(value="SELECT count(*) FROM comment WHERE writer_idx = ?1", nativeQuery=true)
	public int countComment(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="DELETE FROM comment WHERE board_idx = ?1", nativeQuery=true)
	public void deleteByBoard(int board_idx);
}
