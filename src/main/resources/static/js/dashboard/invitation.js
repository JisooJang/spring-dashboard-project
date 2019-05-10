function inviteKaKaoTalkApi() {

    var emailModal = '<div class="mail-invite-form modal-form" data-modal-name="kakao-invitation-modal">\n' +
        '    <form action="/" method="post" enctype="multipart/form-data">\n' +
        '        <div class="mail-invite-form--title">\n' +
        '            <span class="kakao-icon"></span>\n' +
        '            <h2 class="modal-title" data-i18n="kakaoInvitation.title"></h2>\n' +
        '        </div>\n' +
        '        <div class="mail-invite-input-box">\n' +
        '            <div>\n' +
        '                <input type="tel" id="invite-kakao" placeholder="휴대폰 번호를 입력해주세요." data-i18n="[placeholder]kakaoInvitation.placeholder" maxlength="11"/>\n' +
        '                <div class="__filter"></div>\n' +
        '                <span class="__close-button"></span>\n' +
        '            </div>\n' +
        '        </div>\n' +
        '        <label for="invite-email" class="__validation-text" data-i18n="kakaoInvitation.warning"></label>\n' +
        '        <div class="mail-invite-buttons">\n' +
        '            <button type="button" role="button" class="__invite-cancel-button" data-modal-name="kakao-invitation-modal" data-i18n="kakaoInvitation.cancel">취소</button>\n' +
        '            <button type="button" role="button" class="__invite-accept-button" data-modal-name="kakao-invitation-modal" data-i18n="kakaoInvitation.confirm">초대하기</button>\n' +
        '        </div>\n' +
        '    </form>\n' +
        '</div>';


    $("body").append(emailModal);
    $(".mail-invite-form").innerCenter();
    defaultScreenBlock('kakao-invitation-modal');

    Localize(getCookie('lang'),$(".mail-invite-form"));


    /*카카오톡으로 가족초대*/
    var emailEle = $("#invite-kakao");
    var emailInitBtn = $(".mail-invite-input-box .__close-button");
    var sendInviteBtn = $(".mail-invite-buttons .__invite-accept-button");
    var emailReg = /^(01[016789]{1}|02|0[3-9]{1}[0-9]{1})([0-9]{3,4})([0-9]{4})$/;

    var validationText = $(".__validation-text");
    var validations = new Validations();
    emailEle.on('focus', function (e) {
        var phoneVal;
        $(this).on('keydown', function (e) {
            var k = e.keyCode;
            validationText.removeClass('active');

            phoneVal = e.target.value;
            var isValid = validations.onlyCheckNumber(phoneVal);

            if (isValid) {
                acceptOnlyNumber(e);
            } else if (!isValid) {
                removeChar(e);
            }

            /*숫자만 들어오게*/


            if ((k === 13 || k === 32 || k === 9 || k === 188)) {
                e.preventDefault();
                if (phoneVal.length < 1) {
                    validationText.addClass('active');
                    validationText.text('휴대폰 번호를 입력해주세요.');
                    return;
                }
                if (emailReg.test(phoneVal)) {

                    $.ajax({
                        url: "/dashboard/invite_family/kakao",
                        type: "POST",
                        data: {
                            phone: phoneVal
                        },
                        dataType: "text",
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                        },
                        success: function (data) {
                            if (data === 'success') {
                                generateModalInner('invitation-success-modal','초대 완료','성공적으로 초대하였습니다.',false);
                            } else if (data === 'phone_length_error') {
                                generateModalInner('invitation-fail-modal','실패','핸드폰번호를 정확히 입력해주세요.',false);
                                $("#invite-kakao").focus();
                            } else if (data === 'already_member_error') {
                                generateModalInner('invitation-fail-modal','실패','이미 가입된 계정입니다.',false);
                                $("#invite-kakao").focus();
                            } else if (data === 'group_size_error') {
                                generateModalInner('invitation-fail-modal','실패','그룹은 최대 5명까지 가입할 수 있습니다.',false);
                                $("#invite-kakao").focus();
                            } else {
                            	 generateModalInner('invitation-fail-modal','실패','잘못된 서버 통신입니다.',false);
                                 $("#invite-kakao").focus();
                            }
                        }, error: function (e) {
                            generateModalInner('invitation-fail-modal',e.status,'서버오류로 초대에 실패했습니다.<br/> 잠시후에 다시 시도해 주세요.',false);
                        }
                    });

                    validationText.removeClass('active');
                    $(this).val("");


                } else {
                    validationText.text('잘못된 입력입니다.');
                    validationText.addClass('active');
                }


            }
        });//key down
    }).on('keydown', function (e) {
        var targetVal = $(this).val();

        var isValid = validations.onlyCheckNumber(targetVal);

        if (isValid) {
            acceptOnlyNumber(e);
        } else if (!isValid) {
            removeChar(e);
        }

        if (targetVal.length > 0) {
            emailInitBtn.addClass('active')
        } else {
            emailInitBtn.removeClass('active');
        }
    }).on('keyup', function (e) {
        var targetVal = $(this).val();

        var isValid = validations.onlyCheckNumber(targetVal);

        if (isValid) {
            acceptOnlyNumber(e);
        } else if (!isValid) {
            removeChar(e);
        }

        if (targetVal.length > 0) {
            emailInitBtn.addClass('active')
        } else {
            emailInitBtn.removeClass('active');
        }
    }).on('blur',function(e){
        var targetVal = $(this).val();

        var isValid = validations.onlyCheckNumber(targetVal);

        if (isValid) {
            acceptOnlyNumber(e);
        } else if (!isValid) {
            removeChar(e);
        }

    });


    //현재 입력 글 초기화버튼(x) 클릭시
    emailInitBtn.on('click', function (e) {
        emailEle.val('');
        $(this).removeClass('active');
        emailEle.focus();
    });

    /*카카오톡 -> 초대하기 버튼 클릭시*/
    sendInviteBtn.on('click', function (e) {
        var phoneVal = $('#invite-kakao').val();

        var isValid = emailReg.test(phoneVal);

        /*현재 입력된 값이 없다면*/
        if (isValid) {
            /*카카오톡 초대 ajax통신*/
            $.ajax({
                url: "/dashboard/invite_family/kakao",
                type: "POST",
                data: {
                    phone: phoneVal
                },
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'success') {
                        generateModalInner('invitation-success-modal','초대 완료','성공적으로 초대하였습니다.',false);
                    } else if (data === 'phone_length_error') {
                        generateModalInner('invitation-fail-modal','실패','핸드폰번호를 정확히 입력해주세요.',false);
                        $("#invite-kakao").focus();
                    } else if (data === 'already_member_error') {
                        generateModalInner('invitation-fail-modal','실패','이미 가입된 계정입니다.',false);
                        $("#invite-kakao").focus();
                    } else if (data === 'group_size_error') {
                        generateModalInner('invitation-fail-modal','실패','그룹은 최대 5명까지 가입할 수 있습니다.',false);
                        $("#invite-kakao").focus();
                    }
                }, error: function (e) {
                    generateModalInner('invitation-fail-modal',e.status,'서버오류로 초대에 실패했습니다.<br/> 잠시후에 다시 시도해 주세요.',false);
                }
            });//end ajax
        } else if (phoneVal.length < 1) {
            validationText.text('전화번호를 입력해주세요.');
            validationText.addClass('active');
        } else if (!isValid) {
            validationText.text('전화번호 형식을 확인해주세요.');
            validationText.addClass('active');
        }
    });


    /*취소 버튼 클릭시*/
    $(".__invite-cancel-button").on('click', function (e) {
        var modalName = $(this).attr('data-modal-name');
        $('div[data-modal-name=' + modalName + ']').remove();
        $(this).off('click', null);
    });
    
    
    /* 그룹장만 보이는 그룹원 삭제 기능 */
    $(document).on("click", ".__unscribe-group-member", function(e) {
    	alert("aaa");
    });
}