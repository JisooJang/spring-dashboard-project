package com.littleone.littleone_web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Device;
import com.littleone.littleone_web.domain.Device_response;
import com.littleone.littleone_web.repository.DeviceRepository;

@Service
public class DeviceService {
	@Autowired
	private DeviceRepository repository;
	
	public Device save(Device device) {
		return repository.save(device);
	}
	
	public Device findBySerialNum(String serial_num) {
		return repository.findBySerial_num(serial_num);
	}
	
	public Device findBySerialNumAndType(int member_idx, String serial_num, char type) {
		return repository.findBySerialNumAndType(member_idx, serial_num, type);
	}
	
	public List<Device> findByMember(int member_idx) {
		return repository.findByMember(member_idx);
	}
	
	public Device_response findByMemberResponse(int member_idx) {
		List<Device> list = repository.findByMember(member_idx);
		Device_response res = new Device_response();
		int count = 0;
		for(int i=0 ; i<list.size() ; i++) {
			Device tmp = list.get(i);
			if(tmp.getType() == '1') {
				res.setTemp(tmp);
				count++;
			} else if(tmp.getType() == '2') {
				res.setPeepee(tmp);
				count++;
			} else {
				res.setBottle(tmp);
				count++;
			}
			res.setCount(count);
		}
		return res;
	}
	
	public int update(int member_idx, char type, String serial_num, String firmware, String mac_address, char unit, char switch_chk, String update_time) {
		return repository.update(member_idx, type, serial_num, firmware, mac_address, unit, switch_chk, update_time);
	}

}
