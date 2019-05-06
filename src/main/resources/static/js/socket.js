$(function() {
	var sockJS = null;
	var stompClient = null;
	var headers = {
			/*login: 'mylogin',
			passcode: 'mypasscode',
			'client-id': 'my-client-id'*/
			request_type : 'web'
	};

	var idx = $("#session_idx").val();

	// 세션에 로그인이 되어있는 상태면 소켓을 연결한다.
	if(idx != null && idx.length > 0) {
		connect();
		
		if(sockJS != null) {
			sockJS.onclose = function() {
				alert("sock close");
			};	// sockJS onclose event listener
			
		}
	}

	$(".main_logout").click(function() { disconnect(stompClient); });
});//JQUERY BLOCK (이안에서만 제이쿼리가 먹힌다.) 

function connect() {
	sockJS = new SockJS("/websockethandler");
	
	stompClient = Stomp.over(sockJS);
	
	//stompClient.debug = null; stomp debug message 클라이언트 콘솔에 안보이게함.
	
	var socket_id = document.getElementById('session_idx').value;		// 특정 유저를 구분하는 unique값
	var infant_idx = $("#infant_idx").val();
	
	// {} 첫번째 매개변수 부분은 소켓 헤더설정
	stompClient.connect({}, function(frame) {
		// 전체 알림 데이터 구독	
		stompClient.subscribe("/queue/alarm/group_request_added/" + socket_id, function(message) {			
			//2018-04-23
			if(message != null) {
				var parseData = JSON.parse(message.body);			
				var alarmIcon = $(".header--menu__alarm__notification");
				var alarmBox = $(".header--menu__alarm__message__list");
				var mobilealarmBox = $(".header--mobile__alarm__message__list");
				
				if (parseData.alarm.length >= 1) {
                    alarmIcon.css('display', 'block');
                    var template = Handlebars.compile($("#alarm_template").html());
                    var html = template(parseData);
                    $(alarmBox).html(html);
                    $(mobilealarmBox).html(html);
                }

                if (parseData.alarm.length < 1 || parseData.alarm.length === null || parseData.alarm.length === undefined) {
                    alarmIcon.css('display', 'none');
                    var template = Handlebars.compile($("#alarm_template").html());
                    var html = template(parseData);
                    $(alarmBox).html(html);
                    $(mobilealarmBox).html(html);
                }
			}
		});//신규 알림


		// 세션에 아이정보가 저장되어있는 경우 아래 소켓 이벤트 구독
		if(infant_idx != null && infant_idx.length > 0) {
			stompClient.subscribe("/topic/temp/low/" + infant_idx, function(message) {			
				if(message != null) {
					                                     
				}
			}); //템프 저체온 알림

			stompClient.subscribe("/topic/temp/high/" + infant_idx, function(message) {			
				if(message != null) {
					
				}
			}); //템프 고체온 알림

			stompClient.subscribe("/topic/bottle/" + infant_idx, function(message) {		
				alert("새로운 수유활동 알림");
				if(message != null) {

				}
			}); //보틀 수유활동 알림

			stompClient.subscribe("/topic/peepee/" + infant_idx, function(message) {			
				alert("새로운 배변활동 알림");
				if(message != null) {

				}
			}); //피피 배변활동 알림
		}

		var url = window.location.href.split("/");

		var dashboard_date = $("#dashboard_date").val();

		if(infant_idx != null && infant_idx.length > 0 && url[3] == 'dashboard') {	// 대쉬보드 페이지면 + 조건 추가 필요

			var date = new Date(); 
			var year = date.getFullYear(); 
			var month = new String(date.getMonth()+1); 
			var day = new String(date.getDate()); 
			if(dashboard_date != null && dashboard_date === year + '-' + month + '-' + day) {	// 대쉬보드 지정 날짜가 오늘 날짜이면
				stompClient.subscribe("/topic/dashboard/temp/" + infant_idx, function(message) {
					if(message != null) {
						var parseData = JSON.parse(message.body);
						var date_time = parseData.date_time + "";
						var parseDateTime = date_time.substr(8, 2) + ":" + date_time.substr(10, 2) + ":" + date_time.substr(12, 2);
						$(".__temp-time").text(parseDateTime);
						//$(".__temp-time2").text(parseDateTime + " 측정 체온(일별)");	// 최근 업데이트 시간 text 설정
						$("#baby-temperature-widget").html(parseData.temp + "<span>℃</span>");
					}
				});	// 대쉬보드 템프 실시간 데이터 수신

				stompClient.subscribe("/topic/dashboard/peepee/" + infant_idx, function(message) {
					if(message != null) {
						var parseData = JSON.parse(message.body);
						var date_time = parseData.date_time + "";
						var total_count = parseData.total_count;

						$(".__peepee-time").text(date_time);
						$("#baby-feed-widget").text(total_count + "회");
					}
				});	// 대쉬보드 피피 실시간 데이터 수신

				stompClient.subscribe("/topic/dashboard/bottle/" + infant_idx, function(message) {
					if(message != null) {
						var parseData = JSON.parse(message.body);
						var date_time = parseData.date_time + "";
						var total_count = parseData.total_count;

						$(".__bottle-time").text(date_time);
						$("#baby-defecation-widget").text(total_count + "회");
					}
				});	// 대쉬보드 보틀 실시간 데이터 수신
			}
		}

	});//stompClinet.connect();
	
} //function connect();

//알림 수신 메소드
function group_request_added(requester_idx, recipient_idx) {
	stompClient.send("/group_request/added", {}, JSON.stringify({'requester': requester_idx, 'recipient': recipient_idx}));
} //function group_request_added()

//게시글 댓글 알림 관련 수신 메소드
function board_comment__added(requester_idx, recipient_idx, board_idx) {
	stompClient.send("/group_request/added", {}, JSON.stringify({'requester': requester_idx, 'recipient': recipient_idx, 'board_idx': board_idx}));
} //function group_request_added()

function disconnect(stompClient) {
	//clearInterval(subscribe);
	if (stompClient != null) {
		stompClient.disconnect();
	}
}

function reconnect(stompClient) {
	if(stompClient != null) {
		connect();
	}
}

function sendIdx() {
	stompClient.send("/alarm", {}, $('#session_idx').val());
}

