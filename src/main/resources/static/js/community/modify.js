/*커뮤니티 게시글 수정*/
$(function () {

    /* 게시글 수정 */
    $(document).on("click", ".__modify-board-button", function (e) {
        e.preventDefault();
        var category = $(this).attr('data-category');
        var boardIdx = $(this).attr('data-board-idx');
        var formData = new FormData($("#community-modify-form")[0]);

        $.ajax({
            url: "/community/" + category + "/modify/" + boardIdx,
            type: "POST",
            enctype: "multipart/form-data",
            data: formData,
            processData: false,		// 중요. 데이터를 쿼리 문자열로 변환하는 jQuery 형식을 방지
            contentType: false,
            async: false,
            dataType: "text",
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data === '0') {
                    alert("로그인 후 이용가능합니다.");
                    var url = window.location.href.split("/community/" + category + "/modify/");
                    $(location).attr("href", url[0] + "/login");			// 로그인 페이지로 이동
                } else if (data === '1') {
                    alert("수정 권한이 없습니다.");
                    var url = window.location.href.split("/community/" + category + "/modify/");
                    $(location).attr("href", url[0] + "/community/" + category + "/" + boardIdx);			// 게시글 페이지로 이동
                } else if (data === '2') {
                    alert("파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                } else if (data === '3') {
                    alert("글 수정에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                } else if (data === "success") {
                    var url = window.location.href.split("/community/" + category + "/modify/");
                    $(location).attr("href", url[0] + "/community/" + category + "/" + boardIdx);			// 게시글 페이지로 이동
                } else {
                    alert("잘못된 접근입니다. data : " + data);
                }

                return false;
            },
            error: function () {
                alert("ajax 통신 오류");
            }
        });
    });

    /* 게시글 수정 취소 버튼 클릭시 */
    $(".__cancel-modify-button").on("click", function (e) {
        e.preventDefault();
        history.back();
    });


});//JQB