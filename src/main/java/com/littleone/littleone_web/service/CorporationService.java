package com.littleone.littleone_web.service;

import com.littleone.littleone_web.command.CorporationRegistRequest;
import com.littleone.littleone_web.domain.Corporation;

public interface CorporationService {
	public Corporation join(Corporation corporation);	// 기업 가입 신청 후 승인 대기 상태인 corporation 테이블에 insert
	public Corporation join_to_approval(Corporation corporation);	// 기업 가입 신청 후 승인 완료 상태인 corporation_app 테이블에 insert. Corporation에 있는 행은 삭제해야함.
	public Corporation findByIdx(int idx);
	public Corporation findByCorp_num(String corp_num);
	public int update_info(int idx, char field_code, char service_code);
	
	public void delete(int idx);
	
	public String getCorpNum(int idx);
	
	public char getApproval(int idx);
	
	public int set_approval_y(int idx);
	
	public boolean validation_check(CorporationRegistRequest request);
}
