package com.littleone.littleone_web.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Family_group;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.domain.SearchMember;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.InfantService;
import com.littleone.littleone_web.service.MemberService;

@Controller
public class GroupController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private AlarmController alarmController;

	@Autowired
	private InfantService infantService;

	/*@ResponseBody
	@GetMapping("/searchMember/{email_pattern}")
	public List<SearchMember> searchMemberByPattern(@PathVariable("email_pattern") String email_pattern, HttpSession session) {	// 아이디 검색 ajax
		// 클라이언트에서 필요한값 : 회원 이메일, 닉네임, 썸네일, 회원 idx, 이미 그룹신청을 한 상태인지 여부
		if(session.getAttribute("session_email") != null) {
			String session_email = (String) session.getAttribute("session_email");
			List<SearchMember> list = memberService.searchMember(email_pattern, session_email);
			for(int i=0 ; i<list.size() ; i++) {
				if(groupService.duplicate_check((int)session.getAttribute("idx"), list.get(i).getSearch_idx()) != null) {
					list.get(i).setGroup_request_check(1);
				}
			}
			return list;
		} else {
			return null;
		}	
	}*/

	/*@ResponseBody
	@GetMapping("/searchMember/{phone_number}")
	public List<SearchMember> searchMemberByPhone(@PathVariable("phone_number") String phone_number, HttpSession session) {	// 아이디 검색 ajax
		// 클라이언트에서 필요한값 : 회원 이메일, 닉네임, 썸네일, 회원 idx
		if(session.getAttribute("idx") != null) {
			int idx = (int) session.getAttribute("idx");
			List<SearchMember> list = memberService.searchMemberByPhone(phone_number, idx);
			for(int i=0 ; i<list.size() ; i++) {
				if(alertService.duplicate_check((int)session.getAttribute("idx"), list.get(i).getSearch_idx()) != null) {
					list.get(i).setGroup_request_check(1);
				}
			}
			return list;
		} else {
			return null;
		}
	}*/

	@ResponseBody
	@GetMapping("/searchMember/{nickname}")
	public List<SearchMember> searchMemberByPhone(@PathVariable("nickname") String nickname, HttpSession session) {	// 아이디 검색 ajax
		// 클라이언트에서 필요한값 : 회원 이메일, 닉네임, 썸네일, 회원 idx
		if(session.getAttribute("idx") != null) {
			int idx = (int) session.getAttribute("idx");
			List<SearchMember> list = memberService.searchMemberByNickname(nickname, idx);
			for(int i=0 ; i<list.size() ; i++) {
				if(alertService.duplicate_check((int)session.getAttribute("idx"), list.get(i).getSearch_idx()) != null) {
					list.get(i).setGroup_request_check(1);
				}
			}
			return list;
		} else {
			return null;
		}
	}

	@ResponseBody
	@GetMapping("/request_group/{search_idx}")
	public String requestGroup(@PathVariable("search_idx") String search_idx, HttpSession session) {	// 그룹 신청 ajax. 그룹이 양쪽 모두 이미 존재할경우 신청 불가. 수신자가 그룹이 존재하면 안됨.
		if(search_idx != null) {
			Member m = memberService.findByIdx(Integer.parseInt(search_idx));
			
			if(m == null) {
				return "no_member";
			}
			
			Integer infant_idx = (Integer) session.getAttribute("infant_idx");
			if(infant_idx == null) {
				return "no_infant";
			}
			
			int idx = (int) session.getAttribute("idx");
			List<Member_infant> mi = infantService.findByMember(idx);
			if(mi == null || mi.size() == 0) {
				return "infant";		// 아기 정보를 등록해야 그룹신청을 할 수 있음
			}
			
			mi = infantService.findByMember(Integer.parseInt(search_idx));
			if(mi != null && mi.size() > 0) {
				return "have_infant";
			}

			Member_group group = groupService.findMember_group(idx);
			if(group != null) {
				if(groupService.findByGroup_idx(group.getGroup_idx()).size() >= 5) {
					return "fulled";
				}
			}

			Member_group group2 = groupService.findMember_group(Integer.parseInt(search_idx));
			if(group2 != null) {
				return "receiver_failed";
			}

			if(alertService.duplicate_check(idx, Integer.parseInt(search_idx)) == null) {
				Alert alert = new Alert();
				//Family_group group = new Family_group();
				//group.setName("temp");
				//group = groupService.insert(group);
				//group_alert.setGroup_idx(group.getIdx());
				alert.setRequester(idx);
				alert.setRecipient(Integer.parseInt(search_idx));
				alert.setEvent_type('1');		// 1번은 그룹요청이벤트
				alert.setRequest_date(memberService.set_now_time());
				alert.setRead_chk('f');
				alert = alertService.insert(alert);
				alertService.insert(alert);	// DB insert

				if(alert != null) {
					// index_header 소켓 알림
					alarmController.set_group_alert(alert);
					return "success";
				} else {
					return "failed";
				}
			} else {
				return "duplicated";
			}

		} else {
			return "search_idx_null";
		}	
	}

	@ResponseBody
	@GetMapping("/accept_group/{alert_idx}")
	public String acceptGroup(@PathVariable("alert_idx") String alert_idx, HttpSession session) {
		// 1. 그룹 수락시 group_alert 테이블 행 삭제
		// 2. family_group 테이블에 행 추가 idx(auto), name
		// 3. member_group 테이블에 requester, family_group의 idx, authority = 1 행 추가
		// 4. member_group 테이블에 recipient, family_group의 idx, authority = 0 행 추가

		if(session.getAttribute("idx") != null && alert_idx != null) {
			//int requester = (int) session.getAttribute("idx");
			int requester = alertService.findRequester(Integer.parseInt(alert_idx));
			int recipient = (int) session.getAttribute("idx");
			
			Member_group requester_g = groupService.findMember_group(requester);
			if(requester_g != null) {
				// 요청자의 그룹이 이미 존재하면, 수신자를 요청자의 그룹의 그릅원으로 추가
				
				Member_group mg_recipient = new Member_group(recipient, requester_g.getGroup_idx(), '0');
				groupService.insert_member_group(mg_recipient);
			} else {
				// 요청자가 그룹이 없으면 새 그룹 생성
				Family_group group = new Family_group();
				group.setName("temp");
				group = groupService.insert(group);
				
				Member_group mg_request = new Member_group(requester, group.getIdx(), '1');
				Member_group mg_recipient = new Member_group(recipient, group.getIdx(), '0');
				groupService.insert_member_group(mg_request);
				groupService.insert_member_group(mg_recipient);
			}
			
			alertService.deleteAlert(Integer.parseInt(alert_idx));	// group_alert 행 삭제
			
			Alert alert1 = new Alert();
			alert1.setRecipient(recipient);
			alert1.setRequester(requester);		
			alert1.setRequest_date(memberService.set_now_time());
			alert1.setEvent_type('a');	// 그룹 처리 결과 (수락)
			alert1.setRead_chk('f');
			
			alertService.insert(alert1);
			// 그룹 수락자에게 그룹 처리 여부 알림 행 저장
			
			
			
			Alert alert2 = new Alert();
			alert2.setRecipient(requester);
			alert2.setRequester(recipient);
			alert2.setRequest_date(memberService.set_now_time());
			alert2.setEvent_type('0');	// 그룹 수락
			alert2.setRead_chk('f');
			
			alertService.insert(alert2);
			// 그룹 요청자에게 수락 알림 행 저장
			return "success";

		} else {
			return "failed";
		}
	}

	@ResponseBody
	@GetMapping("/cancel_request/{idx}")
	public String cancelRequestByRecipient(@PathVariable("idx") int idx, HttpSession session) {
		Alert alarm = alertService.findAlert(idx);
		
		int member_idx = (int) session.getAttribute("idx");
		if(alarm != null) {
			int requester = alarm.getRequester();
			alertService.deleteAlert(idx);
			
			Alert alert1 = new Alert();
			alert1.setRecipient(member_idx);
			alert1.setRequester(requester);		// admin
			alert1.setRequest_date(memberService.set_now_time());
			alert1.setEvent_type('b');	// 그룹 처리 결과 (거절)
			alert1.setRead_chk('f');
			
			alertService.insert(alert1);
			return "success";
		} else {
			return "failed";
		}
	}

	@ResponseBody
	@GetMapping("/cancel_group_request/{requester}/{recipient}")
	public String cancelRequest(@PathVariable("requester") int requester, @PathVariable("recipient") int recipient) {
		int idx = alertService.findGroupAlertIdx(requester, recipient);
		try {
			alertService.deleteAlert(idx);
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
		return "success";
	}
	
	// 그룹 삭제
	@ResponseBody
	@GetMapping("/delete_group/{group_idx}")
	public String delete_group(@PathVariable("group_idx") int group_idx, HttpSession session) {
		// 현재 회원이 해당 그룹의 그룹장인지 체크
		int member_idx = (int) session.getAttribute("member_idx");
		Member_group group = groupService.findMember_group(member_idx);	
		if(group == null || group.getAuthority() != '1') {
			return "group_auth_failed";
		}
		
		groupService.deleteGroup(group_idx);
		return "success";
	}
	
	
	// 아래 두 요청에서, 그룹원 삭제후 그룸원 사이즈가 1이면 그룹을 삭제시킨다.
	
	// 그룹원 삭제 
	@ResponseBody
	@GetMapping("/delete_group_member/{group_member_idx}")
	public String delete_group_member(@PathVariable("group_member_idx") int group_member_idx, HttpSession session) {
		// 현재 회원이 해당 그룹의 그룹장인지 체크
		int member_idx = (int) session.getAttribute("idx");
		int group_idx = (int) session.getAttribute("group_idx");
		Member_group group = groupService.findMember_group(member_idx);	
		if(group == null || group.getAuthority() != '1') {
			return "not_group_leader";
		}
		
		int result_code = groupService.deleteGroupMember(group_idx, group_member_idx);
		if(result_code == 1) {
			return "group_auth_failed";
		}
		
		List<Member_group> mg = groupService.findByGroup_idx(group_idx);
		
		if(mg.size() == 1 && mg.get(0).getMember_idx() == member_idx) {
			// 그룹에 그룹장 혼자 남아있는 상황이므로 그룹 삭제.
			groupService.deleteGroup(group_idx);
			session.removeAttribute("group_idx");
			return "delete_group";
		}
		
		return "success";
	}
	
	
	// 그룹 탈퇴
	@ResponseBody
	@GetMapping("/delete_group_self")
	public String delete_group_member(HttpSession session) {
		// 현재 회원이 해당 그룹의 그룹장인지 체크
		// 그룹장의 그룹 탈퇴는 그룹 전체가 삭제됨
		int member_idx = (int) session.getAttribute("idx");
		Integer group_idx = (Integer) session.getAttribute("group_idx");
		Member_group group = groupService.findMember_group(member_idx);	
		if(group_idx == null) {
			return "no_group";
		}
		
		if(group == null) {
			return "not_this_group";
		}
		
		if(group.getAuthority() == '1') {
			// 그룹장일 경우 그룹 삭제. member_group 연관 행들도 cascade 속성으로 인해 삭제됨.
			groupService.deleteGroup(group_idx);
			session.removeAttribute("group_idx");
			return "admin_leave";
		} else {
			// 그룹원일 경우 그룸원만 탈퇴
			int result_code = groupService.deleteGroupMember(group_idx, member_idx);
			if(result_code == 1) {
				return "group_auth_failed";
			}
			
			List<Member_group> mg = groupService.findByGroup_idx(group_idx);
			if(mg.size() == 1 && mg.get(0).getMember_idx() == member_idx) {
				// 그룹에 그룹장 혼자 남아있는 상황이므로 그룹 삭제.
				groupService.deleteGroup(group_idx);
				session.removeAttribute("group_idx");
				return "delete_group";
			}
			
			session.removeAttribute("group_idx");
			return "member_leave";
		}

	}
}
