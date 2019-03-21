package com.littleone.littleone_web.service;

import java.util.List;
import com.littleone.littleone_web.domain.Shipping_address;

public interface ShippingAddressService {
	
	public Shipping_address insert(Shipping_address address);
	
	public Shipping_address findByIdx(int idx);
	
	public List<Shipping_address> findByMember_idx(int member_idx);
	
	public Shipping_address findMember_info_true(int member_idx);
	
	public Shipping_address find_default_address(int member_idx);
	
	public int updateAddress(int idx, String recipient_name, String recipient_phone, String recipient_phone2,
			String address_name, String address1, String address2, String zipcode);
	
	public int getIdx(int member_idx);
	
	public void delete(int idx);
	
	public int update_default_address(int idx, int member_idx);
	
	public int cancel_default_check(int member_idx);
	
	public int update_default_check_y(int idx);
	
	public boolean check_default_check(int idx);
	
	public int count_by_member_idx(int member_idx);
	
	public int count_default(int member_idx);
	
	public boolean validation_check(Shipping_address address);
}
