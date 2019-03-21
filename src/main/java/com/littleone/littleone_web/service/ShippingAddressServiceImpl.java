package com.littleone.littleone_web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Shipping_address;
import com.littleone.littleone_web.repository.Shipping_addressRepository;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

	@Autowired
	private Shipping_addressRepository addressRepository;
	
	@Override
	public Shipping_address insert(Shipping_address address) {
		// TODO Auto-generated method stub
		return addressRepository.save(address);
	}

	@Override
	public Shipping_address findByIdx(int idx) {
		// TODO Auto-generated method stub
		return addressRepository.findOne(idx);
	}

	@Override
	public List<Shipping_address> findByMember_idx(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.findByMember_idx(member_idx);
	}

	@Override
	public Shipping_address find_default_address(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.find_default_address(member_idx);
	}

	@Override
	public int updateAddress(int idx, String recipient_name, String recipient_phone, String recipient_phone2,
			String address_name, String address1, String address2, String zipcode) {
		// TODO Auto-generated method stub
		return addressRepository.updateAddress(idx, recipient_name, recipient_phone, recipient_phone2, address_name, address1, address2, zipcode);
	}

	@Override
	public int getIdx(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.getIdx(member_idx);
	}

	@Override
	public void delete(int idx) {
		// TODO Auto-generated method stub
		addressRepository.delete(idx);
	}

	@Override
	public Shipping_address findMember_info_true(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.findMember_info_true(member_idx);
	}

	@Override
	public int update_default_address(int idx, int member_idx) {
		// TODO Auto-generated method stub
		// 1. count * default_address가 1이면,
		int count = addressRepository.count_default(member_idx);
		if (count == 1) {	
			// 이미 기본값으로 설정된 행의 default_address를 n으로 update
			// 해당 idx 행의 default_address를 y로 update
			
		} else if(count == 0) {	// 기본배송지 설정이 안되어있으면
			// 해당 idx 행의 default_address를 y로 update
		}
		else {
			// 비정상적인 경우
			System.out.println("기본 배송지가 2개 이상임.");
		}
		
		return 0;
	}

	@Override
	public int cancel_default_check(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.cancel_default_check(member_idx);
	}

	@Override
	public int update_default_check_y(int idx) {
		// TODO Auto-generated method stub
		return addressRepository.update_default_check_y(idx);
	}

	@Override
	public int count_by_member_idx(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.count_by_member_idx(member_idx);
	}

	@Override
	public boolean validation_check(Shipping_address address) {
		// TODO Auto-generated method stub
		if(address.getRecipient_name() == null || address.getRecipient_phone() == null || address.getAddress_name() == null 
				|| address.getAddress1() == null || address.getAddress2() == null || address.getZipcode() == null) {
			// 각 컬럼 별 사이즈도 체크할것
			return false;
		} else if(address.getRecipient_name().length() == 0|| address.getRecipient_phone().length() == 0 || address.getAddress_name().length() == 0 
				|| address.getAddress1().length() == 0 || address.getAddress2().length() == 0 || address.getZipcode().length() == 0) {
			return false;
		} else {
			return true;
		}
			
	}

	@Override
	public boolean check_default_check(int idx) {
		// TODO Auto-generated method stub
		if(addressRepository.check_default_check(idx) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int count_default(int member_idx) {
		// TODO Auto-generated method stub
		return addressRepository.count_default(member_idx);
	}

}
