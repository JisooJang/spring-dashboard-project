package com.littleone.littleone_web.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.littleone.littleone_web.domain.GroupInfo;
import com.littleone.littleone_web.domain.Infant;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_infant;
import com.littleone.littleone_web.service.DynamoDBService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.InfantService;
import com.littleone.littleone_web.service.MemberService;

public class DashBoardInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private InfantService infantService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private GroupService groupService;
	
	@Autowired
	private DynamoDBService dbService;

	//private Map<Integer, GroupInfo> groupInfoBySession = DashBoardController.getGroupInfoBySession();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		// 컨트롤러 실행 직전에 동작
		// 반환 값이 true일 경우 정상적으로 컨트롤러 코드 진행, false일 경우 컨트롤러 진입 x
		HttpSession session = request.getSession();
		String selected_date = (String) request.getAttribute("selected_date");
		setGroupInfoBySession(session);		// 세션에 대쉬보드 셋팅정보 저장
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		// 컨트롤러 진입 후 view가 랜더링 되기 전 수행
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void setGroupInfoBySession(HttpSession session) throws ParseException {
		// 아이가 있지만 그룹이 없을때, 아이가없지만 그룹이 없을때, 아이도없고 그룹도 없을때 케이스 고려
		// 그룹정보나 아기정보 db가 삭제되거나 업데이트될때마다 사용자별로 초기화되어야함.
		
		Integer member_idx = (Integer) session.getAttribute("idx");
		GroupInfo info = null;
		List<Member_infant> mi = groupService.findByMember_idx(member_idx);		// 아이정보가 있는지 구분값

		
		GroupInfo static_info = (GroupInfo) session.getAttribute("dashboard_info");
		
		if(static_info == null) {	// static map변수에 기존 대쉬보드 정보가 없으면
			if (mi != null && mi.size() > 0) { 
				info = new GroupInfo(); 
			}
		} else {
			// static map변수가 이미 세팅되어있으면
			if (mi != null && mi.size() > 0) {
				info = static_info; 
			} else {
				info = new GroupInfo();
			}
		}
		// 1. 아이정보가 있을경우  info 변수에 값 셋팅. 아이정보가 없으면 info 셋팅 X
		
		
		List<Infant> infant_list = null;
		if (mi != null && mi.size() > 0) {	// 등록된 아이가 존재할경우
			infant_list = new ArrayList<Infant>();
			for (int i = 0; i < mi.size(); i++) {
				infant_list.add(mi.get(i).getInfant());
			}
			//info.setInfant_list(infant_list);
			info.setShared_infant(infant_list.get(0));
			
		}
		// 2. 아이 리스트 셋팅

		
		/*if((mi == null || mi.size() == 0) && infant_idx != null) {	// 등록된 아이는 없지만 그룹으로 인해 세션에 infant_idx 정보가 있는경우
			info.setShared_infant(infantService.findOne(infant_idx));
		}*/
		
		// 1. infant_list, shared_infant setting
		
		
		// 2. 그룹정보 셋팅 (대쉬보드 날짜 이동할때마다 그룹정보를 로딩하므로 groupInfoBySession에 그룹정보가 초기화되있지 않으면 조건 추가 할지 고려해볼것)
		Member_group g = groupService.findMember_group(member_idx);
		
		if(g != null) {	// 멤버가 그룹에 속하면 (아이는 없지만 그룹에만 속할 경우도 있음)
			if(info == null) { 
				if(static_info != null) {
					info = static_info;
				} else {
					info = new GroupInfo(); 
				}	
			}
			List<Member_group> mg_list = groupService.findByGroup_idx(g.getGroup_idx());

			if (mg_list != null && mg_list.size() >= 1) {	// 그룹이 있는경우
				// group_members, group_admin, group_admin_idx, shared_infant 초기화
				
				session.setAttribute("group_idx", g.getGroup_idx());
				
				info.setGroup_idx(g.getGroup_idx());
				ArrayList<Member> group_members = null;
				
				//info.setShared_infant(infantService.findOne(groupService.findInfantIdx(g.getGroup_idx())));
				
				group_members = new ArrayList<Member>();
				for (int i = 0; i < mg_list.size(); i++) {
					Member_group mg = mg_list.get(i);
					Member member = memberService.findByIdx(mg.getMember_idx());
					group_members.add(member);
					if (mg.getAuthority() == '1') {	// 그룹 관리자는 1명만 있음
						int group_admin_idx = member.getIdx();
						info.setGroup_admin_idx(group_admin_idx);	// 그룹장 idx setting
						info.setGroup_admin(member.getName());		// 그룹장 name setting
						
						List<Member_infant> mi2 = groupService.findByMember_idx(group_admin_idx);
						info.setShared_infant(mi2.get(0).getInfant());
					}
				} // end for	
				info.setGroup_members(group_members);	// group members setting
			}
		} else {
			session.removeAttribute("group_idx");
		}	// set group info, shared_infant
		
		
		
		/*// 3. 대쉬보드에 셋팅된 날짜의 수유횟수 및 시간, 배변횟수 및 시간, 셋팅 날짜의 최근체온 및 최근시간 설정하기
		if(info != null && info.getShared_infant() != null) {
			String infant_idx = Integer.toString(info.getShared_infant().getIdx());
			String date, parse_date;
			if(info.getDate() != null) {
				date = info.getDate();
				parse_date = date.substring(0, 4) + date.substring(5,7) + date.substring(8);

			} else {
				SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd");
				parse_date = date_format.format(new Date());
			}
			
			int feed_cnt = dbService.readBottleCountByDate(infant_idx, parse_date);
			info.setFeed_cnt(feed_cnt);
			
			int defec_cnt = dbService.readPeepeeCountByDate(infant_idx, parse_date);
			info.setDefec_cnt(defec_cnt);
			
			if(feed_cnt > 0) {
				String bottle_time = dbService.getRecentBottle(Integer.parseInt(infant_idx), parse_date);
				if(bottle_time != null) {
					info.setBottle_time(bottle_time.substring(8, 10) + ":" + bottle_time.substring(10, 12) + ":" + bottle_time.substring(12));
				} 
			} else {
				info.setBottle_time(null);
			}
			
			if(defec_cnt > 0) {
				String peepee_time = dbService.getRecentPeepee(Integer.parseInt(infant_idx), parse_date);
				if(peepee_time != null) {
					info.setPeepee_time(peepee_time.substring(8, 10) + ":" + peepee_time.substring(10, 12) + ":" + peepee_time.substring(12));
				}
			} else {
				info.setPeepee_time(null);
			}
			
			String RecentTempAndDate = dbService.getRecentTemp(Integer.parseInt(infant_idx), parse_date);
			if(RecentTempAndDate.split("-")[0].equals("null") == false) {
				info.setLast_temp(RecentTempAndDate.split("-")[0]);
				String temp_time = RecentTempAndDate.split("-")[1];
				info.setTemp_time(temp_time.substring(8, 10) + ":" + temp_time.substring(10, 12) + ":" + temp_time.substring(12));
			} else {
				info.setLast_temp(null);
				info.setTemp_time(null);
			}
			
		}
*/
		if(info != null) {
			session.setAttribute("dashboard_info", info);
		}
	}	
}
