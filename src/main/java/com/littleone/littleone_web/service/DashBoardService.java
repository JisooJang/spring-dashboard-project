package com.littleone.littleone_web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.api_response.DiaryResponse;
import com.littleone.littleone_web.api_response.ScheduleResponse;
import com.littleone.littleone_web.domain.Dashboard;
import com.littleone.littleone_web.domain.Id_comment;
import com.littleone.littleone_web.domain.Id_comment_likes;
import com.littleone.littleone_web.domain.Id_file;
import com.littleone.littleone_web.domain.Infant_diary;
import com.littleone.littleone_web.domain.Infant_schedule;
import com.littleone.littleone_web.domain.Infant_thumbnail;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.repository.DashboardRepository;
import com.littleone.littleone_web.repository.IdCommentLikesRepository;
import com.littleone.littleone_web.repository.IdCommentRepository;
import com.littleone.littleone_web.repository.Id_fileRepository;
import com.littleone.littleone_web.repository.InfantThumbnailRepository;
import com.littleone.littleone_web.repository.Infant_diaryRepository;
import com.littleone.littleone_web.repository.Infant_scheduleRepository;

@Service
public class DashBoardService {

	@Autowired
	private Infant_diaryRepository idRepository;

	@Autowired
	private Id_fileRepository ifRepository;
	
	@Autowired
	private IdCommentRepository commentRepository;
	
	@Autowired
	private IdCommentLikesRepository commentLikesRepository;

	@Autowired
	private Infant_scheduleRepository isRepository;

	@Autowired
	private DashboardRepository dashboardRepository;

	@Autowired
	private InfantThumbnailRepository itRepository;

	@PersistenceContext
	private EntityManager em;

	public Infant_diary save(Infant_diary diary) {
		return idRepository.save(diary);
	}

	public Infant_diary findDiary(int idx) {
		return idRepository.findOne(idx);
	}

	public Infant_schedule findSchedule(int idx) {
		return isRepository.findOne(idx);
	}

	public List<Infant_diary> findByInfant_idxTotal(int infant_idx) {
		return idRepository.findByInfant_idx(infant_idx);
	}

	public List<Infant_diary> findByInfantPublic(int infant_idx) {
		return idRepository.findByInfantPublic(infant_idx);
	}

	public Id_file findIdFile(int idx) {
		return ifRepository.findOne(idx);
	}
	public Id_file saveIdFile(Id_file file) {
		return ifRepository.save(file);
	}

	public Infant_schedule saveIdSchedule(Infant_schedule schedule) {
		return isRepository.save(schedule);
	}

	public List<Infant_schedule> findByDate(int infant_idx, String date) {
		return isRepository.findByDate(infant_idx, date);
	}

	public List<Infant_diary> findByDateDiary(int infant_idx, String date) {
		return idRepository.findByDateDashboard(infant_idx, date);
	}

	public List<Infant_diary> findByDateDiary_byGroup(int infant_idx, List<Member> group_members, String date) {
		List<Integer> members = new ArrayList<Integer>(); 
		for(int i=0 ; i<group_members.size() ; i++) {
			members.add(group_members.get(i).getIdx());
		}
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Infant_diary(i.idx, i.subject, DATE_FORMAT(i.date_created, '%H:%i'))"
				+ " FROM Infant_diary i"
				+ " LEFT JOIN i.id_files f"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date,1,10) = :date"
				+ " AND i.member.idx IN (:members)"
				+ " ORDER BY i.date_created DESC";

		TypedQuery<Infant_diary> query = em.createQuery(sqlString, Infant_diary.class);
		query.setParameter("infant_idx", infant_idx);
		query.setParameter("date", date);
		query.setParameter("members", members);
		return query.getResultList();		//여러 행 반환 
	}

	public List<Infant_diary> findByDatePrivate(int infant_idx, int member_idx, String date) {
		return idRepository.findByDatePrivate(infant_idx, member_idx, date);
	}

	public List<Infant_schedule> findByDateSchedule(int infant_idx, String date1, String date2) {
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Infant_schedule(i.idx, i.infant, i.member, "
				+ "DATE_FORMAT(i.event_date_start, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(i.event_date_end, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(i.date_created, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(i.date_updated, '%Y-%m-%d %H:%i:%s'), i.title, i.event_type)"
				+ " FROM Infant_schedule i"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date_start,1,7) = :date1"
				+ " AND SUBSTRING(i.event_date_end,1,7) = :date1"
				+ " AND SUBSTRING(i.event_date_start, 9, 2) <= :date2"
				+ " AND SUBSTRING(i.event_date_end, 9, 2) >= :date2"
				+ " ORDER BY i.date_created DESC";

		TypedQuery<Infant_schedule> query = em.createQuery(sqlString, Infant_schedule.class);
		query.setParameter("infant_idx", infant_idx);
		query.setParameter("date1", date1);
		query.setParameter("date2", date2);
		return query.getResultList();		//여러 행 반환 
	}

	public List<Infant_schedule> findByDateSchedule_byGroup(int infant_idx, List<Member> group_members, String date) {
		List<Integer> members = new ArrayList<Integer>(); 
		for(int i=0 ; i<group_members.size() ; i++) {
			members.add(group_members.get(i).getIdx());
		}
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Infant_schedule(i.idx, i.infant, i.member, "
				+ "DATE_FORMAT(i.event_date_start, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(i.event_date_end, '%Y-%m-%d %H:%i:%s'),"
				+ " DATE_FORMAT(i.date_created, '%Y-%m-%d %H:%i:%s'), DATE_FORMAT(i.date_updated, '%Y-%m-%d %H:%i:%s'),"
				+ " i.title, i.event_type)"
				+ " FROM Infant_schedule i"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date_start,1,10) = :date"
				+ " AND i.member.idx IN (:members)"
				+ " ORDER BY i.date_created DESC";

		TypedQuery<Infant_schedule> query = em.createQuery(sqlString, Infant_schedule.class);
		query.setParameter("infant_idx", infant_idx);
		query.setParameter("date", date);
		query.setParameter("members", members);
		return query.getResultList();		//여러 행 반환 
	}

	public void delete_schedule(int schedule_idx) {
		isRepository.delete(schedule_idx);
	}

	public void delete_diary(int diary_idx) {
		idRepository.delete(diary_idx);
	}

	public int updateSchedule(int schedule_idx, String date_start, String date_end, String title) {
		return isRepository.update(schedule_idx, date_start, date_end, title);	
	}

	public List<Infant_diary> findByInfantAndMember(int infant_idx, int member_idx) {
		return idRepository.findByInfantAndMember(infant_idx, member_idx);
	}

	public List<Infant_schedule> findByInfantAndMemberIS(int infant_idx, int member_idx) {
		return isRepository.findByInfantAndMember(infant_idx, member_idx);
	}

	public String checkDiary(int infant_idx) {
		return idRepository.checkDiary(infant_idx);
	}

	public int checkSchedule(int infant_idx, int member_idx) {
		return isRepository.checkSchedule(infant_idx, member_idx);
	}

	public int updateDiary(int idx, String subject, String contents, String date_updated, String hashtag, char share) {
		return idRepository.update(idx, subject, contents, date_updated, hashtag, share);
	}
	
	public int updateDiaryAtGallery(int idx, String subject, String contents, String date_updated) {
		return idRepository.updateGallery(idx, subject, contents, date_updated);
	}
	
	public int updateDiaryAtGallery2(int idx, String subject, String contents, String date_updated, String hashtag) {
		return idRepository.updateGallery2(idx, subject, contents, date_updated, hashtag);
	}

	public void deleteIdFile(int idx) {
		ifRepository.delete(idx);
	}
	
	public void deleteIdFileByDiary(int diary_idx) {
		ifRepository.deleteIdFileByDiary(diary_idx);
	}
	
	public void deleteCommentsByDiary(int diary_idx) {
		commentRepository.deleteByDiary(diary_idx);
	}
	
	public Id_comment saveComment(Id_comment comment) {
		return commentRepository.save(comment);
	}
	
	public List<Id_comment> findCommentsByDiary(int diary_idx) {
		return commentRepository.findCommentsByDiary(diary_idx);
	}
	
	public Id_comment findComment(int idx) {
		return commentRepository.findOne(idx);
	}
	
	public void deleteComment(int idx) {
		commentRepository.delete(idx);
	}
	
	public int countComment(int member_idx) {
		return commentRepository.countComment(member_idx);
	}
	
	public long getTotalLikes(int member_idx) {
		// TODO Auto-generated method stub
		String sqlString1 = "SELECT COUNT(*) FROM Id_likes AS l, Infant_diary AS b WHERE l.d_idx = b.idx and b.member.idx = :member_idx";
		TypedQuery<Long> boardLikesQuery = em.createQuery(sqlString1, Long.class);
		boardLikesQuery.setParameter("member_idx", member_idx);
		
		String sqlString2 = "SELECT COUNT(*) FROM comment_likes AS l, Id_comment AS c WHERE l.comment_idx = c.idx and c.member.idx = :member_idx";
		TypedQuery<Long> commentLikesQuery = em.createQuery(sqlString2, Long.class);
		commentLikesQuery.setParameter("member_idx", member_idx);
		
		//int result = (int) (boardLikesQuery.getSingleResult() + commentLikesQuery.getSingleResult());
		return boardLikesQuery.getSingleResult() + commentLikesQuery.getSingleResult();
	}

	public List<ScheduleResponse> checkByDateScheduleByGroup(int infant_idx, List<Member> group_members, String date) {
		List<Integer> members = new ArrayList<Integer>(); 
		for(int i=0 ; i<group_members.size() ; i++) {
			members.add(group_members.get(i).getIdx());
		}
		/*String sqlString = "SELECT DISTINCT SUBSTRING(i.event_date_start, 9, 2), DISTINCT SUBSTRING(i.event_date_end, 9, 2), i.event_type"
				+ " FROM Infant_schedule i"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date_start,1,7) = :date"
				+ " AND i.member.idx IN (:members)"
				+ " ORDER BY i.event_date DESC";*/
		
		String sqlString = "SELECT DISTINCT new com.littleone.littleone_web.api_response.ScheduleResponse"
				+ "(SUBSTRING(i.event_date_start, 9, 2), SUBSTRING(i.event_date_end, 9, 2), i.event_type)"
				+ " FROM Infant_schedule i"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date_start,1,7) = :date"
				+ " AND i.member.idx IN (:members)"
				+ " ORDER BY i.event_date_start ASC, i.event_type ASC";

		TypedQuery<ScheduleResponse> query = em.createQuery(sqlString, ScheduleResponse.class);
		query.setParameter("infant_idx", infant_idx);
		query.setParameter("date", date);
		query.setParameter("members", members);
		return query.getResultList();		//여러 행 반환 
	}

	public List<ScheduleResponse> checkByDateSchedule(int infant_idx, int member_idx, String date) {
		//return isRepository.checkByDate(infant_idx, member_idx, date);
		
		String sqlString = "SELECT DISTINCT new com.littleone.littleone_web.api_response.ScheduleResponse"
				+ "(SUBSTRING(i.event_date_start, 9, 2), SUBSTRING(i.event_date_end, 9, 2), i.event_type)"
				+ " FROM Infant_schedule i"
				+ " WHERE i.infant.idx = :infant_idx"
				//+ " AND i.member.idx = :member_idx"
				+ " AND SUBSTRING(i.event_date_start,1,7) = :date"
				+ " ORDER BY i.event_date_start ASC";

		TypedQuery<ScheduleResponse> query = em.createQuery(sqlString, ScheduleResponse.class);
		query.setParameter("infant_idx", infant_idx);
		//query.setParameter("member_idx", member_idx);
		query.setParameter("date", date);
		
		return query.getResultList();		//여러 행 반환 
	}

	public List<DiaryResponse> checkByDateDiaryByGroup(int infant_idx, List<Member> group_members, String date) {
		List<Integer> members = new ArrayList<Integer>(); 
		for(int i=0 ; i<group_members.size() ; i++) {
			members.add(group_members.get(i).getIdx());
		}
		String sqlString = "SELECT DISTINCT new com.littleone.littleone_web.api_response.DiaryResponse"
				+ "(SUBSTRING(i.event_date, 9, 2))"
				+ " FROM Infant_diary i"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date,1,7) = :date"
				+ " AND i.member.idx IN (:members)"
				+ " ORDER BY i.event_date";

		TypedQuery<DiaryResponse> query = em.createQuery(sqlString, DiaryResponse.class);
		query.setParameter("infant_idx", infant_idx);
		query.setParameter("date", date);
		query.setParameter("members", members);
		return query.getResultList();		//여러 행 반환 
	}

	public List<DiaryResponse> checkByDateDiary(int infant_idx, int member_idx, String date) {
		//return isRepository.checkByDate(infant_idx, member_idx, date);
		String sqlString = "SELECT DISTINCT new com.littleone.littleone_web.api_response.DiaryResponse"
				+ "(SUBSTRING(i.event_date, 9, 2))"
				+ " FROM Infant_diary i"
				+ " WHERE i.infant.idx = :infant_idx"
				+ " AND SUBSTRING(i.event_date,1,7) = :date"
				//+ " AND i.member.idx = :member_idx"
				+ " ORDER BY i.event_date";

		TypedQuery<DiaryResponse> query = em.createQuery(sqlString, DiaryResponse.class);
		query.setParameter("infant_idx", infant_idx);
		query.setParameter("date", date);
		//query.setParameter("member_idx", member_idx);
		return query.getResultList();		//여러 행 반환 
	}

	public Dashboard saveDashboard(Dashboard dashboard) {
		return dashboardRepository.save(dashboard);
	}

	public Dashboard findDashboard(int member_idx) {
		return dashboardRepository.findOne(member_idx);
	}

	public int updateDashboard(int member_idx, String temp, String peepee, String bottle,/* String diary,*/ String infant_info, String group_info) {
		return dashboardRepository.update(member_idx, temp, peepee, bottle, /*diary,*/ infant_info, group_info);
	}

	public Infant_thumbnail findInfantThumbnail(int infant_idx) {
		return itRepository.findOne(infant_idx);
	}

	public Infant_thumbnail saveInfantThumbnail(Infant_thumbnail thumbnail) {
		return itRepository.save(thumbnail);
	}
	
	public List<Id_file> findIdFileByDiary_idx(int diary_idx) {
		return ifRepository.findIdFileByDiary_idx(diary_idx);
	}
	
	public int updateHits(int diary_idx) {
		return idRepository.updateHits(diary_idx);
	}
	
	public int update_diary(int diary_idx, String subject, String contents, String date_updated) {
		// TODO Auto-generated method stub
		return idRepository.updateGallery(diary_idx, subject, contents, date_updated);
	}
	
	public int update_diary2(int diary_idx, String subject, String contents, String hashtag, String date_updated) {
		// TODO Auto-generated method stub
		return idRepository.updateGallery2(diary_idx, subject, contents, hashtag, date_updated);
	}
	
	public int updateLikes(int diary_idx) {
		return idRepository.updateLikes(diary_idx);
	}
	
	public int updateLikesM(int diary_idx) {
		return idRepository.updateLikesM(diary_idx);
	}
	
	public Id_comment_likes findCommentLikes(int member_idx, int comment_idx) {
		return commentLikesRepository.find(member_idx, comment_idx);
	}
	
	public void deleteFileByDiary_idx(int diary_idx) {
		ifRepository.deleteIdFileByDiary(diary_idx);
	}
	
	public Set<Integer> getScheduleDay(int infant_idx, int member_idx, String date) {
		return null;
	}
	
	public Set<Integer> getDiaryDay(int infant_idx, int member_idx, String date) {
		return null;
	}
}
