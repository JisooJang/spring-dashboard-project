(function () {
    $(function () {

        /*육아일기 */
        $(document).on("click", ".babybook--contents--diary--info", function (e) {
            $(this).toggleClass('active');
            if ($(this).hasClass('active')) {
                $('.babybook--contents--diary--info__event-type>span', this).stop().fadeOut();
            } else {
                $('.babybook--contents--diary--info__event-type>span', this).stop().fadeIn();
            }
        });


        $(document).on("touchstart", ".babybook--contents--no-schedule", function (e) {
            $('h2', this).text("일정을 추가합니다.");
        });//on click touch start
        /* 모바일 : 터치 끝날 때*/
        $(document).on("touchend", ".babybook--contents--no-schedule", function (e) {
            $('h2', this).text("일정 없음");
        });//on click touch start
        $(document).on("touchstart", ".babybook--contents--no-diary__desc", function (e) {
            $('h2', this).text("육아일기를 추가합니다.");
        });//on click touch start
        /* 모바일 : 터치 끝날 때*/
        $(document).on("touchend", ".babybook--contents--no-diary__desc", function (e) {
            $('h2', this).text("육아일기 없음");
        });//on click touch start

        /*일정없음 클릭 했을 경우에 modal창 생성*/
        $(document).on('click', ".babybook--contents--no-schedule", function (e) {
            writeScheduleModal();
        });
        /*육아일기 없음 클릭했을 경우 육아일기 쓰는 modal창 생성*/
        $(document).on('click', ".babybook--contents--no-diary__desc", function (e) {
            writeBabybookModal();
        });

        /*widget에 육아일기 작성 버튼 클릭시*/
        $(".dashboard--list--widget__babybook-button").on('click', function (e) {
            e.preventDefault();
            writeBabybookModal();
        });

        /*육아일기 수정 버튼 클릭 메서드*/
        $(document).on('click touchend', "button.babybook--contents--diary__modify__update", function (e) {
            updateBabybookModal(this);
        });// 육아일기 수정 버튼 클릭 메서드

        /*육아일기 삭제 버튼 클릭 메서드*/
        $(document).on("click", "button.babybook--contents--diary__modify__delete", function (e) {
            var diary_idx = $(this).parent().attr("data-idx");
            var result = confirm("정말 삭제하시겠습니까?");
            if (result) {
                $.ajax({
                    url: "/dashboard/delete_diary/" + diary_idx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data == 'success') {
                            alert("다이어리 삭제를 완료하였습니다.");
                            location.reload();
                        } else if (data == 'no_authority') {
                            alert("삭제 권한이 없습니다.");
                        } else {
                            alert("잘못된 통신입니다.");
                        }
                    },
                    error: function () {
                        alert("ajax 통신 에러");
                    }
                });
            }
        }); // 육아일기 삭제 통신

        /*육아일기 작성 버튼 클릭 메서드*/
        $(document).on('click', ".babybook--contents--first-diary button", function (e) {
            writeBabybookModal(this);
        });


        /*육아 일정추가 버튼 클릭 메서드*/
        $(document).on('click', '.dashboard--list--widget__schedule-button', function (e) {
            e.preventDefault();
            writeScheduleModal();
        });

        /*육아 일정 수정 버튼 클릭 메서드*/
        $(document).on("click", ".babybook--contents--schedule__modify__update", function (e) {
            e.preventDefault();
            updateScheduleModal(e.target);
        });


        /*육아 일정 삭제 메서드*/
        $(document).on("click touchend", ".babybook--contents--schedule__modify__delete", function (e) {
            e.preventDefault();
            var schedule_idx = $(this).parent().attr("data-idx");
            generateModal("정말 삭제하시겠습니까?", true, function () {

                $.ajax({
                    url: "/dashboard/delete_schedule/" + schedule_idx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === 'no_authority') {
                            generateModal("삭제 권한이 없습니다.");
                        } else if (data === 'success') {
                            generateModal("삭제가 완료되었습니다.");
                            setTimeout(function () {
                                location.reload();
                            }, 650)
                        } else if (data === 'failed') {
                            generateModal("서버 통신 오류입니다. 잠시 후에 다시 시도해주세요.");
                        }
                    },
                    error: function () {
                        generateModal("ajax 통신 에러");
                    }
                });//AJAX


            });
            ;

        });	// 육아 일정 삭제 메서드

    });//JQB
})();//iife