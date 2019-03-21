package com.littleone.littleone_web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.repository.Member_managementRepository;

@Service
public class MemberMngService {

	@Autowired
	private Member_managementRepository member_mngRepository;
	
	public Member_mng findOne(int member_idx) {
		// TODO Auto-generated method stub
		return member_mngRepository.findOne(member_idx);
	}
	
	public Member_mng insert(Member_mng member_management) {
		// TODO Auto-generated method stub
		return member_mngRepository.save(member_management);
	}
	
	public int update_login_date(int idx, String date) {
		// TODO Auto-generated method stub
		return member_mngRepository.updateLast_login_date(idx, date);
	}
	
	public void delete_mng(int member_idx) {
		member_mngRepository.delete(member_idx);
	}
	
	public int update_login_err_cnt(int member_idx) {
		// TODO Auto-generated method stub
		return member_mngRepository.update_login_err_cnt(member_idx);
	}

	public int getLoginErrCount(int member_idx) {
		// TODO Auto-generated method stub
		return member_mngRepository.getLoginErrCount(member_idx);
	}
	
	public int reset_login_err_cnt(int member_idx) {
		return member_mngRepository.reset_login_err_cnt(member_idx);
	}

	public int update_introduction(int member_idx, String introduction) {
		// TODO Auto-generated method stub
		return member_mngRepository.update_introduction(member_idx, introduction);
	}

	public int update_dormant_chk_false(int member_idx) {
		// TODO Auto-generated method stub
		return member_mngRepository.update_dormant_chk_false(member_idx);
	}
	
	public int update_unit(int member_idx, char unit) {
		// TODO Auto-generated method stub
		return member_mngRepository.update_unit(member_idx, unit);
	}
}
