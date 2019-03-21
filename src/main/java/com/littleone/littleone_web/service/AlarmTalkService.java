package com.littleone.littleone_web.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.littleone.littleone_web.api_request.AlarmTalkRequest;

@Service
public class AlarmTalkService {
	private final String API_STORE_ID = "littleone";
	private final String API_STORE_KEY = "OTI5NS0xNTM2Mjg2NDAwMzIxLWYwNzNmYTFkLWNiNTAtNDRmZC1iM2ZhLTFkY2I1MDI0ZmRhNw==";
	private final String API_SEND_PROFILE_KEY = "f687617b26f6a0e1b9550d06e2559fbf9aa24e07";
	private final String MSG_SEND_URL = "http://api.apistore.co.kr/kko/1/msg/" + API_STORE_ID;
	private final String RESULT_URL = "http://api.apistore.co.kr/kko/{v1}/report/{client_id}";
	private final String SEARCH_TEMPLATE_URL = "http://api.apistore.co.kr/kko/{v1}/template/list/{client_id}";
	private final String REGISTER_CALL_NUM_URL = "http://api.apistore.co.kr/kko/{v1}/sendnumber/save/{client_id}";
	private final String SEARCH_CALL_NUM_URL = "http://api.apistore.co.kr/kko/{v1}/sendnumber/list/{client_id}";
	private final String AUTH_SEND_NUM_URL = "http://api.apistore.co.kr/kko/{apiVersion}/sendnumber/save/{client_id}";
	private final String API_VERSION = "1";
	
	private RestTemplate restTemplate;

	public void send_welcome_msg(String receiver_phone, String receiver_name) {
		String join_template_code = "join01";
		
		/*AlarmTalkRequest request2 = new AlarmTalkRequest();
		request2.setPhone("01045860883");
		request2.setCallback("01043424658");
		request2.setMsg("알림톡 테스트");
		request2.setTemplate_code(join_template_code);*/
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", receiver_phone);
		params.put("callback", "01043424658");
		params.put("msg", "안녕하세요 " + receiver_name +"님, 리틀원에 가입하신 것을 환영합니다!\r\n" + 
				"\r\n" + 
				"리틀원은 IoT를 통해 어디서나 내 아이의 건강 상태를 확인하고 케어할 수 있는 다음 세대의 육아 서비스를 제공합니다. \r\n" + 
				"\r\n" + 
				"어렵고 힘든 육아, 저희가 도울테니 소중한 아이를 안전하고 편리하게 돌보세요-");
		params.put("template_code", join_template_code);
		params.put("btn_types", "웹링크");
		params.put("btn_txts", "리틀원 웹");
		params.put("btn_urls1", "https://www.littleone.kr");
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.setAll(params);
		
		restTemplate = new RestTemplate();	
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		headers.add("x-waple-authorization", API_STORE_KEY);
		
	/*	Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("apiVersion", API_VERSION);
		uriVariables.put("client_id", API_STORE_ID);*/
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(MSG_SEND_URL, HttpMethod.POST, requestEntity, String.class);
		System.out.println(response.getBody());
		
	}
	
	public void send_invite_msg(String receiver_phone, String inviter_name, String invite_url) {
		String join_template_code = "invite";
		
		/*AlarmTalkRequest request2 = new AlarmTalkRequest();
		request2.setPhone("01045860883");
		request2.setCallback("01043424658");
		request2.setMsg("알림톡 테스트");
		request2.setTemplate_code(join_template_code);*/
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", receiver_phone);
		params.put("callback", "01043424658");
		params.put("msg", inviter_name +"님이 리틀원에 그룹으로 초대하셨습니다!\r\n" + 
				"\r\n" + 
				"아래 버튼을 통하여 가입을 완료하시면 자동으로 " + inviter_name + "님이 초대한 그룹에 속하게 됩니다. \r\n" +
				"\r\n" + 
				"어렵고 힘든 육아, 저희가 도울테니 소중한 아이를 안전하고 편리하게 돌보세요-");
		params.put("template_code", join_template_code);
		params.put("btn_types", "웹링크");
		params.put("btn_txts", "가입하기");
		params.put("btn_urls1", invite_url);
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.setAll(params);
		
		restTemplate = new RestTemplate();	
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		headers.add("x-waple-authorization", API_STORE_KEY);
		
	/*	Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("apiVersion", API_VERSION);
		uriVariables.put("client_id", API_STORE_ID);*/
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(MSG_SEND_URL, HttpMethod.POST, requestEntity, String.class);
		System.out.println(response.getBody());
		
	}
	
	public void auth_send_number() {
		restTemplate = new RestTemplate();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("sendnumber", "01043424658");
		params.put("comment", "리틀원 관리자 번호");
		params.put("pintype", "SMS");
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.setAll(params);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("x-waple-authorization", API_STORE_KEY);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		Map<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("apiVersion", API_VERSION);
		uriVariables.put("client_id", API_STORE_ID);
		
		ResponseEntity<String> response = restTemplate.exchange(AUTH_SEND_NUM_URL, HttpMethod.POST, requestEntity, String.class, uriVariables);
		System.out.println(response.getBody());
		
	}
}
