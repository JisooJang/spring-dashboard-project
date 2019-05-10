$(function () {

    /*핸들바로 추가해야되는 알림박스는 2개가 있습니다.
    * 모바일과 데스크탑이 나눠져 있습니다.
    * pc :alarmBox,
    * mobile :mobilealarmBox
    * */

    var alarmBox = $(".header--menu__alarm__message__list");
    var mobilealarmBox = $(".header--mobile__alarm__message__list");
    var userIdx = $("#session_idx").val();
    var alarmIcon = $(".header--menu__alarm__notification");
    var allReadBtn = $('#header_alarm_readAll');



    /* 그룹 수락 버튼 클릭시 */
    $(document).on("click", ".user-message--list__conditions .__accept-button", function (e) {
        e.preventDefault();
        var alarm_idx = $(this).attr("data-idx");
        var requester_idx = $(this).attr("requester-idx");
        $.ajax({
            type: "GET",
            url: "/accept_group/" + alarm_idx,
            dataType: "text",
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data === "success") {
                    alert("그룹 수락이 완료되었습니다.");
                    group_request_added(0, userIdx);	// 수신자 새로운 알림리스트 갱신
                    group_request_added(0, requester_idx);	// 그룹 요청자에게도 그룹 수락 알림 전송
                    /*대쉬보드면 새로고침*/
                    var isDashBoard = $("#dashboard-component");
                    if(isDashBoard.length > 0){
                        window.location.href = '/dashboard';
                    }

                } else {
                    alert("통신 오류가 발생하였습니다. 잠시 후에 다시 시도해주세요.");
                }
            },
            error: function () {
                generateModal("ajax 오류");
            }
        });
    }); //그룹 수락 버튼 클릭시

    /* 그룹 요청 취소 버튼 클릭시 */
    $(document).on("click", ".user-message--list__conditions .__cancel-button", function (e) {
        e.preventDefault();
        var alarm_idx = $(this).attr("data-idx");
        $.ajax({
            type: "GET",
            url: "/cancel_request/" + alarm_idx,
            dataType: "text",
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data == "success") {
                    $(e.target).parents(".search_user_data").remove();
                    alert("취소되었습니다.");
                    group_request_added(0, userIdx);
                } else {
                    alert("통신 오류가 발생하였습니다. 잠시 후에 다시 시도해주세요.");
                }
            },
            error: function () {
                alert("ajax 오류");
            }
        });
    });//그룹 요청 취소 버튼 클릭시
    
    if (typeof(userIdx) !== "undefined" && userIdx.length > 0 && userIdx !== null) {
        $.ajax({
            type: "GET",
            url: "/alarm/group_request/" + userIdx,
            dataType: 'json',
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                console.log('들어옴');
                if (data.alarm.length >= 1) {
                    alarmIcon.css('display', 'block');
                    var template = Handlebars.compile($("#alarm_template").html());
                    var html = template(data);
                    $(alarmBox).html(html);
                    $(mobilealarmBox).html(html);
                }

                if (data.alarm.length < 1 || data.alarm.length === null || data.alarm.length === undefined) {
                    alarmIcon.css('display', 'none');

                    var template = Handlebars.compile($("#alarm_template").html());
                    var html = template(data);
                    $(alarmBox).html(html);
                    $(mobilealarmBox).html(html);
                }

            },
            error: function (e) {
                generateModal("ajax-error-modal",e.status,'ajax 통신 에러', false);
            }
        });
    }//if
    
    // 헤더에서 썸네일 클릭후 비밀번호 변경 메뉴 클릭시
    $("#modify_password_menu").on("click", function(e) {
    	e.preventDefault();
    	
    	$.ajax({
    		type: "GET",
            url: "/mypage/check_social",
            dataType: 'text',
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
            	if(data === 'social') {
            		alert("소셜 회원은 비밀번호 변경이 불가합니다.");
            	} else if(data === 'normal') {
            		window.location.href = "/mypage/modify_password";
            	} else {
            		alert("잘못된 통신입니다.");
            	}
            },
            error: function() {
            	alert("잘못된 통신입니다.");
            }
    	});
    });
    
    
    /* 알림 모두 읽기 버튼 클릭시 */
    $("#header_alarm_readAll").on("click", function(e) {
    	e.preventDefault();
        var result = confirm("모든 알람을 읽음 처리하시겠습니까?");
        if (result) {
            $.ajax({
                url: "/alarm/readAll",
                type: "GET",
                dataType: "text",
                success: function (data) {
                    if (data === 'success') {
                        location.reload();
                    }
                },
                error: function () {
                    alert("ajax 통신 에러");
                }
            });
        }
    });
    
});