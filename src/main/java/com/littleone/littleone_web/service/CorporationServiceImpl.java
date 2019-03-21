package com.littleone.littleone_web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.command.CorporationRegistRequest;
import com.littleone.littleone_web.domain.Corporation;
import com.littleone.littleone_web.repository.CorporationRepository;

@Service
public class CorporationServiceImpl implements CorporationService {

	@Autowired
	CorporationRepository corporationRepository;
	
	@Override
	public Corporation join(Corporation corporation) {
		// TODO Auto-generated method stub
		return corporationRepository.save(corporation);
	}

	@Override
	public Corporation join_to_approval(Corporation corporation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update_info(int idx, char field_code, char service_code) {
		// TODO Auto-generated method stub
		return corporationRepository.updateInfo(idx, field_code, service_code);
	}

	@Override
	public Corporation findByIdx(int idx) {
		// TODO Auto-generated method stub
		return corporationRepository.findOne(idx);
	}

	@Override
	public void delete(int idx) {
		// TODO Auto-generated method stub
		corporationRepository.delete(idx);
	}

	@Override
	public String getCorpNum(int idx) {
		// TODO Auto-generated method stub
		return corporationRepository.getCorpNum(idx);
	}

	@Override
	public char getApproval(int idx) {
		// TODO Auto-generated method stub
		return corporationRepository.getApproval(idx);
	}

	@Override
	public int set_approval_y(int idx) {
		// TODO Auto-generated method stub
		return corporationRepository.set_approval_y(idx);
	}

	@Override
	public Corporation findByCorp_num(String corp_num) {
		// TODO Auto-generated method stub
		return corporationRepository.findByCorp_num(corp_num);
	}

	@Override
	public boolean validation_check(CorporationRegistRequest request) {
		// TODO Auto-generated method stub
		// personal_email  password  name  phone corp_num  corp_name  field_code  service_code
		if(request.getPersonal_email() == null || request.getPassword() == null || request.getName() == null
				|| request.getPhone() == null || request.getCorp_num() == null || request.getCorp_name() == null 
				|| request.getService_code() == null || request.getField_code() == null || request.getDname() == null 
				|| request.getDphone() == null) {
			if(request.getCorp_num().length() != 10) {
				return false;
			}
			return false;
		} else {
			return true;
		}
	}

}
