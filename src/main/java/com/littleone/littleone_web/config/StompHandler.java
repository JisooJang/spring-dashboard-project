package com.littleone.littleone_web.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@Component
public class StompHandler extends ChannelInterceptorAdapter {
	// 소켓연결 후 특정 채널로 데이터 보낼때 가로채어지는 클래스
	
	@Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		
		// 네이티브 헤더에 접근하기 위한 accessor 별도 선언
		//NativeMessageHeaderAccessor nativeAccessor = NativeMessageHeaderAccessor.getAccessor(message, SimpMessageHeaderAccessor.class);
		
        String sessionId = accessor.getSessionId();
        Set<String> sessionSet = new HashSet<>();
        String simpMessageType = String.valueOf(message.getHeaders().get("simpMessageType"));
        
        if(StringUtils.equals(simpMessageType, "CONNECT_ACK") || StringUtils.equals(simpMessageType, "CONNECT")) {
        	System.out.println("소켓 연결 : session_id : " + sessionId);
            String simpSessionId = String.valueOf(message.getHeaders().get("simpSessionId"));
            sessionSet.add(simpSessionId);
        } else if(StringUtils.equals(simpMessageType, "DISCONNECT")) {
        	System.out.println("소켓 해제");
            String simpSessionId = String.valueOf(message.getHeaders().get("simpSessionId"));
            sessionSet.remove(simpSessionId);
        }

        /*int uniqueJoinSessionCount = sessionSet.size();
        System.out.println("소켓 총 접속자수 : " + uniqueJoinSessionCount);*/
        
        return super.preSend(message, channel);
	}
	
	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        
	}
}
