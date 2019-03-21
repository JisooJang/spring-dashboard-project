package com.littleone.littleone_web.service;

import java.util.List;

import com.littleone.littleone_web.domain.CorpAuthInfo;
import com.littleone.littleone_web.domain.Corp_approval;

public interface CorpApprovalService {
	List<Corp_approval> findAll();
	List<CorpAuthInfo> findAllInfo();
	Corp_approval save(Corp_approval corp_approval);
	Corp_approval findByIdx(int member_idx);
	void delete(int member_idx);
}
