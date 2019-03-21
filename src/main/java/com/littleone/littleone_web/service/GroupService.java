package com.littleone.littleone_web.service;

import java.util.List;

import com.littleone.littleone_web.domain.Alarm;
import com.littleone.littleone_web.domain.Family_group;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;

public interface GroupService {
	Member_group insert_member_group(Member_group group);
	Member_group findMember_group(int member_idx);
	List<Member_infant> findByMember_idx(int member_idx);
	Member_infant insertMI(Member_infant mi);
	List<Alarm> getListByRecipient(int recipient);
	void invite_family_group(int inviter_group_idx, int recipient);
	void invite_family_group_new(int inviter, int recipient);
	Family_group insert(Family_group pgroup);
	Member_group check_authority(int member_idx);
	Alarm getAlarm(int recipient, int requester);
	boolean same_group_check(int inviter, int recipient);
	public List<Member_group> findByGroup_idx(int group_idx);
	public Family_group findFamily_group(int idx);
	public Member_group findByGroup_idxAdmin(int group_idx);
	public void deleteGroup(int group_idx);
	public Member_infant findMI(int member_idx, int infant_idx);
	public int findInfantIdx(int group_idx);
	public Integer findGroupIdx(int member_idx);
	int deleteGroupMember(int group_idx, int group_member_idx);
}
