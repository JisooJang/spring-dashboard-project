$(function(){
	$(".reset_button").on("click", function(e) {
		e.preventDefault();
		$("#license_file").attr("value", "");
	});
	
	$(".crn_button").on("click", function(e) {
		e.preventDefault();
		var result = confirm("제출하시겠습니까? 제출 전 표시된 기업 정보를 반드시 확인해주세요.");
		var formData = new FormData($("#crn_submit_form")[0]);
		if(result) {
			$.ajax({
				type: "POST",
				enctype: "multipart/form-data",
                url: "/authentication/business_license",
                data: formData,
                cache: false,
                contentType: false,
                processData: false,
                beforeSend:function(xhr){
            		sendCsrfToken(xhr);
                },
                success: function(data) {
                    var url = window.location.href.split('authentication');
                    if(data == "success") {
                    	alert("인증 신청이 성공적으로 접수되었습니다.");
                        $(location).attr("href", url[0] + "mypage");		// 나중에 aws 서버 로그인 성공 아이피로 수정할 것.
                    } else {	// 실패
                        showPopup("잘못된 파일입니다.");
                    }
                },
                error: function(e) {
                    showPopup("통신에 실패하였습니다.");
                }
			});
		}
	});
	
	$("#cancel_application").on("click", function(e) {
		e.preventDefault();
		var result = confirm("정말 취소하시겠습니까?");
		if(result) {
			$.ajax({
				type: "GET",
                url: "/authentication/canceled",
                beforeSend:function(xhr){
            		sendCsrfToken(xhr);
                },
                success: function(data) {
                    var url = window.location.href.split('authentication');
                    if(data == "success") {
                    	alert("취소가 완료되었습니다.");
                        $(location).attr("href", url[0] + "mypage");		// 나중에 aws 서버 로그인 성공 아이피로 수정할 것.
                    } else {	// 실패
                        alert("로그인 후 이용해주세요");
                        $(location).attr("href", url[0] + "login");
                    }
                },
                error: function(e) {
                    showPopup("통신에 실패하였습니다.");
                }
			});
		}
	});
});