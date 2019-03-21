/*package com.littleone.littleone_web;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import com.littleone.littleone_web.domain.Temp;
import com.littleone.littleone_web.domain.WriteTempData;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LittleoneApplicationTests {

	static final String WEBSOCKET_URI = "ws://13.125.87.44:8080/websockethandler";
	static final String WEBSOCKET_SEND_URI = "/app/socket/write_temp";
	static final String WEBSOCKET_TOPIC_URI = "/queue/dashboard/temp/";
	int numberOfConnections = 500;
	
	CountDownLatch lock = new CountDownLatch(numberOfConnections);	
	// 하나 이상의 스레드가 다른 스레드에서 수행되는 일련의 작업이 완료 될 때까지 대기하도록하는 동기화 보조 도구. 객체 생성시 쓰레드 수 만큼 초기화시킴

	BlockingQueue<Temp> blockingQueue;
	List<StompSession> socketSessions;
	WebSocketStompClient stompClient;

	WriteTempData data;

	private List<Transport> createTransportClient() {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		return transports;
	}

	@Before
	public void setup() throws InterruptedException {
		blockingQueue = new LinkedBlockingDeque<>();
		stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		socketSessions = new ArrayList<StompSession>(500);
	}

	@Test
	public void contextLoads() throws InterruptedException {
		//StompSession stompSession = null;
		
		for(int i=0 ; i < numberOfConnections ; i++) {
			TimeUnit.SECONDS.sleep(1);
			// TODO Auto-generated method stub
			try {
				StompSession stompSession = stompClient.connect(WEBSOCKET_URI, new StompSessionHandlerAdapter() {
				}).get(1, TimeUnit.SECONDS);
				socketSessions.add(stompSession);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	// 500개의 소켓 접속

		for(int i=0 ; i<socketSessions.size() ; i++) {			
			Thread t = new Thread(new ThreadTest(i));
			t.start();
		}
		
		lock.await();	// 각 쓰레드가 종료될때까지 test를 종료시키지 않고 기다림
	}


	class DefaultStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return Temp.class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			blockingQueue.offer((Temp) o);
		}
	}
	
	class ThreadTest implements Runnable {
		int index;
		ThreadTest(int i) {
			this.index = i;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("run 들어옴 : " + index);
			
			data = new WriteTempData();
			data.setInfant_idx(65);
			data.setMember_idx(55);
			data.setMemo("socket test");
			data.setSerial_num("5544332211887766");
			data.setSession_id("2d5b86d0-c96f-4f6c-bdab-ced0370f3aeb");
			data.setTemperature(27.6f);
			
			while(true) {
				System.out.println("while문 들어옴 : " + index);
				socketSessions.get(index).send(WEBSOCKET_SEND_URI, data);
				try {
					TimeUnit.SECONDS.sleep(7);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
	}

}
*/