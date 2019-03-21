package com.littleone.littleone_web.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Infant_thumbnail;

public interface InfantThumbnailRepository extends Repository <Infant_thumbnail, Integer> {
	public Infant_thumbnail save(Infant_thumbnail thumbnail);
	public Infant_thumbnail findOne(int infant_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE infant_thumbnail SET origin_file = ?2, server_file = ?3, url = ?4 WHERE infant_idx = ?1", nativeQuery=true)
	public int updateThumbanil(int infant_idx, String origin_name, String server_file, String url);
}
