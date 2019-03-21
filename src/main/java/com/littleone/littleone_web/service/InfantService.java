package com.littleone.littleone_web.service;

import java.text.ParseException;
import java.util.List;

import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Infant_thumbnail;
import com.littleone.littleone_web.domain.Member_infant;

public interface InfantService {
	Infant insert(Infant infant);
	Infant findOne(int idx);
	List<Member_infant> findByMember(int member_idx);
	List<Infant> findByGroup_idx(int group_idx);
	Member_infant find(int member_idx, int infant_idx);
	void delete(int idx);
	int updateInfantTemp(int idx, float weight, float height);
	int updateInfant(int idx, String name, String birth, char sex, float weight, float height, String blood_type);
	public int updateThumbanil(int infant_idx, String origin_name, String server_file, String url);
	public Infant_thumbnail saveThumbnail(Infant_thumbnail tn);
	public Infant_thumbnail findThumbnail(int infant_idx);
	public int getInfantYear(String infant_birth) throws ParseException;
}
