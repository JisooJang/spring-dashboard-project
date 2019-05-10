(function () {
    $(function () {

        /*해쉬태그 추가하는 메서드(갯수제한:20개)*/

        var limitHashlen = 40;

        $("#gallery_tag").on('keydown', function (e) {
            var key = e.keyCode;

            if (key === 13 || key === 32) { //enter(13), spcae(32)를 keydown하였을 경우
                e.preventDefault();
                var tagName = $(this).val();

                if(!isEmpty(tagName) && (tagName.length > 0 && tagName.length <= limitHashlen)){
                    generateHashTag(tagName);
                    $(this).val('');
                } else if(tagName.length > limitHashlen){
                        alert("해쉬태그는 40자 까지만 가능합니다.");
                        return false;
                } else if(isEmpty(tagName)){
                    return false;
                }
            } else if(key === 27){
                $(this).val('');
            }

        });

        $(".btn_add_tag").on('click', function (e) {
            e.stopPropagation();

            var tagName = $("#gallery_tag").val();

            if (!isEmpty(tagName) && (tagName.length > 0 && tagName.length <= limitHashlen)) {
                generateHashTag(tagName);
                $("#gallery_tag").val('');
            } else if (tagName.length > limitHashlen) {
                generateModal("해쉬태그는 40자 까지만 가능합니다.");
                return false;
            } else if (isEmpty(tagName)) {
                return false;
            }
            $("#gallery_tag").val('');
        });


        /*해쉬 태그 삭제 메서드*/
        $(document).on("click",".btn_delete_hashtag", function(e){
            $(this).parent().remove();
        });



        function generateHashTag(str){
            var tagLen = $(".gallery_hashtag").length;
            if(tagLen<=20){
                var hashtag = str.replace(/\s+/g, ' ').replace(/[\'\".,\/#!$%\\^&\*;:{}=\-_`~()\[\]|+@?<>]/g, '').replace(/\s{2,}/g, ' ').toUpperCase();
                if(isEmpty(hashtag)){
                    console.log('해쉬태그가 비었습니다.');
                    return false;
                } else{
                    var tag = $(' <li>\n' +
                        '                    <a href="#" class="gallery_hashtag">\n' +
                        '                        <input type="hidden" name="tags" value='+hashtag+'>\n' +
                        '                        <div><span>'+hashtag+'</span></div>\n' +
                        '                    </a>\n' +
                        '                    <span class="btn_delete_hashtag">X</span>\n' +
                        '                </li>');

                    $("#container_tag").append(tag);
                }

            } else{
                generateModal('20개 까지만 가능합니다.');
                return false;
            }

        }

        galleryTextCount("#gallery_title");
        galleryTextCount("#gallery_content");

        $("#gallery_write").on("click", function (e) {
            e.preventDefault();

            /*제목의 value*/
            var titleVal = $('input[name="title"]').val();
            /*내용의 value*/
            var contentVal = $("input[name='content']").val();
            /*사진 파일의 value*/
            var imageVal = $("input[name='upload_image']").val();
            if(isEmpty(titleVal)){
                generateModal('제목을 입력해주세요.');
                return false;
            }
            if(isEmpty(contentVal)){
                generateModal('내용을 입력해주세요.');
                return false;
            }
            if(isEmpty(imageVal)){
                generateModal('사진을 올려주세요.')
                return false;
            }

            var formData = new FormData($("#gallery_form")[0]);



            $.ajax({
                url: "/gallery/upload",
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
                    if (data == '0') {
                        alert("회원 정보가 존재하지 않습니다.");
                    } else if (data == '1') {
                        alert("필수 입력값을 확인해주세요.");
                    } else if (data == '2') {
                    } else if (data == '3') {
                        alert("파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                    } else if (data == '4') {
                        alert("데이터 저장에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                    } else if (data == '5') {
                        // 업로드 성공
                        var url = window.location.href.split("/gallery/upload");
                        $(location).attr("href", url[0] + "/gallery");	// qna 게시판 목록으로 이동
                    } else if (data == '6') {
                    	alert("해쉬태그는 20개까지만 등록이 가능합니다.");
                    } else if(data == 'file_size_error'){
                    	alert("크기가 2MB미만인 파일만 첨부 가능합니다.");
                    } else if(data == 'no_upload') {
                    	alert("첨부된 파일이 없습니다.");
                    } else {
                        alert("잘못된 서버 응답입니다.");
                    }
                }, error: function () {
                    alert("ajax 통신 에러");
                }
            });
        });


        /*현재 입력칸의 숫자를 센다.*/
        function galleryTextCount(cssSelector) {
            $(cssSelector).on("keydown", function () {
                var currentText = $(this).val().length;
                var maxLength = $(this).next(".text_limit_count").attr('data-cs-maxlength');
                var contentName = $(this).prev(".info_input").attr('data-cs-title');
                $(this).next(".text_limit_count").text(currentText + " / " + maxLength);
                if (currentText > maxLength) {
                    generateModal(contentName + "은 " + maxLength + "자 이하만 가능합니다.");
                    return false;
                }
            }).on('blur', function () {
                var currentText = $(this).val().length;
                var maxLength = $(this).next(".text_limit_count").attr('data-cs-maxlength');
                var contentName = $(this).prev(".info_input").attr('data-cs-title');
                $(this).next(".text_limit_count").text(currentText + " / " + maxLength);
                if (currentText > maxLength) {
                    generateModal(contentName + "은 " + maxLength + "자 이하만 가능합니다.");
                    return false;
                }
            });
        }
    });//JQB
})();//iife