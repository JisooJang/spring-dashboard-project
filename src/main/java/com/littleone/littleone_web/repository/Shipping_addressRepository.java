package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Shipping_address;

public interface Shipping_addressRepository extends Repository<Shipping_address, Integer> {

	public Shipping_address save(Shipping_address address);
	public Shipping_address findOne(int idx);
	
	@Query(value="SELECT * FROM shipping_address WHERE member_idx = ?1", nativeQuery=true)
	public List<Shipping_address> findByMember_idx(int member_idx);
	
	@Query(value="SELECT * FROM shipping_address WHERE member_idx = ?1 AND member_info = 'y'", nativeQuery=true)
	public Shipping_address findMember_info_true(int member_idx);
	
	@Query(value="SELECT * FROM shipping_address WHERE member_idx = ?1 AND default_check = 'y'", nativeQuery=true)
	public Shipping_address find_default_address(int member_idx);
	
	@Query(value="SELECT * FROM shipping_address WHERE idx = ?1 AND default_check = 'y'", nativeQuery=true)
	public Shipping_address check_default_check(int idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE shipping_address SET recipient_name = ?2, recipient_phone = ?3, recipient_phone2 = ?4,"
			+ " address_name = ?5, address1 = ?6, address2 = ?7, zipcode = ?8 WHERE idx = ?1", nativeQuery=true)
	public int updateAddress(int idx, String recipient_name, String recipient_phone, String recipient_phone2,
			String address_name, String address1, String address2, String zipcode);
	
	@Query(value="SELECT idx FROM shipping_address WHERE member_idx = ?1", nativeQuery=true)
	public int getIdx(int member_idx);
	
	public void delete(int idx);
	
	@Query(value="SELECT count(*) FROM shipping_address WHERE member_idx = ?1 AND default_check = 'y'", nativeQuery=true) 
	public int count_default(int member_idx);
	
	@Query(value="SELECT count(*) FROM shipping_address WHERE member_idx = ?1", nativeQuery=true)
	public int count_by_member_idx(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE shipping_address SET default_check = 'n' WHERE member_idx = ?1 AND default_check = 'y'", nativeQuery=true) 
	public int cancel_default_check(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE shipping_address SET default_check = 'y' WHERE idx = ?1", nativeQuery=true)
	public int update_default_check_y(int idx);
}
