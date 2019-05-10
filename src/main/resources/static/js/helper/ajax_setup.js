/*
* last-updated date : 2018-03-05
* writer : 김창현
* */

$(function() {

    $.ajaxSetup( {
        /** ajax 기본 옵션 */
        // cache: false,			// 캐쉬 사용 금지 처리
        timeout: 20000,			// 타임아웃 (20초)

        // 통신 시작전 실행할 기능 (ex: 로딩바 표시)
        beforeSend: function() {
            // loader.show();
            console.log("AJAX 통신 시작");
        },
        // 통신 실패시 호출될 함수 (파라미터는 에러내용)
        error: function(error) {
            // 404 -> Page Not Found
            // 50x -> Server Error(웹 프로그램 에러)
            // 200, 0 -> 내용의 형식 에러(JSON,XML)
            console.log(">> AJAX 통신 에러 >> " + error.status);

            var error_msg = "[" + error.status + "] " + error.statusText;

            if(error.status === 404){
                console.log('잘못된 요청, 페이지 혹은 url이 없음');
            } else if(error.status === 403){
                console.log('서버에서의 접근 거부 권한 혹은 cors 체크');
            }

            var code = parseInt(error.status / 100);
            if (code === 4) {	// 400번대의 에러인 경우
                error_msg = "잘못된 요청입니다.\n" + error_msg;
            } else if (code === 5) {
                error_msg = "서버의 응답이 없습니다.\n" + error_msg;
            } else if (code === 2 || code === 0) {
                error_msg = "서버의 응답이 잘못되었습니다.\n" + error_msg;
            }
            console.log(error_msg);
        },
        // 성공,실패에 상관 없이 맨 마지막에 무조건 호출됨 ex) 로딩바 닫기
        complete: function() {
            // loader.hide(800);
            console.log("AJAX 통신 종료");
        }
    } );
});

function sendCsrfToken(beforeSend_xhr) {
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    beforeSend_xhr.setRequestHeader(header, token);
}	// 모든 ajax 통신시 csrf token 헤더에 전송
