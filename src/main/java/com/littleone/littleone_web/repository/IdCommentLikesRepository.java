package com.littleone.littleone_web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Id_comment_likes;

public interface IdCommentLikesRepository extends Repository <Id_comment_likes, Integer> {
	
	@Query(value="SELECT * FROM id_comment_likes WHERE member_idx = ?1 AND comment_idx = ?2", nativeQuery=true)
	public Id_comment_likes find(int member_idx, int comment_idx);
	
	public Id_comment_likes save(Id_comment_likes likes);
	
	@Query(value="DELETE FROM id_comment_likes WHERE member_idx = ?1 AND comment_idx = ?2", nativeQuery=true)
	public void delete(int member_idx, int comment_idx);
}
