package com.littleone.littleone_web.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker	// STOMP 메시징 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Bean
	public HandshakeInterceptor HttpSessionHandshakeInterceptor() {
		return new HttpSessionHandshakeInterceptor();
	}
	
	@Autowired
	private StompHandler stompHandler;
	
	/*@Autowired
	private HandshakeInterceptor handshakeInterceptor;*/
	 
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		//config.enableSimpleBroker("/alarm/group_request");	// 구독 url 등록
		config.enableSimpleBroker("/topic", "/queue");
		// 클라이언트에서 subscribe prefix 설정.  해당 api를 구독하는 클라이언트에게 메세지 전송
		
		// "/topic" : Use topic when there are more than one subscribers for a message.
		// "/queue" : Use queue for peer to peer communication.
		
		config.setApplicationDestinationPrefixes("/");	// @MessageMapping 메서드에 접두사를 붙여 바인딩
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.addEndpoint("websockethandler")	// socket.js에서 new SockJS("/websocketHandler") 로 sock객체 받아옴. 웹소켓 핸들러경로("ws://serverURL:port/websockethandler")로 먼저 연결후, 각 채널 url로 송수신
			.setAllowedOrigins("*")		// Client가 접속 할 URL (모든 클라이언트에서 소켓 연결을 허용)
			.withSockJS()				// 클라이언트에서는 SockJS를 사용함을 명시
			.setInterceptors(HttpSessionHandshakeInterceptor());	// Handshake 인터셉터 추가
		/*
		registry
		   .addEndpoint("/stomp")
		   .setAllowedOrigins("http://localhost")
		   .withSockJS()
		   .setStreamBytesLimit(512 * 1024)
		   .setHttpMessageCacheSize(1000)
		   .setDisconnectDelay(30 * 1000);
		*/
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		// TODO Auto-generated method stub
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		// TODO Auto-generated method stub
		registration.interceptors(new ChannelInterceptorAdapter() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					String id;
				}
				return message;
			}
		});
		//registration.interceptors(stompHandler);
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		// TODO Auto-generated method stub
		registration.interceptors(stompHandler);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		// TODO Auto-generated method stub
		return false;
	}
}
