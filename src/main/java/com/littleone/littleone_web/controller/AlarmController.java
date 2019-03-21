package com.littleone.littleone_web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.littleone.littleone_web.domain.Alarm;
import com.littleone.littleone_web.domain.Alarm_index;
import com.littleone.littleone_web.domain.Alert;
import com.littleone.littleone_web.domain.Temp;
import com.littleone.littleone_web.domain.WriteTempData;
import com.littleone.littleone_web.service.AppService;
import com.littleone.littleone_web.service.DynamoDBService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MemberService;

@RestController
public class AlarmController {
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;	// SimpleMessagingTemplate은 메시지를 먼저 받지 않아도 애플리케이션 내의 어느 곳에서든지 메시지를 전송한다

	@Autowired
	MemberService memberService;

	@Autowired
	GroupService groupService;
	
	@Autowired
	private DynamoDBService dynamoDBservice;
	
	@Autowired
	private AppService appService;

	Alert data;

	// 기본 header 요청(ajax 통신)
	@ResponseBody
	@GetMapping("/alarm/group_request/{idx}")
	public Alarm_index send_group_request(@PathVariable("idx") String idx) throws Exception {	// 매개변수에는 클라이언트로부터 수신되는 메시지 객체를 넣음
		List<Alarm> data = groupService.getListByRecipient(Integer.parseInt(idx));
		Alarm_index index;
		if(data.size() > 10) {
			// 알림 갯수가 10개 넘는다는 체크값 필요
			data = data.subList(0, 10);
			index = new Alarm_index(data, 'f');
		} else {
			index = new Alarm_index(data, 't');
		}
		return index;
	}
	
	@MessageMapping("/alarm")	// 요청 토픽이 "/alarm" 일 때 처리할 함수
	public void get_alarm_list(int session_idx) throws Exception {	// 매개변수에는 클라이언트로부터 수신되는 메시지 객체를 넣음
		List<Alarm> data = groupService.getListByRecipient(session_idx);
		messagingTemplate.convertAndSend("/alarm/" + session_idx, data);
	}
	
	// 소켓 알림 요청시 수신자에게 전체 알림리스트 전달
	@MessageMapping("/group_request/added")	
	public void group_request_added(Alarm alarm) throws Exception {	// 매개변수에는 클라이언트로부터 수신되는 메시지 객체를 넣음
		System.out.println("recipient_idx=" + alarm.getRecipient());
		List<Alarm> data = groupService.getListByRecipient(alarm.getRecipient());	// outer join 필요
		Alarm_index index;
		if(data.size() > 10) {
			// 알림 갯수가 10개 넘는다는 체크값 필요
			data = data.subList(0, 10);
			index = new Alarm_index(data, 'f');
		} else {
			index = new Alarm_index(data, 't');
		}
		System.out.println("/alarm/" + alarm.getRecipient());
		if(alarm.getEvent_type() == '2') {	// 게시판 관련 알람이면
			
		}
		messagingTemplate.convertAndSend("/queue/alarm/group_request_added/" +  alarm.getRecipient(), index);	// 소켓 실시간 전체 알람리스트 전달
	}
	
	/*@MessageMapping("/dashboard/temp")
	public void dashboard_temp_data(int infant_idx) throws Exception {
		String temp = dynamoDBservice.getRecentTemp(infant_idx);
		messagingTemplate.convertAndSend("/queue/dashboard/temp/" + infant_idx, temp);
	}*/
	
	@MessageMapping("/alert")
	public void send_notice() {
		// 공지 내용 : 메시지
	}
	
	@MessageMapping("/app/socket/write_temp")
	public void app_temp_write(WriteTempData data) {	
		
		// temp 객체 생성전 유효한 데이터값들이 왔는지 검증후 temp 객체로 변환할것. 앱에서 특정 온도 미만이면 기계미착용으로 판단하고 save하지 말것
		/*System.out.println("getInfant_idx " + data.getInfant_idx());
		System.out.println("getTemperature " + data.getTemperature());*/
		
		Temp temp = new Temp();
		temp.setInfant_idx(data.getInfant_idx());
		temp.setDate_time(dynamoDBservice.get_now_time());
		
		if(data.getMemo() != null && data.getMemo().length() > 0) {
			temp.setMemo(data.getMemo());
		}
		
		temp.setTemp(data.getTemperature());
		//temp.setSerial_num(data.getSerial_num());
		dynamoDBservice.saveTemp(temp);

		messagingTemplate.convertAndSend("/topic/dashboard/temp/" + data.getInfant_idx(), temp);	// 소켓전송(대쉬보드 수치 변경)
		
		if(temp.getTemp() >= 33.5 && temp.getTemp() <= 34.6) {
			// 저체온증 의심 알림
			messagingTemplate.convertAndSend("/topic/temp/low/" + data.getInfant_idx(), temp);	// 소켓전송(대쉬보드 수치 변경)
		} else if(temp.getTemp() >= 37.6) {
			// 발열 의심
			messagingTemplate.convertAndSend("/topic/temp/high/" + data.getInfant_idx(), temp);	// 소켓전송(대쉬보드 수치 변경)
		}
	}

	public void set_group_alert(Alert data) {
		this.data = data;
	}
}
