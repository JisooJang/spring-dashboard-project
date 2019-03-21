package com.littleone.littleone_web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Comment_likes;

public interface Comment_likesRepository extends Repository<Comment_likes, Integer>{
	
	@Query(value="SELECT * FROM comment_likes WHERE member_idx = ?1 AND comment_idx = ?2", nativeQuery=true)
	public Comment_likes find(int member_idx, int comment_idx);
	
	@Query(value="INSERT INTO comment_likes(member_idx, comment_idx) VALUES(?1, ?2)", nativeQuery=true)
	public Comment_likes save(int member_idx, int comment_idx);
	
	@Query(value="DELETE FROM comment_likes WHERE member_idx = ?1 AND comment_idx = ?2", nativeQuery=true)
	public void delete(int member_idx, int comment_idx);
}
