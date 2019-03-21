package com.littleone.littleone_web.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.littleone.littleone_web.domain.Board;
import com.littleone.littleone_web.domain.Board_index;

public interface BoardRepository extends Repository<Board, Integer>{
	public Board save(Board board);
	
	@Query(value="SELECT idx, category, member_idx, subject, contents, hashtag, hits, likes, DATE_FORMAT(date_created, '%Y-%m-%d %H:%i') as date_created, DATE_FORMAT(date_updated, '%Y-%m-%d %H:%i') as date_updated, notice, public_check from board WHERE idx = ?1", nativeQuery=true)
	public Board findOne(int board_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET likes = likes + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikes(int board_idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET likes = likes - 1 WHERE idx = ?1", nativeQuery=true)
	public int updateLikesM(int board_idx);
	
	@Query(value="DELETE FROM board WHERE idx = ?1", nativeQuery=true)
	public void delete(int idx);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET subject = ?2, contents = ?3 WHERE idx = ?1", nativeQuery=true)
	public int update(int board_idx, String subject, String contents);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET contents = ?2 WHERE idx = ?1", nativeQuery=true)
	public int updateContents(int board_idx, String contents);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET subject = ?2, contents = ?3, date_updated = ?4 WHERE idx = ?1", nativeQuery=true)
	public int update_file(int board_idx, String subject, String contents, String date_updated);
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET subject = ?2, contents = ?3, hashtag = ?4, date_updated = ?5 WHERE idx = ?1", nativeQuery=true)
	public int update_file2(int board_idx, String subject, String contents, String hashtag, String date_updated);
	
	@Query(value="SELECT * FROM board WHERE category = '1' ORDER BY date_created DESC", nativeQuery=true)
	public List<Board> getQnaBoard();
	
	@Query(value="SELECT * FROM board WHERE member_idx = ?1", nativeQuery=true)
	public List<Board> getListByMember_idx(int member_idx);
	
	@Query(value="SELECT * FROM board WHERE category = '1' AND subject like %?1%", nativeQuery=true)
	public List<Board> searchBySubject(String search_word);
	
	@Query(value="SELECT * FROM board WHERE category = '1' AND contents like %?1%", nativeQuery=true)
	public List<Board> searchByContents(String contents);
	
	@Query(value="SELECT b.idx, b.category, b.member_idx, b.subject, b.contents, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'),"
			+ " DATE_FORMAT(b.date_updated, '%Y-%m-%d %H:%i:%s') FROM board b, member m WHERE b.member_idx = m.idx AND NOT m.member_type = 'a' AND b.category = '1'", nativeQuery=true)
	public List<Board> getQnaBoardPaging();
	
	@Query(value="SELECT count(*) FROM board WHERE category = '1'", nativeQuery=true)
	public int getCountQNA();
	
	@Query(value="SELECT count(*) FROM board WHERE category = '2'", nativeQuery=true)
	public int getCountService();
	
	@Transactional
	@Modifying(clearAutomatically=true)
	@Query(value="UPDATE board SET hits = hits + 1 WHERE idx = ?1", nativeQuery=true)
	public int updateHits(int board_idx);
	
	@Query(value="SELECT count(*) FROM board WHERE member_idx = ?1 AND category != '3'", nativeQuery=true)
	public int getCountMemberBoard(int member_idx);
	
	@Query(value="SELECT count(*) FROM board WHERE member_idx = ?1 AND category = '3'", nativeQuery=true)
	public int getCountMemberGallery(int member_idx);
	
	@Query(value="select * from board as b where b.category = ?2 AND b.idx=(SELECT MAX(o.idx) FROM board AS o WHERE o.category = ?2 AND o.idx < ?1)", nativeQuery=true)
	public Board getPreviousBoard(int board_idx, char category);
	
	@Query(value="select * from board as b where b.category = ?2 AND b.idx=(SELECT MIN(o.idx) FROM board AS o WHERE o.category = ?2 AND o.idx > ?1)", nativeQuery=true)
	public Board getNextBoard(int board_idx, char category);
	
	@Query(value="SELECT * FROM board WHERE member_idx = ?1 AND category = '3' AND public_check = '1'", nativeQuery=true)
	public List<Board> getGalleryByMember_idx(int member_idx);
	
	@Query(value="SELECT count(*) FROM board WHERE category = '3' AND public_check = '1'", nativeQuery=true)
	public int getGalleryCount();
	
	@Query(value="SELECT * FROM board WHERE category = '3' AND public_check = '1' AND hashtag LIKE '%,:tag,%' OR hashtag LIKE ':tag,%' OR hashtag LIKE '%,:tag' OR hashtag = ':tag'", nativeQuery=true)
	public List<Board> getGalleryByHashTag(String tag);
}
