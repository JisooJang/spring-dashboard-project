/*
* 기능 : 육아일기 작성 MODAL창을 동적으로 생성합니다.
* 작성자 : 김창현
* 날짜 : 2018-07-20 (금)
* */

// csrf 토큰 정보를 변수에 담음
var csrf_name = $("#csrf_token").attr("name");
var csrf_value = $("#csrf_token").val();


function writeBabybookModal() {
    /*동적으로 삽입될 태그*/

    // 육아일기 작성 모달
    var tag = '<div class="base-modal-form modal-form">\n' +
        '    <div>\n' +
        '        <form action="#" method="post" id="babybook_write_form" enctype="multipart/form-data">\n' +
        '            <fieldset>\n' +
        '                <legend>육아일기 작성 폼</legend>\n' +
        '                <div class="base-modal-form--container align_c" style="margin-bottom:25px">\n' +
        '                    <img class="base-modal-form--container__thumb type-b" src="/images/dashboard/baby/아기8.jpg"/>\n' +
        '                </div>\n' +
        '                <div class="base-modal-form--container">\n' +
        '                    <div class="file-container">\n' +
        '                        <div class="file-container--box">\n' +
        '                            <input class="file-container--box__input-upload" value="이미지 파일을 업로드해주세요." disabled="disabled">\n' +
        '                            <label for="input-file-upload">이미지 업로드</label>\n' +
        '                            <input type="file"  class="file-container--box__input-upload__hidden" id="input-file-upload" name="babybook_image" accept=".jpg, .png, .jpeg, .gif">\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '                <div class="base-modal-form--container">\n' +
        '                   <select name="public_check">\n' +
        '                       <optgroup label="공개범위">\n' +
        '                           <option value="public" selected="selected">전체공개</option>\n' +
        '                           <option value="group">그룹공개</option>\n' +
        '                           <option value="private">비공개</option>\n' +
        '                       </optgroup>\n' +
        '                   </select>\n' +
        '                </div>\n' +
        '                <div class="base-modal-form--container">\n' +
        '                    <label for="base-modal-form--calendar">날짜 선택</label>\n' +
        '                    <input type="text" class="babybook--modal__picker" name="event_date" id="base-modal-form--calendar" value=""/>\n' +
        '                </div>\n' +
        '                <div class="base-modal-form--container">\n' +
        '                    <label for="base-modal-form--name">제목</label>\n' +
        '                    <input type="text" name="title" id="base-modal-form--name" maxlength="100"/>\n' +
        '                </div>\n' +
        '                <div class="base-modal-form--container">\n' +
        '                    <label for="base-modal-form--contents">내용</label>\n' +
        '                    <input type="text" name="update_contents" id="base-modal-form--contents" maxlength="1000"/>\n' +
        '                </div>\n' +
        '                <br>\n' +
        '                <button type="submit">작성 완료</button>\n' +
        '                <input type="hidden" name=' + csrf_name + ' value=' + csrf_value + '/>\n' +
        '            </fieldset>\n' +
        '        </form>\n' +
        '    </div>\n' +
        '</div>'

    /*이미지 업로드시 동적 변경*/
    imageUploading('#input-file-upload', '.base-modal-form--container__thumb.type-b', '.file-container--box__input-upload', 3000000);

    /*바디에 앞에 넣는다.*/
    $('body').prepend(tag);

    $('.babybook--modal__picker').datepicker({
        language: 'kr',
        autoClose: true,
        onSelect: function onSelect(fd, date) {
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var day = date.getDate() < 10 ? "0" + (date.getDate()) : date.getDate();
            var dateVal = [date.getFullYear(), month, day].join('-');

            $('input[name="event_date"]').val(dateVal);
        }//onSelect
    });


    /*가운데에 정렬*/
    $(".modal-form").innerCenter();

    /*스크린 블락 생성*/
    generateScreenBlock();

    var inputEle = $(".base-modal-form--container input");

    inputEle.on("focus", function (e) {
        /*빈 input에 왔을 경우 active 클래스 주기*/
        var isLen = $(e.target).val().length > 0;

        console.log(isLen);
        if (isLen) {
            return;
        }
        if (!isLen) {
            $(e.target).prev('label').addClass('active');
            $(e.target).parent().stop().animate({
                margin: '24px auto'
            }, 200);
        }


    }).on('blur', function (e) {
        var isLen = $(e.target).val().length > 0;
        console.log(isLen);
        var target = e.target;
        if (isLen) {
            return;
        }
        if (!isLen) {
            $(target).parent().stop().animate({
                margin: '8px auto'
            }, 200);
            $(target).prev('label').removeClass('active');
        }


    });


   /* submit 버튼 클릭시 서버와 AJAX통신을 통해 수정된 값을 통신
    var ajaxReady = true;
    ajaxReady가 true일 때만 서버와 통신
    $('.babybook--modal__update button[type="submit"]').on('click', function (e) {
        e.preventDefault();
        var requester_idx = $("#session_idx").val();
        var formData = new FormData($("#babybook_write_form")[0]);

        if (ajaxReady === true) {
            $.ajax({
                url: "/dashboard/babyBook/write",
                type: "POST",
                enctype: "multipart/form-data",
                data: formData,
                dataType: "text",
                processData: false,		// 중요. 데이터를 쿼리 문자열로 변환하는 jQuery 형식을 방지
                contentType: false,
                async: true,	// 비동기
                dataType: "text",
                beforeSend: function () {
                    ajaxReady = false;
                },
                success: function (data) {
                    if (data == 'db') {
                        alert("데이터 입력 오류입니다. 잠시 후 다시 시도해주세요.");
                    } else if (data == 'success') {
                        alert("글 작성이 완료되었습니다.");
                        $(".babybook--modal").remove();
                        // 세션유저가 그룹에 속해있으면 소켓 listen
                        // 아래 소켓 연결에서 recipient_idx는 유저가 그룹에 속했으면, 해당 그룹에 속한 개개인 idx로 다 뿌려줘야함.
                        if (typeof $("input[name='family_group_idx']").val() != "undefined") {
                            var group_member_idx = $("input[name='family_group_idx']").val();	// 그룹 멤버 idx값을 전부 가져옴
                            var group_idx_array = new Array(group_member_idx.length);	// 배열로 변환
                            var requester_idx = $("input[name='session_idx']").val();

                            for (i = 0; i < group_idx_array.length; i++) {
                                if (requester_idx === group_idx_array[i]) {
                                    continue;
                                }
                                group_request_added(requester_idx, group_idx_array[i]);
                            }
                        }

                    }

                    모달창 삭제
                    $(".modal-form").remove();
                    $(".screen_block").remove();
                    

                },
                error: function (e) {
                    generateModal(e.status + " : ajax 통신 에러");
                },
                complete: function () {
                    ajaxReady = true;
                }
            });//ajax
        } else {
            generateModal('이전 작업이 진행중입니다');
        }
    });//버튼 클릭 메서드
*/

}// writeBabybookModal 함수


/*
* 기능 : 육아일기를 수정하는 MODAL창을 동적으로 생성합니다.
* 작성자 : 김창현
* 날짜 : 2018-07-20 (금)
* */
function updateBabybookModal(cssSelector) {
    /*수정할 타겟이 data-idx값*/
    var dataIdx = $(cssSelector).parent().parent().attr('data-diary-idx');
    var ImgIdx = $(cssSelector).parent().nextAll(".babybook--contents--diary--info").children("img").attr("data-img-idx");
    /*기존의 이미지 */
    var originImage = $('[data-img-idx=' + ImgIdx + ']').attr('src');
    /*기존의 제목*/
    var originTitle = $(cssSelector).parent().nextAll(".babybook--contents--diary--desc").children("div").children("h1").text();
    /*기존의 내용*/
    var originContents = $('[data-diary-idx=' + dataIdx + ']').find('p').text();
    //$('[data-diary-idx=' + dataIdx + ']').find('p').text();

    /*기존의 날짜*/
    var originDate = $('[data-diary-idx=' + dataIdx + ']').find('time').text();
    var originFileName = $("img[data-img-idx='" + dataIdx + "']").next().val();

    /*동적으로 삽입될 태그*/
    var tag = '<div class="babybook--modal modal-form">\n' +
        '            <div class="babybook--modal__update">\n' +
        '                <form action="#" method="post" id="babybook_modify_form" enctype="multipart/form-data">\n' +
        '                    <fieldset>\n' +
        '                        <legend>일정 수정하기</legend>\n' +
        '                        <div class="babybook--modal__update__picker">\n' +
        '                            <div class="babybook--modal__update__container">\n' +
        '                                <label for="update__date">날짜</label>\n' +
        '                                <input type="text" name="update_date" id="update__date" value=' + originDate + '>\n' +
        '                            </div>\n' +
        '                        </div>\n' +
        '                        <div class="babybook--modal__update__container">\n' +
        '                            <div class="babybook--modal__update__container--image">\n' +
        '                                <img src=' + originImage + ' alt="asd"/>\n' +
        '                                <div id="container_filebox">\n' +
        '                                    <div class="filebox dp_ib">\n' +
        '                                        <div id="uploads"></div>\n' +
        '                                        <input name="update_image" class="upload-name" value="' + originFileName + '" disabled="disabled">\n' +
        '                                        <label for="babyboook--image__upload">재업로드</label>\n' +
        '                                        <input type="file" id="babyboook--image__upload" name="update_file" class="upload-hidden" accept=".jpg, .png, .jpeg, .gif">\n' +
        '                                    </div>\n' +
        '                                </div>\n' +
        '                            </div>\n' +
        '                            <div>\n' +
        '                                <select name="public_check">\n' +
        '                                    <optgroup label="공개범위">\n' +
        '                                        <option value="public" selected="selected">전체공개</option>\n' +
        '                                        <option value="group">그룹공개</option>\n' +
        '                                        <option value="private">비공개</option>\n' +
        '                                    </optgroup>\n' +
        '                                </select>\n' +
        '                                <input type="text" name="update_title" value=' + originTitle + '>\n' +
        '                            </div>' +
        '                            <div>\n' +
        '                                <label for="update_contents">내용</label>\n' +
        '                                <input type="text" id="update_contents" name="update_contents" value=' + originContents + '>\n' +
        '                            </div>\n' +
        '                            <button type="submit">수정완료</button>\n' +
        '                        </div>\n' +
        '                    </fieldset>\n' +
        '					<input type="hidden" name="' + csrf_name + '" value="' + csrf_value + '"/>' +
        '                </form>\n' +
        '            </div>\n' +
        '        </div>';

    /*이미지 업로드시 동적 변경*/
    imageUploading('#babyboook--image__upload', '.babybook--modal__update__container--image>img', '.upload-name', 3000000);

    /*바디에 앞에 넣는다.*/
    $('body').prepend(tag);
    /*update의 데이트픽커 정의*/
    $('.babybook--modal__update__picker').datepicker({
        language: 'kr',
        onSelect: function onSelect(fd, date) {
            var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
            var day = date.getDate() < 10 ? "0" + (date.getDate()) : date.getDate();
            var dateVal = [date.getFullYear(), month, day].join('-');

            $('input[name="update_date"]').val(dateVal);

        }//onSelect
    });
    /*가운데에 정렬*/
    $(".modal-form").innerCenter();
    /*스크린 블락 생성*/
    generateScreenBlock();

    /*submit 버튼 클릭시 서버와 AJAX통신을 통해 수정된 값을 통신*/
    var ajaxReady = true;
    /*ajaxReady가 true일 때만 서버와 통신*/
    $('.babybook--modal__update button[type="submit"]').on('click', function (e) {
        e.preventDefault();
        if (ajaxReady === true) {
            var formData = new FormData($("#babybook_modify_form")[0]);
            $.ajax({
                url: "/dashboard/modify_diary/" + dataIdx,
                type: "POST",
                enctype: "multipart/form-data",
                data: formData,
                dataType: "text",
                processData: false,		// 중요. 데이터를 쿼리 문자열로 변환하는 jQuery 형식을 방지
                contentType: false,
                async: true,	// 비동기
                beforeSend: function () {
                    ajaxReady = false;
                },
                success: function (data) {
                    if (data == 'success') {
                        alert("수정이 완료되었습니다.");
                        location.reload();
                    } else if (data == 'db_error') {
                        alert("데이터 수정 오류입니다. 잠시 후 다시 시도해주세요.");
                    } else if (data == 'io_error') {
                        alert("파일 업로드 오류입니다. 잠시 후 다시 시도해주세요.");
                    } else {
                        alert("잘못된 서버 통신입니다.");
                    }
                    /*모달창 삭제*/
                    $(".modal-form").remove();
                    $(".screen_block").remove();
                    /**/
                },
                error: function (e) {
                    generateModal(e.status + " : ajax 통신 에러");
                },
                complete: function () {
                    ajaxReady = true;
                }
            });//ajax
        } else {
            generateModal('이전 작업이 진행중입니다');
        }

    });// 다이어리 수정 버튼 클릭 메서드


}// updateBabybookModal 함수


/*육아일정 작성하는 메서드*/
function writeScheduleModal() {

    var infant_idx_val = $("#infant_idx").val();

    var tag = '<div class="schedule-form modal-form">\n' +
        '    <div>\n' +
        '        <form action="#" method="post" id="babybook_write_form" enctype="multipart/form-data">\n' +
        '            <fieldset>\n' +
        '                <legend>일정 입력하는 폼</legend>\n' +
        '                <div class="schedule-form--container">\n' +
        '                    <label for="schedule-form--title">제목</label>\n' +
        '                    <input type="text" name="title" id="schedule-form--title"/>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container">\n' +
        '                    <label for="schedule-form--contents">일정내용</label>\n' +
        '                    <input type="text" name="content" id="schedule-form--contents"/>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container">\n' +
        '                    <label for="schedule-form--location">장소</label>\n' +
        '                    <input type="text" name="location" id="schedule-form--location"/>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container -b">\n' +
        '                    <div>\n' +
        '                        <label for="schedule-form--calendar-a">시작</label>\n' +
        '                        <input type="text" name="start_date" class="schedule-calendar" id="schedule-form--calendar-a"\n' +
        '                               readonly="readonly" data-multiple-dates-separator=" - "\n />\n' +
        '                    </div>\n' +
        '                    <div>\n' +
        '                        <label for="schedule-form--calendar-b">종료</label>\n' +
        '                        <input type="text" name="end_date" class="schedule-calendar" id="schedule-form--calendar-b"\n' +
        '                               readonly="readonly"     data-multiple-dates-separator=" - "\n/>\n' +
        '                    </div>\n' +
        '                    <div>\n' +
        '                        <label for="schedule-form--repeat">반복</label>\n' +
        '                        <input type="text" data-modal-name="매일,매주,매달,매년,평일,주말" name="repeat" id="schedule-form--repeat"\n' +
        '                               value="안함" readonly="readonly" style="text-align: right"/>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container radio-box -b">\n' +
        '                    <!-- <input type="radio" name="public_check" id="radio-box__public" value="public" checked="checked"/><label for="radio-box__public">전체공개</label>\n -->' +
        '                    <input type="radio" name="public_check" id="radio-box__group" value="public"/><label for="radio-box__group">그룹공개</label>\n' +
        '                    <input type="radio" name="public_check" id="radio-box__private" value="private"/><label for="radio-box__private">비공개</label>\n' +
        '                </div>\n' +
        '                <br>\n' +
        '                <button type="submit">일정 등록</button>\n' +
        '					<input type="hidden" name="' + csrf_name + '" value="' + csrf_value + '"/>' +
        '            </fieldset>\n' +
        '        </form>\n' +
        '    </div>\n' +
        '</div>'; //end tag

    $('body').prepend(tag);
    var $picker = $('.schedule-calendar');


    $picker.datepicker({
        language: 'kr',
        timepicker: true,
        autoClose: true,
        dateFormat: 'yyyy-mm-dd'
    });//picker

    /*스크린 블락 생성*/
    generateScreenBlock();
    /*포크스 & 블러시 label위치 이동*/


    $(".modal-form").innerCenter();

    /*innerMOdal생성*/
    $(document).on("click", "#schedule-form--repeat", function (e) {
        generateInnerModal("#schedule-form--repeat", "반복 없음");

    });//click

    $(".schedule-form--container input").on("focus", function (e) {
        /*빈 input에 왔을 경우 active 클래스 주기*/
        var isLen = $(e.target).val().length > 0;

        console.log(isLen);
        if (isLen) {
            return;
        }
        if (!isLen) {
            $(e.target).prev('label').addClass('active');
            $(e.target).parent().stop().animate({
                margin: '24px auto'
            }, 200);
        }


    }).on('blur', function (e) {
        var isLen = $(e.target).val().length > 0;
        console.log(isLen);
        var target = e.target;
        if (isLen) {
            return;
        }
        if (!isLen) {
            $(target).parent().stop().animate({
                margin: '8px auto'
            }, 200);
            $(target).prev('label').removeClass('active');
        }


    });


    //  육아 일정표 작성 완료 ajax통신 메서드
    var ajaxReady = true;
    $(document).on("click", ".schedule-form button[type=submit]", function (e) {
        e.preventDefault();
        if (ajaxReady === true) {
            $.ajax({
                url: "/dashboard/add_schedule",
                type: "POST",
                data: {
                    title: $("input[name='title']").val(),
                    content: $("input[name='content']").val(),
                    place: $("input[name='location']").val(),
                    location: $("input[name='location']").val(),
                    start_date: $("input[name='start_date']").val(),
                    end_date: $("input[name='end_date']").val(),
                    public_check: $("input[name='public_check']").val(),
                    repeat: $("input[name='repeat']").val(),
                    infant_idx: $("#infant_idx").val()
                },
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                    ajaxReady = false;
                },
                success: function (data) {
                    if (data === 'success') {
                        generateModal("육아 일정작성이 완료되었습니다.");
                        $(".modal-form").remove();
                        $(".screen_block").remove();
                        // 그룹에 알림전송
                        if (typeof $("input[name='family_group_idx']").val() != "undefined") {	// 그룹이 존재할시
                            var group_member_idx = $("input[name='family_group_idx']").val();
                            var group_idx_array = new Array(group_member_idx.length);
                            var requester_idx = $("input[name='session_idx']").val();

                            console.log("일정 추가 알림 전송 :" + group_member_idx, group_idx_array);
                            var i = 0;
                            for (i = 0; i < group_member_idx.length; i++) {
                                group_idx_array[i] = $("input[name='family_group_idx']")[i].value;
                                if ($("#session_idx").val() === group_idx_array[i]) {
                                    continue;
                                }
                                group_request_added(requester_idx, group_idx_array[i]);

                            }
                        }

                    } else if (data === 'db_error') {
                        generateModal("서버 오류로 인해 일정 작성에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                    } else {
                        generateModal("잘못된 서버 통신입니다.");
                    }
                },
                error: function () {
                    generateModal("ajax 통신 에러");
                },
                complete: function () {
                    ajaxReady = true;
                }
            });//ajax
        }

    });//end method click


}//함수


/*육아일정 수정하는 함수*/
function updateScheduleModal(cssSelector) {

    /*기존에 생성된 모달창 제거*/
    $(".modal-form").remove();
    $(".screen_block").remove();

    /*통신하려는 대상의 일정의 idx값*/
    var schedule_idx = $(cssSelector).parent().attr("data-idx");

    /*기존 값들 가져오기*/
    var originValue = $('li[data-schedule-idx=' + schedule_idx + ']');
    /*기존 제목*/
    var originTitle = originValue.find('.babybook--contents--schedule--desc__info--reserve h2').text() || null;
    /*기존 내용*/
    var originContents = originValue.find('.babybook--contents--schedule--desc__info--reserve p').text() || null;
    /*기존 시작날짜*/
    var originStartDate = originValue.find('.babybook--contents--schedule--desc__time>time').text() || null;
    /*기존 종료날짜*/
    var originEndDate = originValue.find('.babybook--contents--schedule--desc__time>time').text() || null;
    /*반복상태*/
    var orginRepeat = '' || null;
    /*기존 일정 장소*/
    var originLocation = originValue.find('.babybook--contents--schedule--desc__info--reserve .babybook_schedule_place').val() || null;
    /*기존 공개여부*/
    var originSelectedOptopm = originValue.find('.babybook--contents--schedule--desc__info--reserve .babybook_schedule_publicCheck').val() || null;

    var check = "";
    if (originSelectedOptopm == '1') {
        check = '                    <input type="radio" name="public_check" id="radio-box__group" value="public" checked="true"/><label for="radio-box__group">그룹공개</label>\n' +
            '                    <input type="radio" name="public_check" id="radio-box__private" value="private"/><label for="radio-box__private">비공개</label>\n';
    } else if (originSelectedOptopm == '2') {
        check = '                    <input type="radio" name="public_check" id="radio-box__group" value="public"/><label for="radio-box__group">그룹공개</label>\n' +
            '                    <input type="radio" name="public_check" id="radio-box__private" value="private" checked="true"/><label for="radio-box__private">비공개</label>\n';
    }

    /*동적 삽입될 태그*/
    var tag = '<div class="schedule-form modal-form">\n' +
        '    <div>\n' +
        '        <form action="#" method="post" id="babybook_write_form" enctype="multipart/form-data">\n' +
        '            <fieldset>\n' +
        '                <legend>일정 수정하는 폼</legend>\n' +
        '                <div class="schedule-form--container" style="margin:24px auto">\n' +
        '                    <label for="schedule-form--title" class="active">제목</label>\n' +
        '                    <input type="text" name="update_title" id="schedule-form--title" value=' + originTitle + '>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container" style="margin:24px auto">\n' +
        '                    <label for="schedule-form--contents" class="active">일정내용</label>\n' +
        '                    <input type="text" name="update_content" id="schedule-form--contents" value=' + originContents + '>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container" style="margin:24px auto">\n' +
        '                    <label for="schedule-form--location" class="active">장소</label>\n' +
        '                    <input type="text" name="update_location" id="schedule-form--location" value=' + originLocation + '>\n' +
        '                </div>\n' +
        '                <div class="schedule-form--container -b">\n' +
        '                    <div style="margin:24px auto">\n' +
        '                        <label for="schedule-form--calendar-a" class="active">시작</label>\n' +
        '                        <input type="text" name="update_start_date" class="schedule-calendar" id="schedule-form--calendar-a"\n' +
        '                               readonly="readonly" value=' + originStartDate + '/>\n' +
        '                    </div>\n' +
        '                    <div style="margin:24px auto">\n' +
        '                        <label for="schedule-form--calendar-b" class="active">종료</label>\n' +
        '                        <input type="text" name="update_end_date" class="schedule-calendar" id="schedule-form--calendar-b"\n' +
        '                               readonly="readonly" value=' + originEndDate + '/>\n' +
        '                    </div>\n' +
        '                    <div>\n' +
        '                        <label for="schedule-form--repeat">반복</label>\n' +
        '                        <input type="text" data-modal-name="매일,매주,매달,매년,평일,주말" name="update_repeat" id="schedule-form--repeat"\n' +
        '                               value="안함" readonly="readonly" style="text-align: right" value=' + orginRepeat + '/>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '                <!--<div class="schedule-form&#45;&#45;container">-->\n' +
        '                    <!--<label for="schedule-form&#45;&#45;alarm">알림</label>-->\n' +
        '                    <!--<input type="text" data-modal-name="웹사이트 알림,알림톡 받기,이메일 받기"   name="alarm" id="schedule-form&#45;&#45;alarm" value="없음" readonly="readonly"-->\n' +
        '                           <!--style="text-align: right"/>-->\n' +
        '                <!--</div>-->\n' +
        '                <div class="schedule-form--container radio-box -b">\n' +
        '                    <!--<input type="radio" name="public_check" id="radio-box__public" value="public" checked="checked"/><label for="radio-box__public">전체공개</label>\n -->' +
        '                    <!-- <input type="radio" name="public_check" id="radio-box__group" value="public"/><label for="radio-box__group">그룹공개</label>\n -->' +
        '                    <!--<input type="radio" name="public_check" id="radio-box__private" value="private"/><label for="radio-box__private">비공개</label>\n -->' + check +
        '                </div>\n' +
        '                <br>\n' +
        '                <button type="submit">일정 수정</button>\n' +
        '					<input type="hidden" name="' + csrf_name + '" value="' + csrf_value + '"/>' +
        '            </fieldset>\n' +
        '        </form>\n' +
        '    </div>\n' +
        '</div>'; //end tag

    /*body에 tag 붙여넣기*/
    $('body').prepend(tag);
    /*datepicker selector 변수에 핟당*/
    var $picker = $('.schedule-calendar');

    /*datepicker 실행*/
    $picker.datepicker({
        language: 'kr',
        timepicker: true,
        autoClose: true,
        dateFormat: 'yyyy-mm-dd'
    });//picker

    /*modalform 가운데 위치*/
    $(".modal-form").innerCenter();
    /*스크린 블락 생성*/
    generateScreenBlock();

    /*innerMOdal생성*/
    $(document).on("click", "#schedule-form--repeat", function (e) {
        generateInnerModal("#schedule-form--repeat", "반복 없음");

    });//click

    $(".schedule-form--container input").on("focus", function (e) {
        /*빈 input에 왔을 경우 active 클래스 주기*/
        var isLen = $(e.target).val().length > 0;

        console.log(isLen);
        if (isLen) {
            return;
        }
        if (!isLen) {
            $(e.target).prev('label').addClass('active');
            $(e.target).parent().stop().animate({
                margin: '24px auto'
            }, 200);
        }

    }).on('blur', function (e) {
        var isLen = $(e.target).val().length > 0;
        console.log(isLen);
        var target = e.target;
        if (isLen) {
            return;
        }
        if (!isLen) {
            $(target).parent().stop().animate({
                margin: '8px auto'
            }, 200);
            $(target).prev('label').removeClass('active');
        }

    });

    var ajaxReady = true;

    /*일정 수정 버튼 클릭*/
    $("#babybook_write_form button").on('click', function (e) {
        e.preventDefault();
        /*ajaxReady가 true일 때만 통신한다. success하면 다시 TRUE로 바꿔줘야 함. */
        if (ajaxReady === true) {
            $.ajax({
                url: "/dashboard/modify_schedule/" + schedule_idx,
                type: "POST",
                data: {
                    update_title: $("input[name='update_title']").val(),
                    update_contents: $("input[name='update_content']").val(),
                    update_location: $("input[name='update_location']").val(),
                    update_start_date: $("input[name='update_start_date']").val(),
                    update_end_date: $("input[name='update_end_date']").val(),
                    update_repeat: $("input[name='update_repeat']").val(),
                    update_public_check: $("input[name='update_public_check']").val()
                },
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                    ajaxReady = false;
                },
                success: function (data) {
                    //성공 완료 후에 기존 모달창 제거
                    if (data === 'no_authority') {
                        generateModal("수정 권한이 없습니다.");
                    } else if (data === 'success') {
                        generateModal("수정이 완료되었습니다.");
                        setTimeout(function () {
                            location.reload();
                        }, 600);
                    } else if (data === 'failed') {
                        generateModal("서버 통신 오류입니다. 잠시 후에 다시 시도해주세요.");
                    }
                    $(".babybook--modal").remove();
                    $(".screen_block").remove();
                },
                complete: function () {
                    //ajax통신이 종료시
                    ajaxReady = true;
                },
                error: function () {
                    alert("ajax 통신 에러");
                }

            })
        } else {
            return false;
        }
    });//FORM 일정 수정 버튼 클릭


}
