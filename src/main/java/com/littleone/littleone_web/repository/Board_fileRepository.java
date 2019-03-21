package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Board_file;

public interface Board_fileRepository extends Repository<Board_file, Integer> {
	public Board_file save(Board_file bf);
	
	@Query(value="SELECT * FROM board_file WHERE board_idx = ?1", nativeQuery=true)
	public List<Board_file> findByBoard_idx(int board_idx);
	
	public Board_file findOne(int idx);
	
	@Query(value="SELECT * FROM board_file WHERE board_idx = ?1 AND server_filename = ?2", nativeQuery=true)
	public Board_file findByFile(int board_idx, String server_filename);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board_file SET original_filename = ?2, server_filename = ?3, file_url = ?4, file_size = ?5 WHERE idx = ?1", nativeQuery=true)
	public int updateBf(int idx, String originalName, String serverName, String file_url, int file_size);
	
	@Query(value="DELETE FROM board_file WHERE board_idx = ?1 AND server_filename = ?2", nativeQuery=true)
	public void deleteByFile(int board_idx, String server_filename);
	
	@Query(value="DELETE FROM board_file WHERE board_idx = ?1", nativeQuery=true)
	public void deleteByBoard(int board_idx);
	
	public void delete(int idx);
}
