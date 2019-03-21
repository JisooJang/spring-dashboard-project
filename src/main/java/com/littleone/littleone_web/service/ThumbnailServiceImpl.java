package com.littleone.littleone_web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Thumbnail;
import com.littleone.littleone_web.repository.ThumbnailRepository;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {
	
	@Autowired
	private ThumbnailRepository tnRepository;

	@Override
	public Thumbnail insert(Thumbnail thumbnail) {
		// TODO Auto-generated method stub
		return tnRepository.save(thumbnail);
	}

	@Override
	public int update(int idx, String original_filename, String server_filename, int file_size) {
		// TODO Auto-generated method stub
		
		return tnRepository.update(idx, original_filename, server_filename, file_size);
	}

	@Override
	public Thumbnail find(int idx) {
		// TODO Auto-generated method stub
		return tnRepository.findOne(idx);
	}

	@Override
	public Thumbnail findByMemberIdx(int member_idx, char member_type) {
		// TODO Auto-generated method stub
		return tnRepository.findOne(member_idx);
	}

	@Override
	public void delete(int member_idx) {
		// TODO Auto-generated method stub
		tnRepository.delete(member_idx);
	}

	@Override
	public Thumbnail findByCategory(int member_idx, char category) {
		// TODO Auto-generated method stub
		return tnRepository.findByCategory(member_idx, category);
	}

	@Override
	public int getIdx(int member_idx, char category) {
		// TODO Auto-generated method stub
		return tnRepository.getIdx(member_idx, category);
	}

}
