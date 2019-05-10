(function(){
    $(function(){
    	var board_idx = $("#board_idx").val();
    	
    	// 게시글 수정 post
    	$("#gallery_update_submit").on("click", function(e) {
    		e.preventDefault();
    		var formData = new FormData($("#gallery_modify_form")[0]);
    		$.ajax({
    			url: "/gallery/modify/" + board_idx,
                type: "POST",
                enctype: "multipart/form-data",
                data: formData,
                processData: false,		// 중요. 데이터를 쿼리 문자열로 변환하는 jQuery 형식을 방지
                contentType: false,
                async: false,
                dataType: "text",
                beforeSend: function(xhr) {
        			sendCsrfToken(xhr);
        		},
                success: function (data) {
                	if(data == "1") {
                		alert("존재하지 않는 게시글입니다.");
                	} else if(data == "2") {
                		alert("게시물 수정 권한이 없습니다.");
                	} else if(data == "3") {
                		alert("파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                	} else if(data == "4") {
                		alert("수정이 완료되었습니다.");
                		var url = document.location.href.split("/gallery/modify/");
                		$(location).attr("href", url[0] + "/gallery/view/" + board_idx);
                	} else {
                		alert("잘못된 서버통신입니다.");
                	}
                }, error: function() {
                	alert("게시글 수정 통신에 실패하였습니다.");
                }
    		});
    	});	
    });//JQB
})();//iife