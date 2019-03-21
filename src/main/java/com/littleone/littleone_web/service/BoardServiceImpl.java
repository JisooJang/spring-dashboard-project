package com.littleone.littleone_web.service;

import java.text.ParseException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Board;
import com.littleone.littleone_web.domain.Board_file;
import com.littleone.littleone_web.domain.Board_index;
import com.littleone.littleone_web.domain.Board_likes;
import com.littleone.littleone_web.domain.Comment;
import com.littleone.littleone_web.domain.Comment_check;
import com.littleone.littleone_web.domain.Comment_likes;
import com.littleone.littleone_web.repository.BoardRepository;
import com.littleone.littleone_web.repository.Board_fileRepository;
import com.littleone.littleone_web.repository.Board_likesRepository;
import com.littleone.littleone_web.repository.CommentRepository;
import com.littleone.littleone_web.repository.Comment_likesRepository;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private Board_likesRepository board_likes_r;
	
	@Autowired
	private Board_fileRepository bfRepository;
	
	@Autowired
	private CommentRepository cRepository;
	
	@Autowired
	private Comment_likesRepository clRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Board save(Board board) {
		// TODO Auto-generated method stub
		return boardRepository.save(board);
	}

	@Override
	public Board findOne(int board_idx) {
		// TODO Auto-generated method stub
		return boardRepository.findOne(board_idx);
	}

	@Override
	public Board_likes find_likes(int member_idx, int board_idx) {
		// TODO Auto-generated method stub
		return board_likes_r.find(member_idx, board_idx);
	}

	@Override
	public Board_likes save_likes(Board_likes likes) {
		// TODO Auto-generated method stub
		return board_likes_r.save(likes);
	}

	@Override
	public int updateLikes(int board_idx) {
		// TODO Auto-generated method stub
		return boardRepository.updateLikes(board_idx);
	}

	@Override
	public void deleteLikes(int member_idx, int board_idx) {
		// TODO Auto-generated method stub
		board_likes_r.delete(member_idx, board_idx);
	}

	@Override
	public int updateLikesM(int board_idx) {
		// TODO Auto-generated method stub
		return boardRepository.updateLikesM(board_idx);
	}

	@Override
	public List<Board_index> getQnaBoard() {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, g.group_idx, b.subject, b.contents, b.member.nickname, m.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board as b, Member_group as g" 
				+ " WHERE NOT m.member_type = 'a' AND b.member.idx = g.member_idx AND b.category = '1' ORDER BY b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getListByMember_idx(int member_idx, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'), b.category)"
				+ " FROM Board AS b JOIN b.member AS m" 
				+ " WHERE b.member.idx = :member_idx AND b.category != '3' ORDER BY b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		query.setParameter("member_idx", member_idx);
		int startPosition = (page_index - 1) * 10;
		int maxResult = 10;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public int update(int board_idx, String subject, String contents) {
		// TODO Auto-generated method stub
		return boardRepository.update(board_idx, subject, contents);
	}

	@Override
	public int update_file(int board_idx, String subject, String contents, String date_updated) {
		// TODO Auto-generated method stub
		return boardRepository.update_file(board_idx, subject, contents, date_updated);
	}
	
	@Override
	public int update_file2(int board_idx, String subject, String contents, String hashtag, String date_updated) {
		// TODO Auto-generated method stub
		return boardRepository.update_file2(board_idx, subject, contents, hashtag, date_updated);
	}

	@Override
	public List<Board_index> getQnaBoardPaging(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.category = '1' ORDER BY b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}
	
	@Override
	public List<Board_index> getQnaBoardNotice() {
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board as b" 
				+ " WHERE b.notice = 't' AND b.category = '1' ORDER BY b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public int getCountQNA() {
		// TODO Auto-generated method stub
		return boardRepository.getCountQNA();
	}

	@Override
	public void delete(int idx) {
		// TODO Auto-generated method stub
		boardRepository.delete(idx);
	}

	@Override
	public Board_file save(Board_file bf) {
		// TODO Auto-generated method stub
		return bfRepository.save(bf);
	}

	@Override
	public List<Board_file> findBf(int board_idx) {
		// TODO Auto-generated method stub
		return bfRepository.findByBoard_idx(board_idx);
	}

	@Override
	public List<Board_index> searchByNickname(String nickname, char category, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.member.nickname = :nickname AND category = :category";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		query.setParameter("nickname", nickname);
		query.setParameter("category", category);
		
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> searchByContents(String contents, char category, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.contents LIKE :contents AND category = :category";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		query.setParameter("contents", "%" + contents + "%");
		query.setParameter("category", category);
		
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}
	
	@Override
	public List<Board_index> searchBySubject(String search_word, char category, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.subject LIKE :search_word AND category = :category";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		query.setParameter("search_word", "%" + search_word + "%");
		query.setParameter("category", category);
		
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public Board_file findByFile(int board_idx, String server_filename) {
		// TODO Auto-generated method stub
		return bfRepository.findByFile(board_idx, server_filename);
	}

	@Override
	public void deleteBF(int idx) {
		// TODO Auto-generated method stub
		bfRepository.delete(idx);
	}

	@Override
	public void deleteByFile(int board_idx, String server_filename) {
		// TODO Auto-generated method stub
		bfRepository.deleteByFile(board_idx, server_filename);
	}

	@Override
	public List<Comment> findComment(int board_idx) {
		// TODO Auto-generated method stub
		return cRepository.findByBoard_idx(board_idx);
	}

	@Override
	public List<Comment> findMyComment(int writer_idx) {
		// TODO Auto-generated method stub
		return cRepository.findByWriter_idx(writer_idx);
	}

	@Override
	public Comment save(Comment comment) {
		// TODO Auto-generated method stub
		return cRepository.save(comment);
	}

	@Override
	public int getCommentCount(int board_idx) {
		// TODO Auto-generated method stub
		return cRepository.getCommentCount(board_idx);
	}

	@Override
	public int updateHits(int board_idx) {
		// TODO Auto-generated method stub
		return boardRepository.updateHits(board_idx);
	}

	@Override
	public int updateCommentLikes(int comment_idx) {
		// TODO Auto-generated method stub
		return cRepository.updateLikes(comment_idx);
	}

	@Override
	public int updateCommentLikesM(int comment_idx) {
		// TODO Auto-generated method stub
		return cRepository.updateLikesM(comment_idx);
	}

	@Override
	public Comment_likes find(int member_idx, int comment_idx) {
		// TODO Auto-generated method stub
		return clRepository.find(member_idx, comment_idx);
	}

	@Override
	public Comment_likes saveCommentLikes(Comment_likes cl) {
		// TODO Auto-generated method stub
		return clRepository.save(cl.getMember_idx(), cl.getComment_idx());
	}

	@Override
	public void deleteCommentLikes(int member_idx, int comment_idx) {
		// TODO Auto-generated method stub
		clRepository.delete(member_idx, comment_idx);
	}

	@Override
	public Comment findCommentOne(int comment_idx) {
		// TODO Auto-generated method stub
		return cRepository.findOne(comment_idx);
	}

	@Override
	public void deleteComment(int comment_idx) {
		// TODO Auto-generated method stub
		cRepository.delete(comment_idx);
		/*String sqlString = "DELETE FROM Comment as c" 
				+ " WHERE c.idx = :idx";
		Query query = em.createQuery(sqlString);
		query.setParameter("idx", comment_idx);
		query.executeUpdate();*/
	}

	@Override
	public int updateComment(int comment_idx, String contents, String date_updated) {
		// TODO Auto-generated method stub
		return cRepository.updateComment(comment_idx, contents, date_updated);
	}

	@Override
	public List<Comment_check> getListCommentCheck(int board_idx) {
		// TODO Auto-generated method stub
		//int idx, int board_idx, int writer_idx, String writer_thumbnail, String writer_nickname,
		//char category, String contents, int likes, String date_created, String date_updated
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Comment_check(c.idx, b.idx, c.member.idx,"
				+ " c.member.thumbnail, c.member.nickname, c.category, c.contents, c.likes, "
				+ "DATE_FORMAT(c.date_created, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(c.date_updated, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Comment as c, Board as b, Member as m" 
				+ " WHERE b.idx = :board_idx AND c.member.idx = m.idx AND c.board_idx = b.idx"
				+ " ORDER BY c.date_created DESC";
		TypedQuery<Comment_check> query = em.createQuery(sqlString, Comment_check.class);
		query.setParameter("board_idx", board_idx);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getServiceBoardPaging(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m" 
				+ " WHERE b.category = '2' ORDER BY b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getServiceBoardNotice() {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board as b" 
				+ " WHERE b.notice = 't' AND b.category = '2' ORDER BY b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public int getCountService() {
		// TODO Auto-generated method stub
		return boardRepository.getCountService();
	}

	@Override
	public int getCountMemberBoard(int member_idx) {
		// TODO Auto-generated method stub
		return boardRepository.getCountMemberBoard(member_idx);
	}

	@Override
	public int countComment(int member_idx) {
		// TODO Auto-generated method stub
		return cRepository.countComment(member_idx);
	}

	@Override
	public Board getPreviousBoard(int board_idx, char category) {
		// TODO Auto-generated method stub
		return boardRepository.getPreviousBoard(board_idx, category);
	}

	@Override
	public Board getNextBoard(int board_idx, char category) {
		// TODO Auto-generated method stub
		return boardRepository.getNextBoard(board_idx, category);
	}

	@Override
	public long getTotalLikes(int member_idx) {
		// TODO Auto-generated method stub
		String sqlString1 = "SELECT SUM(likes) FROM Board AS b WHERE b.member.idx = :member_idx";
		TypedQuery<Long> boardLikesQuery = em.createQuery(sqlString1, Long.class);
		boardLikesQuery.setParameter("member_idx", member_idx);
		
		String sqlString2 = "SELECT SUM(likes) FROM Comment AS c WHERE c.member.idx = :member_idx";
		TypedQuery<Long> commentLikesQuery = em.createQuery(sqlString2, Long.class);
		commentLikesQuery.setParameter("member_idx", member_idx);
		
		String sqlString3 = "SELECT SUM(likes) FROM Infant_diary AS b WHERE b.member.idx = :member_idx";
		TypedQuery<Long> DiaryLikesQuery = em.createQuery(sqlString1, Long.class);
		DiaryLikesQuery.setParameter("member_idx", member_idx);
		
		String sqlString4 = "SELECT SUM(likes) FROM Id_comment AS c WHERE c.member.idx = :member_idx";
		TypedQuery<Long> DiaryCommentLikesQuery = em.createQuery(sqlString2, Long.class);
		DiaryCommentLikesQuery.setParameter("member_idx", member_idx);
		
		//int result = (int) (boardLikesQuery.getSingleResult() + commentLikesQuery.getSingleResult());
		Long s1 = boardLikesQuery.getSingleResult();
		Long s2 = commentLikesQuery.getSingleResult();
		Long s3 = DiaryLikesQuery.getSingleResult();
		Long s4 = DiaryCommentLikesQuery.getSingleResult();
		
		Long sum = (long) 0;
		if(s1 != null) { sum += s1; }
		if(s2 != null) { sum += s2; }
		if(s3 != null) { sum += s3; }
		if(s4 != null) { sum += s4; }
		
		return sum;
		//return boardLikesQuery.getSingleResult() + commentLikesQuery.getSingleResult() + DiaryLikesQuery.getSingleResult() + DiaryCommentLikesQuery.getSingleResult();
	}

	@Override
	public List<Board_index> setBoardIndex(List<Board_index> list) {
		// TODO Auto-generated method stub
		String regex = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
		for(int i=0 ; i<list.size() ; i++) {
			Board_index index = list.get(i);
			String contents2 = index.getContents().replaceAll(regex, "").replaceAll("\r|\n", "").replaceAll("&nbsp;", " ");
			index.setContents(contents2);
			index.setComment_count(getCommentCount(index.getIdx()));
			List<Board_file> bf = findBf(index.getIdx());
			if(bf != null && bf.size() > 0) {
				index.setAttached_file('t');
			}
			
			try {
				index.setDate_created();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(index.getDate_created().substring(0, 10).equals(memberService.set_now_date())) {
				index.setNew_check(1);
			}
			list.set(i, index);
		}
		return list;
	}
	
	public String parsingHtml(String contents) {
		String regex = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
		String contents2 = contents.replaceAll(regex, "").replaceAll("\r|\n", "").replaceAll("&nbsp;", " ");
		return contents2;
	}

	@Override
	public int updateBf(int idx, String originalName, String serverName, String file_url, int file_size) {
		// TODO Auto-generated method stub
		return bfRepository.updateBf(idx, originalName, serverName, file_url, file_size);
	}

	@Override
	public void deleteByBoard(int board_idx) {
		// TODO Auto-generated method stub
		bfRepository.deleteByBoard(board_idx);
	}

	@Override
	public void deleteCommentByBoard(int board_idx) {
		// TODO Auto-generated method stub
		cRepository.deleteByBoard(board_idx);
	}

	@Override
	public int getCountMemberGallery(int member_idx) {
		// TODO Auto-generated method stub
		return boardRepository.getCountMemberGallery(member_idx);
	}

	@Override
	public int getGalleryCount() {
		// TODO Auto-generated method stub
		return boardRepository.getGalleryCount();
	}

	@Override
	public List<Board_index> getQnaeBoardPagingByLikes(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.category = '1' ORDER BY b.likes DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getQnaBoardPagingByOld(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.category = '1' ORDER BY b.date_created ASC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getQnaBoardPagingByComment(int page_index) {
		// TODO Auto-generated method stub
		//select b.idx, count(c.idx) from board as b left join comment c on b.idx = c.board_idx where b.category = '1' group by b.idx order by count(c.idx) desc, b.date_created desc;
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname,"
				+ " b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board as b LEFT JOIN b.comments as c"
				+ " WHERE b.category = '1'"
				+ " GROUP BY b.idx"
				+ " ORDER BY COUNT(c.idx) DESC, b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getQnaBoardPagingByHits(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m"
				+ " WHERE b.category = '1' ORDER BY b.hits DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getServiceBoardPagingByLikes(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m" 
				+ " WHERE b.category = '2' ORDER BY b.likes DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getServiceBoardPagingByOld(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m" 
				+ " WHERE b.category = '2' ORDER BY b.date_created ASC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getServiceBoardPagingByComment(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname,"
				+ " b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board as b LEFT JOIN b.comments as c"
				+ " WHERE b.category = '2'"
				+ " GROUP BY b.idx"
				+ " ORDER BY COUNT(c.idx) DESC, b.date_created DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public List<Board_index> getServiceBoardPagingByHits(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Board_index(b.idx, b.member.idx, b.subject, b.contents, b.member.nickname, b.member.thumbnail, b.hits, b.likes, DATE_FORMAT(b.date_created, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Board AS b JOIN b.member AS m" 
				+ " WHERE b.category = '2' ORDER BY b.hits DESC";
		TypedQuery<Board_index> query = em.createQuery(sqlString, Board_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환
	}

	@Override
	public int updateContents(int board_idx, String contents) {
		// TODO Auto-generated method stub
		return boardRepository.updateContents(board_idx, contents);
	}
}
