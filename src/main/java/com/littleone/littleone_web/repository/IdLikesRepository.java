package com.littleone.littleone_web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Id_likes;

public interface IdLikesRepository extends Repository<Id_likes, Integer> {
	
	@Query(value="SELECT * FROM id_likes WHERE member_idx = ?1 AND diary_idx = ?2", nativeQuery=true)
	public Id_likes find(int member_idx, int diary_idx);
	
	public Id_likes save(Id_likes likes);
	
	@Query(value="DELETE FROM id_likes WHERE member_idx = ?1 AND diary_idx = ?2", nativeQuery=true)
	public void delete(int member_idx, int diary_idx);
}
