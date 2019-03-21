package com.littleone.littleone_web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Alert_board;

public interface Alert_boardRepository extends Repository<Alert_board, Integer> {
	public Alert_board findOne(int idx);
	@Query(value="SELECT * FROM alert_board WHERE comment_idx = ?1", nativeQuery=true)
	public Alert_board findByComment_idx(int comment_idx);
	public Alert_board save(Alert_board alert_board);
	public void delete(int idx);
}
