package com.littleone.littleone_web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.littleone.littleone_web.domain.Alarm;
import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Bottle;
import com.littleone.littleone_web.domain.Peepee;
import com.littleone.littleone_web.domain.Temp;
import com.littleone.littleone_web.domain.WriteTempData;
import com.littleone.littleone_web.service.AlertService;
import com.littleone.littleone_web.service.AppService;
import com.littleone.littleone_web.service.DeviceService;
import com.littleone.littleone_web.service.DynamoDBService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MemberService;

@Controller
public class DynamoDBController {
	@Autowired
	private DynamoDBService service;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private AlertService alertService;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private DeviceService deviceService;

	@ResponseBody
	@GetMapping("/dynamodb/read_test/{idx}/{range_key}")
	public String test(@PathVariable("idx") String idx, String range_key) {
		service.readTemp(idx, range_key);
		return "success";
	}

	// 특정 날짜별로 가져오기
	@ResponseBody
	@GetMapping("/dynamodb/readByDate/{infant_idx}/{date}")
	public String testDate(@PathVariable("infant_idx") String infant_idx, @PathVariable("date") String date) {
		service.readTempByDate(infant_idx, date);
		return "success";
	}

	@ResponseBody
	@GetMapping("/dynamodb/readUpdated/{infant_idx}")
	public String testUpdated(@PathVariable("infant_idx") String infant_idx, @PathVariable("date") String date) {
		service.readupdated(infant_idx);
		return "success";
	}

	@ResponseBody
	@GetMapping("/dynamodb/table_info")
	public String test2() {
		return service.tableInfo();
	}

	// 회원 1명당 7초당 1회씩 수행되는 코드 -> 회원 500명당 7초당 500회씩 수행. 하루에 4320만행 insert 수행. DB 접근 부하 고려 필요 (인증코드를 뺄지, 클라이언트에서 더 긴 시간 간격으로 인증 할지 검토 필요)
	@ResponseBody
	@PostMapping("/app/dynamodb/write_temp")
	public String temp_write(@RequestParam("infant_idx") int infant_idx, @RequestParam("member_idx") int member_idx, @RequestParam(value="memo", required=false) String memo,
			@RequestParam("temperature") float temperature, @RequestParam("serial_num") String serial_num, HttpServletRequest request) {	
		/*if(appService.check_auth_app(request) == false) {
			System.out.println("인증실패");
			return "auth_failed";
		}
		
		// 디바이스넘버랑 회원정보랑 바른 쌍인지 비교. 디바이스넘버와 인증토큰이 변조되지 않았는지 체크하려면 db 접근 필요
		if(deviceService.findBySerialNumAndType(member_idx, serial_num, '1') == null) {
			System.out.println("유효한 시리얼 넘버가 아님");
			return "serial_num_failed";
		}*/
		
		// java.net.SocketException: Socket closed. dynamodb insert를 주석처리해도 몇초지나면 똑같은 오류 계속남. http 잦은 요청 자체가 문제
		
		
		// temp 객체 생성전 유효한 데이터값들이 왔는지 검증후 temp 객체로 변환할것.
		
		Temp temp = new Temp();
		temp.setInfant_idx(infant_idx);
		temp.setDate_time(service.get_now_time());
		
		if(memo != null && memo.length() > 0) {
			temp.setMemo(memo);
		}
		
		temp.setTemp(temperature);
		//temp.setSerial_num(serial_num);
		service.saveTemp(temp);

		messagingTemplate.convertAndSend("/dashboard/temp/" + infant_idx, temp);	// 소켓전송(대쉬보드 수치 변경)
		
		return "success";
	}
	
	/*@ResponseBody
	@GetMapping("/app/dynamodb/write_peepee")
	public String peepee_write(HttpSession session) {
		Peepee peepee = new Peepee();
		peepee.setInfant_idx(55);
		//peepee.setDate_time(service.get_now_time());
		peepee.setTemp(37.0f);
		peepee.setHumid(62);
		peepee.setChange_chk(false);
		peepee.setDefec_chk(true);
		
		service.savePeepee(peepee);
		
		int member_idx = (int)session.getAttribute("idx");
		Alert alert = new Alert();
		alert.setRequester(0);	// 관리자 : 0번
		alert.setRecipient(member_idx);
		alert.setRequest_date(memberService.set_now_time());
		alert.setEvent_type('6');
		alertService.insert(alert);
		
		List<Alarm> data = groupService.getListByRecipient(member_idx);
		
		messagingTemplate.convertAndSend("/dashboard/peepee/" + 55, peepee);	// 소켓전송(대쉬보드 수치 변경)
		//messagingTemplate.convertAndSend("/alarm/group_request_added/" +  member_idx, alert);	// 소켓 실시간 전체 알람리스트 전달
		messagingTemplate.convertAndSend("/alarm/group_request_added/" +  member_idx, data);	// 소켓 실시간 전체 알람리스트 전달
		return "success";
	}
	
	@ResponseBody
	@PostMapping("/app/dynamodb/write_bottle")
	public String bottle_write(@RequestParam("infant_idx") int infant_idx, HttpSession session) {
		Bottle bottle = new Bottle();
		bottle.setInfant_idx(infant_idx);
		bottle.setDate_time(service.get_now_time());
		bottle.setTemp(37.0f);
		bottle.setAngle(75.0f);
		bottle.setFeed_chk(true);
		
		service.saveBottle(bottle);
		
		int member_idx =  (int)session.getAttribute("idx");
		Alert alert = new Alert();
		alert.setRequester(0);	// 관리자 : 0번
		alert.setRecipient(member_idx);
		alert.setRequest_date(memberService.set_now_time());
		alert.setEvent_type('7');
		alertService.insert(alert);
		
		List<Alarm> data = groupService.getListByRecipient(member_idx);
		
		messagingTemplate.convertAndSend("/dashboard/bottle/" + infant_idx, bottle);	// 소켓전송(대쉬보드 수치 변경)
		messagingTemplate.convertAndSend("/alarm/group_request_added/" +  member_idx, data);	// 소켓 실시간 전체 알람리스트 전달
		return "success";
	}*/
	
	@ResponseBody
	@PostMapping("/dynamodb/batch_save_temp")
	public String temp_batch_save(@RequestParam("temps") List<Temp> temps, HttpServletRequest request) {
		if(appService.check_auth_app(request) == true) {
			service.batchSaveTemp(temps);
			return "success";
		} else {
			return "auth_failed";
		}
	}
	
	@ResponseBody
	@PostMapping("/dynamodb/batch_save_peepee")
	public String peepee_batch_save(@RequestParam("peepees") List<Peepee> peepees, HttpServletRequest request) {
		if(appService.check_auth_app(request) == true) {
			service.batchSavePeepee(peepees);
			return "success";
		} else {
			return "auth_failed";
		}
	}
	
	@ResponseBody
	@PostMapping("/dynamodb/batch_save_bottle")
	public String bottle_batch_save(@RequestParam("bottles") List<Bottle> bottles, HttpServletRequest request) {
		if(appService.check_auth_app(request) == true) {
			service.batchSaveBottle(bottles);
			return "success";
		} else {
			return "auth_failed";
		}
	}

	// temp 제일 최신 데이터 가져오기 (Temp : 5~10초마다 클라이언트에서 통신. 대쉬보드 페이지에서 5~10초 간격으로 setTimeout(/dashboard/temp 알람 요청) + 소켓수신)
	/*@ResponseBody
	@GetMapping("/dynamodb/scan_recent_data")
	public String recent_test() {
		return service.getRecentTemp(20);
	}*/

	@ResponseBody
	@GetMapping("/dynamodb/delete_test")
	public String delete_test(@PathVariable("idx") String idx) {
		service.deleteTemp(idx);
		return "success";
	}
	@ResponseBody
	@GetMapping("/dynamodb/send_log")
	public String send_log() {
		// 템프의 경우 해당 일자의 모든 행을 로그파일로 옮긴 후 삭제
		return null;
	}

	/*// 앱에서 템프 데이터가 insert되고 성공하면 앱에서 아래경로로 요청보낼것. (소켓 사용을 위해 이벤트 발생을 체크하기 위함)
	@ResponseBody
	@GetMapping("/dynamodb/insert_alarm/temp/{infant_idx}")
	public String temp_alarm(@PathVariable("infant_idx") int infant_idx) {
		String temp = service.getRecentTemp(infant_idx);
		messagingTemplate.convertAndSend("/dashboard/temp/" + infant_idx, temp);	// 소켓전송
		return "success";
	}

	// 앱에서 피피 데이터가 insert되고 성공하면 앱에서 아래경로로 요청보낼것. (소켓 사용을 위해 이벤트 발생을 체크하기 위함)
	@ResponseBody
	@GetMapping("/dynamodb/insert_alarm/peepee/{infant_idx}")
	public String peepee_alarm(@PathVariable("infant_idx") int infant_idx) {
		return "";
	}

	// 앱에서 보틀 데이터가 insert되고 성공하면 앱에서 아래경로로 요청보낼것. (소켓 사용을 위해 이벤트 발생을 체크하기 위함)
	@ResponseBody
	@GetMapping("/dynamodb/insert_alarm/bottle/{infant_idx}")
	public String bottle_alarm(@PathVariable("infant_idx") int infant_idx) {
		return "";
	}*/
}
