package com.littleone.littleone_web.service;

import com.littleone.littleone_web.domain.Thumbnail;

public interface ThumbnailService {
	public Thumbnail insert(Thumbnail thumbnail);
	public int update(int idx, String original_filename, String server_filename, int file_size);
	public Thumbnail find(int idx);
	public Thumbnail findByCategory(int member_idx, char category);
	public Thumbnail findByMemberIdx(int member_idx, char member_type);
	public void delete(int member_idx);
	public int getIdx(int member_idx, char category);
}
