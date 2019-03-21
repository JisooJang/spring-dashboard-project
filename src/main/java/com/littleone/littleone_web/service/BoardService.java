package com.littleone.littleone_web.service;

import java.util.List;

import com.littleone.littleone_web.domain.Alert_board;
import com.littleone.littleone_web.domain.Board;
import com.littleone.littleone_web.domain.Board_file;
import com.littleone.littleone_web.domain.Board_index;
import com.littleone.littleone_web.domain.Board_likes;
import com.littleone.littleone_web.domain.Comment;
import com.littleone.littleone_web.domain.Comment_check;
import com.littleone.littleone_web.domain.Comment_likes;

public interface BoardService {
	public Board save(Board board);
	public Board_file save(Board_file bf);
	public Board_likes save_likes(Board_likes likes);
	public Board findOne(int board_idx);
	public List<Board_file> findBf(int board_idx);
	public Board_likes find_likes(int member_idx, int board_idx);
	public int updateLikes(int board_idx);
	public int updateLikesM(int board_idx);
	public int update(int board_idx, String subject, String contents);
	public int update_file(int board_idx, String subject, String contents, String date_updated);
	public int update_file2(int board_idx, String subject, String contents, String hashtag, String date_updated);
	public int getCountQNA();
	public int getCountService();
	public void delete(int idx);
	public void deleteBF(int idx);
	public void deleteLikes(int member_idx, int board_idx);
	public List<Board_index> getQnaBoard();
	public List<Board_index> getQnaBoardPaging(int page_index);
	public List<Board_index> getServiceBoardPaging(int page_index);
	public List<Board_index> getQnaBoardNotice();
	public List<Board_index> getServiceBoardNotice();
	public List<Board_index> getListByMember_idx(int member_idx, int page_index);
	public List<Board_index> searchBySubject(String search_word, char category, int page_index);
	public List<Board_index> searchByNickname(String nickname, char category, int page_index);
	public List<Board_index> searchByContents(String contents, char category, int page_index);
	public Board_file findByFile(int board_idx, String server_filename);
	public void deleteByFile(int board_idx, String server_filename);
	public List<Comment> findComment(int board_idx);
	public List<Comment> findMyComment(int writer_idx);
	public List<Comment_check> getListCommentCheck(int board_idx);
	public Comment save(Comment comment);
	public Comment findCommentOne(int comment_idx);
	public int updateComment(int comment_idx, String contents, String date_updated);
	public void deleteComment(int comment_idx);
	public int getCommentCount(int board_idx);
	public int updateHits(int board_idx);
	public int updateCommentLikes(int comment_idx);
	public int updateCommentLikesM(int comment_idx);
	public Comment_likes find(int member_idx, int comment_idx);
	public Comment_likes saveCommentLikes(Comment_likes cl);
	public void deleteCommentLikes(int member_idx, int comment_idx);
	public int getCountMemberBoard(int member_idx);
	public int countComment(int member_idx);
	public Board getPreviousBoard(int board_idx, char category);
	public Board getNextBoard(int board_idx, char category);
	public long getTotalLikes(int member_idx);
	public List<Board_index> setBoardIndex(List<Board_index> list);
	public int updateBf(int idx, String originalName, String serverName, String file_url, int file_size);
	public void deleteByBoard(int board_idx);
	public void deleteCommentByBoard(int board_idx);
	public int getCountMemberGallery(int member_idx);
	public int getGalleryCount();
	public List<Board_index> getQnaeBoardPagingByLikes(int page_index);
	public List<Board_index> getQnaBoardPagingByOld(int page_index);
	public List<Board_index> getQnaBoardPagingByComment(int page_index);
	public List<Board_index> getQnaBoardPagingByHits(int page_index);	
	public List<Board_index> getServiceBoardPagingByLikes(int page_index);
	public List<Board_index> getServiceBoardPagingByOld(int page_index);
	public List<Board_index> getServiceBoardPagingByComment(int page_index);
	public List<Board_index> getServiceBoardPagingByHits(int page_index);
	public int updateContents(int board_idx, String contents);
	public String parsingHtml(String contents);
}
