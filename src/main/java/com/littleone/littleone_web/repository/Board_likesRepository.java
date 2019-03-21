package com.littleone.littleone_web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Board_likes;

public interface Board_likesRepository extends Repository<Board_likes, Integer>{
	
	@Query(value="SELECT * FROM board_likes WHERE member_idx = ?1 AND board_idx = ?2", nativeQuery=true)
	public Board_likes find(int member_idx, int board_idx);
	
	
	public Board_likes save(Board_likes likes);
	
	@Query(value="DELETE FROM board_likes WHERE member_idx = ?1 AND board_idx = ?2", nativeQuery=true)
	public void delete(int member_idx, int board_idx);
	
}
