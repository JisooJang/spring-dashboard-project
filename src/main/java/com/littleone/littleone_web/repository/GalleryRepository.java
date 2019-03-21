package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Gallery;

public interface GalleryRepository extends Repository<Gallery, Integer>{
	public Gallery save(Gallery gallery);
	
	@Query(value="SELECT * FROM gallery ORDER BY date_created DESC", nativeQuery=true)
	public List<Gallery> getList();
	
	public Gallery findOne(int idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE gallery SET hits = hits + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateHits(int idx);
}
