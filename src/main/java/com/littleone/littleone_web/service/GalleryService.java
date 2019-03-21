package com.littleone.littleone_web.service;

import java.text.ParseException;
import java.util.List;

import com.littleone.littleone_web.domain.Board;
import com.littleone.littleone_web.domain.Comment_check;
import com.littleone.littleone_web.domain.Gallery;
import com.littleone.littleone_web.domain.GalleryLikesInfo;
import com.littleone.littleone_web.domain.Gallery_index;
import com.littleone.littleone_web.domain.Id_comment;
import com.littleone.littleone_web.domain.Id_comment_likes;
import com.littleone.littleone_web.domain.Id_likes;

public interface GalleryService {
	public List<Gallery_index> getListIndexPaging(int page_index);
	public List<Gallery_index> getListByLikes(int page_index);
	public List<Gallery_index> getListByOld(int page_index);
	public List<Gallery_index> getListByHits(int page_index);
	public List<Gallery_index> getListByComments(int page_index);	
	public List<Gallery_index> setCommentCount(List<Gallery_index> list, int member_idx);
	public List<Gallery_index> getGalleryByMember_idx(int member_idx, int page_index);
	//public List<Gallery_index> geGalleryByMember_idx_group(int member_idx);
	
	public Gallery save(Gallery gallery);
	public Gallery findOne(int idx);
	
	public int updateHits(int idx);
	
	public List<Gallery_index> getListBySubject(String subject);
	public List<Gallery_index> getListByContents(String contents);
	public List<Gallery_index> getListByNickname(String nickname);
	public List<Gallery_index> getListByHashTag(String tag);
	
	public List<Gallery_index> getGalleryByHashTag(String tag, int page_index);	// 페이징처리
	public List<Gallery_index> getListByDates(String date, int member_idx);
	
	public Id_likes findLikes(int member_idx, int diary_idx);
	public List<GalleryLikesInfo> findLikesByGallery(int diary_idx);
	public Id_likes saveLikes(Id_likes likes);
	
	public void deleteIdLikes(int member_idx, int diary_idx);
	public int updateComment(int comment_idx, String contents, String date_updated);
	
	public List<Comment_check> getListCommentCheck(int diary_idx);
	
	public Id_comment findComment(int comment_idx);
	public List<Id_comment> getCommentListPaging(int gallery_idx, int page_index);
	public Id_comment_likes findCommentLikes(int member_idx, int comment_idx);
	public Id_comment_likes saveCommentLikes(Id_comment_likes likes);
	
	public int getGalleryCountMember(int member_idx);
	
	public int plusCommentLikes(int member_idx, int comment_idx);
	public int minusCommentLikes(int member_idx, int comment_idx);
	
	public String setViewDate(String date) throws ParseException;
	
}	
