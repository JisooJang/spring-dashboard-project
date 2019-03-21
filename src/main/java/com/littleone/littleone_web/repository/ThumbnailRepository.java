package com.littleone.littleone_web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Thumbnail;

public interface ThumbnailRepository extends Repository<Thumbnail, Integer> {

	public Thumbnail save(Thumbnail thumbnail);
	
	public Thumbnail findOne(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE thumbnail SET original_filename = ?2, server_filename = ?3, file_size = ?4 WHERE idx = ?1", nativeQuery=true)
	public int update(int idx, String original_filename, String server_filename, int file_size);
	
	@Query(value="SELECT idx FROM thumbnail WHERE member_idx = ?1 AND category = ?2", nativeQuery=true)
	public int getIdx(int member_idx, char category);
	
	public void delete(int member_idx);
	
	@Query(value="SELECT * FROM thumbnail WHERE member_idx = ?1 AND category = ?2", nativeQuery=true)
	public Thumbnail findByCategory(int member_idx, char category);
}
