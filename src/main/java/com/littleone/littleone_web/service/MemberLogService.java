package com.littleone.littleone_web.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Board;
import com.littleone.littleone_web.domain.Member_log;
import com.littleone.littleone_web.repository.Member_logRepository;

@Service
public class MemberLogService {
	@Autowired
	private Member_logRepository repository;
	
	@PersistenceContext
	private EntityManager em;

	public Member_log save_log(Member_log log) {
		return repository.save(log);
	}
	
	public int logout(String session_id, String logout_date) {
		return repository.logout(session_id, logout_date);
	}
	
	public Member_log findOne(String session_id) {
		return repository.findOne(session_id);
	}
	
	public Member_log authSession(String session_id, int member_idx) {
		return repository.find(session_id, member_idx);
	}
	
	public Member_log authSessionWeb(String session_id, int member_idx) {
		return repository.findWeb(session_id, member_idx);
	}
}
