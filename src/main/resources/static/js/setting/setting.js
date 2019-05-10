(function () {
    $(function () {
        var ajaxReady = true;
        /*최초 ajax통신시 알림 메뉴 가져옴*/
        $.ajax({
            type: 'get',
            url: '/mypage/setting/measure',
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                $("#option-container").html(data);
            },
            complete: function () {

            }
        });//ajax


        /*옵션 스위치 클릭 이벤트트*/
        $(document).on('click', '.__switch', function (e) {
            e.preventDefault();
            var target = $("input[type='hidden']");

            $(this).toggleClass('active');
            var isValid = $(this).hasClass('active');

            if (isValid) {

                $(this).find(target).val('true');

            } else {

                $(this).find(target).val('false');

            }

            var optionVal = $(this).find(target).val();
            var optionName = $(this).find(target).attr('name');

            if (optionName === 'option-email-subscribe' && optionVal === true) {
                alert("메일 구독");
            }

            console.log(optionVal, optionName);
            if (ajaxReady === true) {
                $.ajax({
                    type: 'POST',
                    url: "/mypage/setting/switch",
                    data: {
                        optionName: optionName,
                        optionVal: optionVal
                    },
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        if (data === 'email_success') {
                            
                        }
                    },
                    complete: function () {
                        ajaxReady = true;

                    },
                    error: function (e) {
                        console.log(e.status);
                    }
                });
            } else {
                alert('이전 작업이 진행중입니다.');
            }


        });// end click

        /*단위 선택 버튼*/
        $(document).on('click', "#measure-option li", function (e) {
            $(this).siblings().removeClass('active');
            $(this).addClass('active');

            var optionVal = $(this).find('input').val();
            if (ajaxReady === true) {

                $.ajax({
                    url: "/mypage/setting/measure/" + optionVal,
                    type: "GET",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        if (data === 'unit_failed') {
                            alert("잘못된 접근입니다.");
                        }
                    },
                    complete: function () {
                        ajaxReady = true;
                    },
                    error: function (e) {
                        console.log(e.status);
                    }
                });
            }
        });//end click


        /*좌측 메뉴 클릭*/
        $(".web-setting-section--container--menu a").on('click', function (e) {
            var isActive = $(this).parent().hasClass('active');
            if (isActive) {
                return;
            }
            var Url = $(this).attr('data-link');
            $(this).parent().addClass('active');
            $(this).parent().siblings().removeClass('active');
            $.ajax({
                type: 'get',
                url: '/mypage/setting/' + Url,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    $("#option-container").html(data);

                }
            });
        });

        $(document).on('keyup', 'input[name="withdrawal-password"]', function (e) {
            var len = $(this).val().length;
            var isActive = $(".__leaving-submit-button").hasClass('active');
            if (len > 0 && !isActive) {
                $(".__leaving-submit-button").addClass('active');
            }
            if (len < 1) {
                $(".__leaving-submit-button").removeClass('active');

            }

        });

        /* 일반 회원 탈퇴*/
        $(document).on("click", ".__leaving-submit-button", function (e) {
            e.preventDefault();
            var isValid = $(this).hasClass('active');

            var formData = new FormData();
            formData.append('password', $("input[name='withdrawal-password']").val());

            if (isValid) {
                $.ajax({
                    type: "POST",
                    url: "/mypage/member_leave",
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === "success") {
                            generateModal('fail-modal', '성공', '회원 탈퇴가 정상적으로 완료되었습니다.', true, function () {
                                window.location.replace('/');
                            }, true, true);
                        } else if (data === 'auth_failed') {
                            generateModal('fail-modal', '실패', '비밀번호가 일치하지 않습니다.', false);

                            $("input[name='withdrawal-password']").val('');

                        }
                    },
                    error: function (e) {
                        generateModal('fail-modal', e.status, '잘못된 통신입니다. 잠시 후 다시 시도해주세요.');
                    }
                });//ajax
            } else {
                generateModal('fail-modal', '실패', '회원 탈퇴를 위해서는 비밀번호가 필요합니다.');

            } //end else if~

        });//click

        $(document).on("click", ".sns_leave_mail_btn", function (e) {
            e.preventDefault();
            $.ajax({
                type: "GET",
                url: "/mypage/member_leave/social",
                processData: false,
                contentType: false,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === "success") {
                        generateModal('fail-modal', '성공', '회원 탈퇴 인증 메일이 성공적으로 발송되었습니다.');
                    } else {
                        generateModal('fail-modal', '실패', '잘못된 통신입니다. 잠시 후 다시 시도해주세요.');
                    }
                },
                error: function (e) {
                    generateModal('fail-modal', e.status, '잘못된 통신입니다. 잠시 후 다시 시도해주세요.');
                }
            });//ajax
        });
    });
})();