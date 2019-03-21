package com.littleone.littleone_web.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Comment_check;
import com.littleone.littleone_web.domain.Gallery;
import com.littleone.littleone_web.domain.GalleryLikesInfo;
import com.littleone.littleone_web.domain.Gallery_index;
import com.littleone.littleone_web.domain.Id_comment;
import com.littleone.littleone_web.domain.Id_comment_likes;
import com.littleone.littleone_web.domain.Id_likes;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.repository.GalleryRepository;
import com.littleone.littleone_web.repository.IdCommentLikesRepository;
import com.littleone.littleone_web.repository.IdCommentRepository;
import com.littleone.littleone_web.repository.IdLikesRepository;
import com.littleone.littleone_web.repository.Infant_diaryRepository;

@Service
public class GalleryServiceImpl implements GalleryService {

	@Autowired
	private IdCommentRepository commentRepository;

	@Autowired
	private IdLikesRepository idLikesRepository;

	@Autowired
	private IdCommentLikesRepository idCommentLikesRepository;

	@Autowired
	private Infant_diaryRepository idRepository;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GalleryRepository galleryRepository;

	@PersistenceContext
	private EntityManager em;

	@Override
	public Gallery save(Gallery gallery) {
		// TODO Auto-generated method stub
		return galleryRepository.save(gallery);
	}

	public Id_likes saveLikes(Id_likes likes) {
		return idLikesRepository.save(likes);
	}

	@Override
	public Gallery findOne(int idx) {
		// TODO Auto-generated method stub
		return galleryRepository.findOne(idx);
	}

	@Override
	public int updateHits(int idx) {
		// TODO Auto-generated method stub
		return galleryRepository.updateHits(idx);
	}

	@Override
	public List<Gallery_index> getListIndexPaging(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1' ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public List<Gallery_index> setCommentCount(List<Gallery_index> list, int member_idx) {
		// TODO Auto-generated method stub
		//댓글 갯수, 해쉬태그, 이미지 썸네일 URL로 변환
		for(int i=0 ; i<list.size() ; i++) {
			Gallery_index index = list.get(i);
			index.setComment_count(commentRepository.getCommentCount(index.getDiary_idx()));
			Id_likes likes = findLikes(member_idx, index.getDiary_idx());
			if(likes != null) {
				index.setLikes_check(1);
			} else {
				index.setLikes_check(0);
			}
			//String tag = index.getHashtag();
			/*if(tag != null) {
				index.setHashtag2(tag.split(","));
			}*/
			/*String file_url = index.getFile_url();
			if(file_url != null) {
				String[] origin_url = file_url.split("https://s3.ap-northeast-2.amazonaws.com/littleone/community/gallery/");
				index.setFile_url("https://s3.ap-northeast-2.amazonaws.com/littleone/community/gallery/gallery_thumbnail/" + origin_url[1]);
			}*/
		}
		return list;
	}

	@Override
	public List<Gallery_index> getGalleryByMember_idx(int member_idx, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.member.idx = :member_idx AND i.share = '1' AND f.represent = '1' ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("member_idx", member_idx);
		return query.getResultList();
	}

	/*@Override
	public List<Gallery_index> geGalleryByMember_idx_group(int member_idx) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d %H:%i:%s'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " LEFT JOIN i.id_files as f"
				+ " WHERE i.member.idx = :member_idx AND AND NOT b.public_check = '3' ORDER BY b.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("member_idx", member_idx);
		return query.getResultList();
	}*/

	@Override
	public List<Gallery_index> getGalleryByHashTag(String tag, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1'"
				+ " AND i.hashtag LIKE :tag_pattern1 OR i.hashtag LIKE :tag_pattern2 OR i.hashtag LIKE :tag_pattern3 OR i.hashtag = :tag_pattern4"
				+ " ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("tag_pattern1", "%," + tag + ",%");
		query.setParameter("tag_pattern2", tag + ",%");
		query.setParameter("tag_pattern3", "%," + tag);
		query.setParameter("tag_pattern4", tag);

		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);

		return query.getResultList();		//여러 행 반환 
	}
	
	@Override
	public List<Gallery_index> getListByHashTag(String tag) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1'"
				+ " AND i.hashtag LIKE :tag_pattern1 OR i.hashtag LIKE :tag_pattern2 OR i.hashtag LIKE :tag_pattern3 OR i.hashtag = :tag_pattern4"
				+ " ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("tag_pattern1", "%," + tag + ",%");
		query.setParameter("tag_pattern2", tag + ",%");
		query.setParameter("tag_pattern3", "%," + tag);
		query.setParameter("tag_pattern4", tag);


		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public List<Gallery_index> getListBySubject(String subject) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1'"
				+ " AND i.subject LIKE :pattern"
				+ " ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("pattern", "%" + subject + "%");
		return query.getResultList();
	}
	
	@Override
	public List<Gallery_index> getListByContents(String contents) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1'"
				+ " AND i.contents LIKE :pattern"
				+ " ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("pattern", "%" + contents + "%");
		return query.getResultList();
	}
	
	@Override
	public List<Gallery_index> getListByNickname(String nickname) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1'"
				+ " AND i.member.nickname = :pattern"
				+ " ORDER BY i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		query.setParameter("pattern", nickname);
		return query.getResultList();
	}

	@Override
	public List<Gallery_index> getListByLikes(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1' ORDER BY i.likes DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public List<Gallery_index> getListByDates(String date, int member_idx) {	// yyyy-mm-dd 형식
		// TODO Auto-generated method stub
		// member_idx가 그룹이 없으면 작성자가 member_idx인 글인 조건만 추가
		// member_idx가 그룹이 있으면 조건절에 그룹에 속한 사람이 쓴 글만 보여지도록 조건 추가해야함
		Member_group group = groupService.findMember_group(member_idx);

		String sqlString = null;
		TypedQuery<Gallery_index> query = null;
		if(group == null) {
			// 조건 : 카테고리가 갤러리이고, 공개설정이 비공개가 아니고, 작성자가 member_idx, 작성날짜가 date
			sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
					+ " FROM Infant_diary as i"
					+ " JOIN i.id_files as f"
					+ " WHERE i.share = '1' AND f.represent = '1' AND i.member.idx = :member_idx AND SUBSTRING(i.date_created,1,10) = :date ORDER BY i.date_created DESC";
			query = em.createQuery(sqlString, Gallery_index.class);
			query.setParameter("date", date);
			query.setParameter("member_idx", member_idx);
		} else {
			Member_group group_admin = groupService.findByGroup_idxAdmin(group.getGroup_idx());
			// 조건 : 카테고리가 갤러리이고, 공개설정이 비공개가 아니고, 작성자가 member_idx가 속한 그룹의 그룹장이고, 작성날짜가 date
			sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
					+ " FROM Infant_diary as i"
					+ " JOIN i.id_files as f"
					+ " WHERE i.share = '1' AND f.represent = '1'"
					+ " AND i.member.idx = :group_admin_idx"
					+ " AND SUBSTRING(i.date_created,1,10) = :date ORDER BY i.date_created DESC";
			query = em.createQuery(sqlString, Gallery_index.class);
			query.setParameter("group_admin_idx", group_admin.getMember_idx());
			query.setParameter("date", date);
		}

		return query.getResultList();		//여러 행 반환 
	}

	public Id_likes findLikes(int member_idx, int diary_idx) {
		// TODO Auto-generated method stub
		return idLikesRepository.find(member_idx, diary_idx);
	}

	public void deleteIdLikes(int member_idx, int diary_idx) {
		idLikesRepository.delete(member_idx, diary_idx);
	}

	public int updateComment(int comment_idx, String contents, String date_updated) {
		return commentRepository.updateComment(comment_idx, contents, date_updated);
	}

	public List<Comment_check> getListCommentCheck(int diary_idx) {
		// TODO Auto-generated method stub
		//int idx, int board_idx, int writer_idx, String writer_thumbnail, String writer_nickname,
		//char category, String contents, int likes, String date_created, String date_updated
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Comment_check(c.idx, b.idx, c.member.idx,"
				+ " c.member.thumbnail, c.member.nickname, c.contents, c.likes, "
				+ "DATE_FORMAT(c.date_created, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(c.date_updated, '%Y-%m-%d %H:%i:%s'))"
				+ " FROM Id_comment as c, Infant_diary as b, Member as m" 
				+ " WHERE b.idx = :diary_idx AND c.member.idx = m.idx AND c.diary_idx = b.idx ORDER BY c.date_created DESC";
		TypedQuery<Comment_check> query = em.createQuery(sqlString, Comment_check.class);
		query.setParameter("diary_idx", diary_idx);
		return query.getResultList();		//여러 행 반환
	}

	public Id_comment_likes findCommentLikes(int member_idx, int comment_idx) {
		return idCommentLikesRepository.find(member_idx, comment_idx);
	}

	@Override
	public int getGalleryCountMember(int member_idx) {
		// TODO Auto-generated method stub
		return idRepository.getGalleryCountMember(member_idx);
	}

	@Override
	public List<Gallery_index> getListByOld(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1' ORDER BY i.date_created ASC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public List<Gallery_index> getListByHits(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " WHERE i.share = '1' AND f.represent = '1' ORDER BY i.hits DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public List<Gallery_index> getListByComments(int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Gallery_index(i.idx, i.member.idx, i.subject, i.contents, i.member.nickname, i.member.thumbnail, i.hits, i.likes, DATE_FORMAT(i.date_created, '%Y-%m-%d'), f.file_url, i.hashtag)"
				+ " FROM Infant_diary as i"
				+ " JOIN i.id_files as f"
				+ " LEFT JOIN i.comments as c"
				+ " WHERE i.share = '1' AND f.represent = '1'"
				+ " GROUP BY i.idx"
				+ " ORDER BY COUNT(c.idx) DESC, i.date_created DESC";
		TypedQuery<Gallery_index> query = em.createQuery(sqlString, Gallery_index.class);
		int startPosition = (page_index - 1) * 20;
		int maxResult = 20;
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public int plusCommentLikes(int member_idx, int comment_idx) {
		// TODO Auto-generated method stub
		idCommentLikesRepository.save(new Id_comment_likes(member_idx, comment_idx));
		return commentRepository.updateLikes(comment_idx);
	}

	@Override
	public int minusCommentLikes(int member_idx, int comment_idx) {
		// TODO Auto-generated method stub
		idCommentLikesRepository.delete(member_idx, comment_idx);
		return commentRepository.updateLikesM(comment_idx);
	}

	@Override
	public Id_comment_likes saveCommentLikes(Id_comment_likes likes) {
		// TODO Auto-generated method stub
		return idCommentLikesRepository.save(likes);
	}

	@Override
	public Id_comment findComment(int comment_idx) {
		// TODO Auto-generated method stub
		return commentRepository.findOne(comment_idx);
	}

	@Override
	public String setViewDate(String date) throws ParseException {
		// TODO Auto-generated method stub
		Date date_created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);

		// 날짜가 당일이면 몇분전, 몇시간전, 몇초전으로 변환
		// 날짜가 당일이 아니면 년월일까지만 표기
		long curTime = System.currentTimeMillis();
		long regTime_created = date_created.getTime();

		long diffTime_created = (curTime - regTime_created) / 1000;

		String created = null;

		if(diffTime_created < 60) {
			// sec
			created = diffTime_created + "초전";
		} else if ((diffTime_created /= 60) < 60) {
			// min
			created = diffTime_created + "분전";
		} else if ((diffTime_created /= 60) < 24) {
			// hour
			created = (diffTime_created) + "시간전";
		} else {
			// 당일이 아니므로 년월일까지만 표시
			created = date.substring(0, 10);
		}
		return created;
	}

	@Override
	public List<Id_comment> getCommentListPaging(int gallery_idx, int page_index) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT *"
				+ " FROM id_comment as i"
				+ " WHERE i.diary_idx = :diary_idx ORDER BY i.date_created ASC";
		TypedQuery<Id_comment> query = em.createQuery(sqlString, Id_comment.class);
		int startPosition = (page_index - 1) * 5;
		int maxResult = 5;
		
		query.setParameter("diary_idx", gallery_idx);
		query.setFirstResult(startPosition);
		query.setMaxResults(maxResult);
		return query.getResultList();		//여러 행 반환 
	}

	@Override
	public List<GalleryLikesInfo> findLikesByGallery(int diary_idx) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.GalleryLikesInfo(m.idx, m.thumbnail, m.nickname)"
				+ " FROM Id_likes as i, Member as m"
				+ " WHERE i.member_idx = m.idx AND i.diary_idx = :diary_idx";
		TypedQuery<GalleryLikesInfo> query = em.createQuery(sqlString, GalleryLikesInfo.class);
		query.setParameter("diary_idx", diary_idx);
		return query.getResultList();		//여러 행 반환 
	}

}
