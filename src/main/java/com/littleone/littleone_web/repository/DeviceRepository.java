package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Device;

public interface DeviceRepository extends Repository<Device, String> {
	public Device save(Device device);
	
	@Query(value="SELECT * from device WHERE serial_num = ?1", nativeQuery=true)
	public Device findBySerial_num(String serial_num);
	
	@Query(value="SELECT * from device WHERE member_idx = ?1 AND serial_num = ?2 AND type = ?3", nativeQuery=true)
	public Device findBySerialNumAndType(int member_idx, String serial_num, char type);
	
	@Query(value="SELECT * from device WHERE member_idx = ?1", nativeQuery=true)
	public List<Device> findByMember(int member_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE device SET serial_num = ?3, firmware = ?4, mac_address = ?5, unit = ?6, switch_chk = ?7, update_time = ?8 WHERE member_idx = ?1 AND type = ?2", nativeQuery=true)
	public int update(int member_idx, char type, String serial_num, String firmware, String mac_address, char unit, char switch_chk, String update_time);
	
	public void delete(String serial_num);
}
