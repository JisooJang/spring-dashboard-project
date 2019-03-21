package com.littleone.littleone_web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Id_file;

public interface Id_fileRepository extends Repository<Id_file, Integer> {
	public Id_file save(Id_file file);
	public Id_file findOne(int idx);
	public void delete(int idx);

	@Query(value="SELECT * from id_file WHERE diary_idx = ?1", nativeQuery=true)
	public List<Id_file> findIdFileByDiary_idx(int diary_idx);
	
	@Query(value="DELETE from id_file WHERE diary_idx = ?1", nativeQuery=true)
	public void deleteIdFileByDiary(int diary_idx);
}
