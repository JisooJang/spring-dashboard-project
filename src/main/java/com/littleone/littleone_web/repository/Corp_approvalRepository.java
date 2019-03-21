package com.littleone.littleone_web.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Corp_approval;

public interface Corp_approvalRepository extends Repository<Corp_approval, Integer>{
	public List<Corp_approval> findAll();
	public Corp_approval save(Corp_approval corp_approval);
	public Corp_approval findOne(int member_idx);
	public void delete(int member_idx);
}
