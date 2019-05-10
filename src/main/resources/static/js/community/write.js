(function () {
    $(function () {
        var fileArr = [];


        /*
        *
        * true일때만 벗어날떄 삭제 통신
        * 글 작성하면 isDelteOk에 false를 할당해서 함수 실행을 차단;
        *
        *
        *
        * */
        var isDeleteOk = true;

        /*페이지 벗어날 때*/
        $(window).on('unload', function () {
            // async: false will make the AJAX synchronous in case you're using jQuery

            if (isDeleteOk) {

                var service = typeof($(".__write-board-button").attr("data-category")) != "undefined" ? $(".__write-board-button").attr("data-category") : $(".__modify-board-button").attr("data-category");
                var ajax_url = "";

                if (service === "qna") {
                    ajax_url = "/removeTempfile/product";
                } else if (service === "service") {
                    ajax_url = "/removeTempfile/service";
                } else {
                    return false;
                }

                $.ajax({
                    url: ajax_url,
                    type: "GET",
                    processData: false,        // 중요. 데이터를 쿼리 문자열로 변환하는 jQuery 형식을 방지
                    contentType: false,
                    async: false,
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr)
                    },
                    success: function (data) {
                        alert(data);
                    },
                    complete: function (e) {
                        alert("comlete");
                    }
                });//end ajax
            } //end if
        });


        /* 글 작성 취소버튼 클릭 시 */
        $(".__cancel-board-button").on("click", function (e) {
            e.preventDefault();

            var service = typeof($(".__write-board-button").attr("data-category")) != "undefined" ? $(".__write-board-button").attr("data-category") : $(".__modify-board-button").attr("data-category");
            var ajax_url = "";

            if (service === "qna") {
                ajax_url = "/removeTempfile/product";
            } else if (service === "service") {
                ajax_url = "/removeTempfile/service";
            } else {
                return false;
            }

            $.ajax({
                url: ajax_url,
                type: "GET",
                processData: false,        // 중요. 데이터를 쿼리 문자열로 변환하는 jQuery 형식을 방지
                contentType: false,
                async: false,
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr)
                    alert("!");
                },
                success: function (data) {
                    alert(data);
                },
                complete: function (e) {
                    alert("conplete");
                }
            });
        });


        /*서버로 img파일 보내기*/
        function sendFile(file, that) {
            var data = new FormData();

            for (var i = 0; i < file.length; i++) {
                data.append("upload", file[i]);
            }


            var service = typeof($(".__write-board-button").attr("data-category")) != "undefined" ? $(".__write-board-button").attr("data-category") : $(".__modify-board-button").attr("data-category");
            /*이미지를 어떻게 dataurl로 하고, s3로 할 것인가?*/
            $.ajax({
                data: data,
                type: "POST",
                url: "/community/summernote/upload/" + service,
                enctype: "multipart/form-data",
                cache: false,
                processData: false,
                contentType: false,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                    console.log('이미지 전송 시작..');
                },
                success: function (url) {
                    console.log("통신 성공");
                    /*에디터에 이미지 삽입*/
                    console.log(url);
                    for (i = 0; i < url.length; i++) {
                        $(that).summernote('insertImage', url[i]);
                        fileArr.push(url[i]);
                    }
                    console.log('000');
                    console.log(fileArr);

                },
                complete: function () {
                    console.log('이미지 전송 끝..');
                }
            });
        }

        /*위지윅스 초기셋팅*/
        var summerNote = $("#summernote").summernote({
            toolbar: [
                // [groupName, [list of button]]
                ['style', ['bold', 'italic', 'underline', 'clear']],
                ['font', ['strikethrough', 'superscript', 'subscript']],
                ['fontsize', ['fontsize']],
                ['color', ['color']],
                ['para', ['ul', 'ol', 'paragraph']],
                ['height', ['height']],
                ['hr'],
                ['picture'],
                ['link'],
                ['undo'],
                ['redo']
            ],
            height: 500,
            tabsize: 2,
            maximumImageFileSize: 3000000,
            callbacks: {
                onImageUpload: function (files, that) {
                    console.log(files);
                    that = $(this);
                    sendFile(files, that);
                },
                onKeydown: function (e) {
                    console.log('Key is downed:', e.keyCode);
                }
            }
        });


        /* qna 게시글 작성 */
        $(".__write-board-button").on("click", function (e) {
            var category = $(this).attr('data-category');
            e.preventDefault();

            isDeleteOk = false;

            var formData = new FormData($("#write-form")[0]);
            $.ajax({
                url: "/community/" + category,
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
                    if (data === '1') {
                        alert("올바르지 않은 첨부파일 형식입니다.");
                        return false;
                    } else if (data === '0') {
                        alert("로그인 후 글작성이 가능합니다.");
                        var url = window.location.href.split("/community/" + category + "/write");
                        $(location).attr("href", url[0] + "/login");			// 로그인 페이지로 이동
                    } else if (data === '2') {
                        alert("파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                    } else if (data === '3') {
                        // 글작성 성공
                        var url = window.location.href.split("/community/" + category + "/write");
                        $(location).attr("href", url[0] + "/community/" + category + "/page/1");	// qna 게시판 목록으로 이동
                    } else if (data === '4') {
                        alert("파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                    } else if (data == '5') {
                        alert("잘못된 파일 형식입니다. 파일 형식이나 확장자가 올바른지 확인해주세요.");
                    } else if (data == "subject_failed") {
                        alert("제목은 필수 입력 사항입니다.");
                    } else if (data == "contents_failed") {
                        alert("내용은 필수 입력사항입니다.");
                    } else {
                        console.log("ajax data: " + data);
                        alert("잘못된 접근입니다.");
                    }
                },
                error: function () {
                    alert("ajax 통신 오류");
                }
            });
        });

        /* 취소 버튼 클릭시 */
        $(".__cancel-board-button").on("click", function (e) {
            e.preventDefault();
            var category = $(this).attr('data-category');

            var url = window.location.href.split("/community/" + category + "/write");
            $(location).attr("href", url[0] + "/community/" + category + "/page/1");			// 게시글 페이지로 이동
        });


    });


})();