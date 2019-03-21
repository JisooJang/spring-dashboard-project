package com.littleone.littleone_web.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.CorpAuthInfo;
import com.littleone.littleone_web.domain.Corp_approval;
import com.littleone.littleone_web.repository.Corp_approvalRepository;

@Service
public class CorpApprovalServiceImpl implements CorpApprovalService {

	@Autowired
	private Corp_approvalRepository corpAppRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<Corp_approval> findAll() {
		// TODO Auto-generated method stub
		return corpAppRepository.findAll();
	}

	@Override
	public Corp_approval save(Corp_approval corp_approval) {
		// TODO Auto-generated method stub
		return corpAppRepository.save(corp_approval);
	}

	@Override
	public Corp_approval findByIdx(int member_idx) {
		// TODO Auto-generated method stub
		return corpAppRepository.findOne(member_idx);
	}

	@Override
	public void delete(int member_idx) {
		// TODO Auto-generated method stub
		corpAppRepository.delete(member_idx);
	}

	@Override
	public List<CorpAuthInfo> findAllInfo() {
		// TODO Auto-generated method stub
		String sqlString = "select new com.littleone.littleone_web.domain.CorpAuthInfo(corp.member_idx, corp_a.license_url, corp.corp_num, corp.corp_name, m.name, corp.field_code, corp.service_code)"
				+ " FROM Corporation AS corp, Member AS m, Corp_approval AS corp_a"
				+ " where corp.member_idx = m.idx and corp.member_idx = corp_a.member_idx";
		TypedQuery<CorpAuthInfo> query = em.createQuery(sqlString, CorpAuthInfo.class);
		return query.getResultList();
	}
}
