package com.littleone.littleone_web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Infant_thumbnail;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.repository.InfantRepository;
import com.littleone.littleone_web.repository.InfantThumbnailRepository;
import com.littleone.littleone_web.repository.Member_infantRepository;

@Service
public class InfantServiceImpl implements InfantService {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private InfantRepository infantRepository;
	
	@Autowired
	private InfantThumbnailRepository itRepository;
	
	@Autowired
	private Member_infantRepository miRepository;

	public Infant insert(Infant infant) {
		// TODO Auto-generated method stub
		return infantRepository.save(infant);
	}

	@Override
	public Infant findOne(int idx) {
		// TODO Auto-generated method stub
		return infantRepository.findOne(idx);
	}

	@Override
	public List<Infant> findByGroup_idx(int group_idx) {
		// TODO Auto-generated method stub
		return infantRepository.findByGroup_idx(group_idx);
	}

	@Override
	public List<Member_infant> findByMember(int member_idx) {
		// TODO Auto-generated method stub
		return miRepository.findByMember_idx(member_idx);
	}

	@Override
	public Member_infant find(int member_idx, int infant_idx) {
		// TODO Auto-generated method stub
		return miRepository.find(member_idx, infant_idx);
	}

	@Override
	public void delete(int idx) {
		// TODO Auto-generated method stub
		infantRepository.delete(idx);
	}

	@Override
	public int updateInfantTemp(int idx, float weight, float height) {
		// TODO Auto-generated method stub
		return infantRepository.updateInfantTemp(idx, weight, height);
	}

	@Override
	public int updateInfant(int idx, String name, String birth, char sex,  float weight, float height, String blood_type) {
		// TODO Auto-generated method stub
		return infantRepository.updateInfant(idx, name, birth, sex, weight, height, blood_type);
	}

	@Override
	public int updateThumbanil(int infant_idx, String origin_name, String server_file, String url) {
		// TODO Auto-generated method stub
		return itRepository.updateThumbanil(infant_idx, origin_name, server_file, url);
	}
	
	@Override
	public Infant_thumbnail saveThumbnail(Infant_thumbnail tn) {
		return itRepository.save(tn);
	}

	@Override
	public Infant_thumbnail findThumbnail(int infant_idx) {
		// TODO Auto-generated method stub
		return itRepository.findOne(infant_idx);
	}

	@Override
	public int getInfantYear(String infant_birth) throws ParseException {	//yyyymmdd
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		Date date = df.parse(infant_birth.substring(0, 6));
		
		SimpleDateFormat df2 = new SimpleDateFormat("yyyyMM");
		Date today_date = df2.parse(df2.format(new Date()));
		
		long diff = today_date.getTime() - date.getTime();
        
		//초	분 시간 하루 월
		int result = (int) ((diff / 1000) / 60 / 60 / 24 / 30);
				
		
		return result;
	}
}
