package com.littleone.littleone_web.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.littleone.littleone_web.domain.Alarm;
import com.littleone.littleone_web.domain.Family_group;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.repository.Family_groupRepository;
import com.littleone.littleone_web.repository.GroupRepository;
import com.littleone.littleone_web.repository.Member_groupRepository;
import com.littleone.littleone_web.repository.Member_infantRepository;

@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupRepository gRepository;

	@Autowired
	private Member_groupRepository mgRepository;

	@Autowired
	private Member_infantRepository miRepository;

	@Autowired
	private Family_groupRepository fgRepository;

	@PersistenceContext
	private EntityManager em;

	@Override
	public Family_group insert(Family_group group) {
		// TODO Auto-generated method stub
		return gRepository.save(group);
	}

	@Override
	public Member_group insert_member_group(Member_group group) {
		// TODO Auto-generated method stub
		return mgRepository.save(group);
	}

	@Override
	public Member_group check_authority(int member_idx) {
		// TODO Auto-generated method stub
		return mgRepository.check_authority(member_idx);
	}

	@Override
	public List<Alarm> getListByRecipient(int recipient) {
		// TODO Auto-generated method stub		
		String sqlString = "SELECT a.idx as alarm_idx, a.requester as requester, m.nickname as requester_name,"
				+ " m.thumbnail as requester_thumbnail, a.recipient as recipient, a.request_date as date,"
				+ " a.event_type as event_type, a.read_chk as read_chk, b.board_idx as board_idx, b.comment_idx as comment_idx"
				+ " FROM alert AS a "
				+ "LEFT OUTER JOIN alert_board AS b ON a.idx = b.idx "
				+ "JOIN member AS m ON a.requester = m.idx "
				+ "WHERE a.recipient = :recipient "
				+ "ORDER BY a.request_date desc";
		Query query = em.createNativeQuery(sqlString, "AlertMapping");
		query.setParameter("recipient", recipient);
		List<Alarm> resultList = query.getResultList();
		return resultList;
	}

	@Override
	public Alarm getAlarm(int recipient, int requester) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT new com.littleone.littleone_web.domain.Alarm(g.idx, g.requester, m.name, m.thumbnail, g.recipient, g.request_date) FROM Alert as g, Member m" 
				+ " WHERE g.recipient = :recipient AND g.requester = :requester AND m.idx = g.requester";
		TypedQuery<Alarm> query = em.createQuery(sqlString, Alarm.class);
		query.setParameter("recipient", recipient);
		query.setParameter("requester", requester);
		return query.getSingleResult();		// 1개의 행 반환
	}

	@Override
	public Member_group findMember_group(int member_idx) {
		// TODO Auto-generated method stub
		return mgRepository.findOne(member_idx);
	}

	@Override
	public void invite_family_group_new(int inviter, int recipient) {
		// TODO Auto-generated method stub
		// 가족 그룹 생성(DB insert)
		Family_group fg = new Family_group();
		fg.setName("temp");
		fg = insert(fg);

		// 가족 구성원 추가(DB insert) - 초대자 추가
		Member_group mg = new Member_group();
		mg.setMember_idx(inviter);
		mg.setGroup_idx(fg.getIdx());
		mg.setAuthority('1');
		insert_member_group(mg);

		// 가족 구성원 추가(DB insert) - 신규 가입자 추가
		Member_group mg2 = new Member_group();
		mg2.setMember_idx(recipient);
		mg2.setGroup_idx(fg.getIdx());
		mg2.setAuthority('0');
		insert_member_group(mg2);
	}

	@Override
	public void invite_family_group(int inviter_group_idx, int recipient) {
		// TODO Auto-generated method stub
		// 가족 구성원 추가(DB insert) - 신규 가입자 추가
		Member_group mg2 = new Member_group();
		mg2.setMember_idx(recipient);
		mg2.setGroup_idx(inviter_group_idx);
		mg2.setAuthority('0');
		insert_member_group(mg2);
	}

	@Override
	public boolean same_group_check(int inviter, int recipient) {
		// TODO Auto-generated method stub
		int[] group_list = mgRepository.same_group_check(inviter, recipient);
		if(group_list[0] == group_list[1]) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Member_infant> findByMember_idx(int member_idx) {
		// TODO Auto-generated method stub
		return miRepository.findByMember_idx(member_idx);
	}

	@Override
	public Member_infant insertMI(Member_infant mi) {
		// TODO Auto-generated method stub
		return miRepository.save(mi);
	}

	@Override
	public List<Member_group> findByGroup_idx(int group_idx) {
		// TODO Auto-generated method stub
		return mgRepository.findByGroup_idx(group_idx);
	}

	@Override
	public Family_group findFamily_group(int idx) {
		// TODO Auto-generated method stub
		return fgRepository.findOne(idx);
	}

	@Override
	public Member_group findByGroup_idxAdmin(int group_idx) {
		// TODO Auto-generated method stub
		return mgRepository.findByGroup_idxAdmin(group_idx);
	}

	@Override
	public void deleteGroup(int group_idx) {
		// TODO Auto-generated method stub
		gRepository.delete(group_idx);
	}

	@Override
	public Member_infant findMI(int member_idx, int infant_idx) {
		// TODO Auto-generated method stub
		return miRepository.find(member_idx, infant_idx);
	}

	@Override
	public int findInfantIdx(int group_idx) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT i.infant.idx FROM Member_infant i WHERE i.member.idx = "
				+ "(SELECT g.member_idx FROM Member_group g WHERE g.group_idx = :group_idx AND g.authority = '1')";
		TypedQuery<Integer> query = em.createQuery(sqlString, Integer.class);
		query.setParameter("group_idx", group_idx);
		return query.getSingleResult();		// 1개의 행 반환
	}

	@Override
	public Integer findGroupIdx(int member_idx) {
		// TODO Auto-generated method stub
		String sqlString = "SELECT i.group_idx FROM Member_group i WHERE i.member_idx = :member_idx AND i.authority = '1'";
		TypedQuery<Integer> query = em.createQuery(sqlString, Integer.class);
		query.setParameter("member_idx", member_idx);
		return query.getSingleResult();		// 1개의 행 반환
	}

	@Override
	public int deleteGroupMember(int group_idx, int group_member_idx) {
		// TODO Auto-generated method stub
		Member_group mg = mgRepository.findByMemberAndGroup(group_member_idx, group_idx);
		if(mg == null) {
			return 1;
		}
		mgRepository.delete(group_member_idx, group_idx);
		return 2;
	}

}
