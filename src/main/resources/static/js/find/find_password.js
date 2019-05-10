(function () {
    $(function () {
        var validations = new Validations();
        var passwordFindBtn = $(".__find-client-password-button");
        var passwordChangeBtn = $(".__password-change-button");
        var requestClientEmail = $("input[name='pwfind_email']");
        var warningBox = $(".client-join-section--form--warning em");
        var ajaxReady = true;

        inputFilled($("input"));
        removeInputVal($(".__remove-input-button"));

        $("#email-input-component").on('keydown keyup', function (e) {
            if (e.keyCode === 13) {
                $(".__find-client-password-button").focus();
            }
        });

        passwordFindBtn.on("click", function (e) {
            e.preventDefault();

            var clientVal = requestClientEmail.val();
            /*정규식 검사*/
            if (isEmpty(clientVal) || validations.checkEmail(clientVal) === false) {
                warningBox.text('이메일 주소를 다시 확인하세요.');
                return;
            }

            if (ajaxReady === true) {

                $.ajax({
                    type: "POST",
                    url: "/find_password",
                    data: {
                        pwfind_email: clientVal
                    },
                    dataType: "text",
                    beforeSend: function (xhr) {
                        ajaxReady = false;
                        sendCsrfToken(xhr);
                        warningBox.text('');
                    },
                    success: function (data) {
                        if (data === "success") {
                            warningBox.text('');
                            generateModal("find-password-modal", "비밀번호 찾기", "입력하신 이메일로 비밀번호 변경 URL을 전송하였습니다.", false);
                        } else {
                            warningBox.text('리틀원의 가입한 이메일이 아닙니다.');
                        }
                    },
                    complete: function () {
                        ajaxReady = true;
                    },
                    error: function () {
                        generateModal("ajax-connect-error", e.status, '통신에러가 발생했습니다', false);
                    }
                });//end ajax
            } //end if

        });// end of click


        /*비밀번호 찾기 -> 휴대폰 인증후 -> 비밀번호 변경 */
        passwordChangeBtn.on('click', function (e) {
            e.preventDefault();
            var password = $("input[name='new_password']");
            var rePassword = $("input[name='new_password_config']");

            var validations = new Validations();
            var isValidPass = validations.checkPassword(password) && !isEmpty(password.val());
            var isValidRepass = validations.checkPassword(rePassword) && !isEmpty(rePassword.val());
            var isSameVal = password.val() === rePassword.val();
            if (isValidPass && isValidRepass && isSameVal) {
                $.ajax({
                    type: "POST",
                    url: "/find_password/success",
                    data: {
                        new_password: password.val()
                    },
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        console.log('통신 성공: ');
                        console.log(data);
                        if (data === 'success') {
                            generateModal("sign-up-validate-success", "성공", '비밀번호 변경이 완료되었습니다.', true, function () {
                                window.location.replace('/login');
                            }, true, true);
                        } else if (data === 'db_error') {
                            generateModal("success-data-modal", "실패", '서버 오류입니다. 잠시 후에 다시 시도해주세요.', false);
                        } else if (data === 'no_session_error') {
                            generateModal("success-data-modal", "실패", '잘못된 요청입니다.', false);
                        } else if (data === 'password_validation_error') {
                            generateModal("success-data-modal", "실패", '비밀번호는 영문, 숫자, 특수문자 조합으로 8~20자로 설정해주세요. 사용가능한 특수문자 : !,@,#,$,%,^,&,*,?,_,~', false);
                        } else {
                            generateModal("success-data-modal", "실패", '잘못된 요청입니다.', false);
                        }

                    },
                    complete: function () {
                    },
                    error: function () {
                        generateModal("ajax-connect-error", e.status, '통신에러가 발생했습니다', false);
                    }
                })
            } else {
                generateModal('valida-error-modal', '에러', '비밀번호를 정확히 입력해주세요.', false);
            }
        });
    });
})();