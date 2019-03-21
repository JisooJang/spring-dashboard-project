package com.littleone.littleone_web.config;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.littleone.littleone_web.domain.Member_log;
import com.littleone.littleone_web.service.DeviceService;
import com.littleone.littleone_web.service.MemberLogService;

public class HttpSessionHandshakeInterceptor implements HandshakeInterceptor {
	// 소켓연결 전 필요한 핸드쉐이크 전후 이벤트가 발생할때 가로채어지는 클래스

	@Autowired
	private MemberLogService logService;
	
	@Autowired
	private DeviceService deviceService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// TODO Auto-generated method stub

		HttpHeaders headers = request.getHeaders();
		// 앱 소켓요청일 경우에만 아래 인증 체크
		if(headers.get("member-idx") != null && headers.get("session-id") != null) {
			
			int member_idx = Integer.parseInt(headers.get("member-idx").get(0));
			String session_id = headers.get("session-id").get(0);
			String temp_serial_num = headers.get("temp-serial-num").get(0);
			
			System.out.println("member_idx : " + member_idx + " session_id : " + session_id);

			Member_log log = logService.authSession(session_id, member_idx);

			if(log != null) {
				System.out.println("beforeHandshake header auth success : " +  member_idx + ", " + session_id);
				
				if(deviceService.findBySerialNumAndType(member_idx, temp_serial_num, '1') == null) {
					System.out.println("유효한 시리얼 넘버가 아님");
					return false;
				}
				return true;	// 소켓연결시킴
			} else {
				System.out.println("beforeHandshake header auth failed : " +  member_idx + ", " + session_id);
				return false;	// 소켓 연결 비허용
			}

		} else {

			// 웹 소켓요청 인증체크
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session = servletRequest.getServletRequest().getSession(false);
			
			//Returns the current HttpSession associated with this request or, 
			//if there is no current session and create is true, returns a new session. 

			//If create is false and the request has no valid HttpSession, this method returns null. 


			// ppuagirls3@naver.com  log  null (session_id 다름)
			if(session.getAttribute("idx") != null) {
				/*Member_log log = logService.authSessionWeb(session.getId(), (int)session.getAttribute("idx"));
				if(log != null) {
					return true;
				} else {
					return false;
				}*/
				return true;
			} else {
				return false;	// 소켓 연결 비허용
			}
			
		}

		//member_idx, sesson_id를 헤더에서 가져와 logService로 올바른쌍인지 체크후 올바르면 true리턴, 아니면 false 리턴

	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub	
	}

}
