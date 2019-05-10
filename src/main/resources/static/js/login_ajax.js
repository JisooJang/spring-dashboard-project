/*
 * 2018-03-05
 * 김창현 : 아이디 중복 체크시 받은 값이 'duplicate'라면 submit block기능 추가
 * */
var token_val = "";
(function () {
    $(function () {

        /*captch에서 성공하면 주는 값이 null (!=)아니라면*/
        var loginForm = $("#littleone-login-form");
        var loginResultVal = $("#login_result").val();

        $(".login-section-form--submit .__login-button").on("click", function (e) {
            var loginResult = $("#login_result").val();
            e.preventDefault();
            if (loginResult !== null && loginResult !== 'btn_search_invite' && loginResult !== undefined && typeof loginResult !== "undefined" && loginResult === "count_failed") {
                loginForm.append("<input type='hidden' name='recaptcha-token' value='" + token_val + "'>");
            }
            loginForm.submit();
        });	// 로그인 버튼 클릭시 구글캡차 토큰 제어


        if (loginResultVal !== undefined) {
            var result = $("#login_result").val();
            if (result === "failed") {
                var err_count = $("#error_count").val();
                var warningText = $('<p>이메일 혹은 비밀번호가 맞지 않습니다. (실패 횟수 : ' + err_count + '회) </p>');
                $('.login-section-warning-text').append(warningText);
            } else if (result === "password_renew_required") {

            } else if (result === "count_failed") {
                var warningText = $('<p>로그인에 여러번 실패하셨습니다. 아래 로봇이 아님을 인증 후 다시 로그인을 시도하거나 비밀번호 찾기를 이용해주세요.</p>');
                $('.login-section-warning-text').append(warningText);
                
            } else if (result === "member_type_failed") {
                var warningText = $('<p>잘못된 접근입니다.</p>');
                $('.login-section-warning-text').append(warningText);
            } else if (result === "log_failed") {
                var warningText = $('<p>서버 오류입니다. 잠시 후에 다시 시도해주세요.</p>');
                $('.login-section-warning-text').append(warningText);
            } else if (result === 'all_failed') {
                var warningText = $('<p>리틀원에 등록되지 않은 아이디이거나, 아이디 또는 비밀번호를 잘못 입력하셨습니다.</p>');
                $('.login-section-warning-text').append(warningText);
            }
        }

        var emailInput = $("#user_email");
        var emailRemoveBtn = $(".__remove-component.__email");

        var passwordInput = $("#user_password");
        var passwordRemoveBtn = $(".__remove-component.__password");

        /*x버튼을 누르면 아이디의 value값을 지워줌*/
        emailRemoveBtn.on('click', function () {
            var isActive = $(this).hasClass('active');
            if (isActive) {
                emailInput.val("");
                emailInput.focus();
                $(this).removeClass('active');
            } else {
                return;
            }
        });

        /*패스워드 초기화 버튼 이벤트*/
        passwordRemoveBtn.on('click', function (e) {

            var isActive = $(this).hasClass('active');
            if (isActive) {
                passwordInput.val("");
                passwordInput.focus();
                $(this).removeClass('active');
            } else {
                return;
            }

        });

        /*초기화 버튼 활성화 이벤트(이메일).*/
        emailInput.on("keyup", function (e) {
            var strLen = $(this).val().length;
            if (strLen > 0) {
                emailRemoveBtn.addClass('active');
            } else {
                emailRemoveBtn.removeClass('active');
            }
        }).on('keydown', function (e) {
            var strLen = $(this).val().length;
            if (strLen > 0) {
                emailRemoveBtn.addClass('active');
            } else {
                emailRemoveBtn.removeClass('active');
            }
        });

        /*초기화 버튼 활성화 이벤트(비밀번호).*/
        passwordInput.on("keyup", function (e) {
            var strLen = $(this).val().length;
            if (strLen > 0) {
                passwordRemoveBtn.addClass('active');
            } else {
                passwordRemoveBtn.removeClass('active');
            }
        }).on('keydown', function (e) {
            var strLen = $(this).val().length;
            if (strLen > 0) {
                passwordRemoveBtn.addClass('active');
            } else {
                passwordRemoveBtn.removeClass('active');
            }
        });

        /*자동 로그인 on/off*/
        $(".__check-box-pointer").on('click', function (e) {
            e.preventDefault();
            var checkBox = $("#auto-login");
            var isChecked = checkBox.is(':checked');
            console.log(isChecked);
            if (isChecked) {
                checkBox.prop({
                    checked: false
                });
                $(this).removeClass('active');
            }
            if (!isChecked) {
                checkBox.prop({
                    checked: true
                });
                $(this).addClass('active');
            }
        });

        /*비밀번호 보기 메서드*/
        // var timeoutId = 0;
        var eyeIcon = $(".__check-client-password-num");
        // eyeIcon.on('mousedown touchstart', function () {
        //     timeoutId = setTimeout(function () {
        //         passwordInput.attr("type", "text");
        //     }, 100);
        //     $(this).addClass('visible');
        // }).on('mouseup mouseleave touchend', function () {
        //     clearTimeout(timeoutId);
        //     passwordInput.attr("type", "password");
        //     $(this).removeClass('visible');
        // });

        eyeIcon.on('click touch', function(e){
           $(this).toggleClass('active');
           var isActive = $(this).hasClass('active');
           if(isActive){
                       passwordInput.attr("type", "text");
               $(this).addClass('visible');
           } else{
                   passwordInput.attr("type", "password");
               $(this).removeClass('visible');
           }


        });


        /*#휴면계정 해제 메서드*/
        $(".__release-dormant-button").on("click", function (e) {
            $.ajax({
                url: "/login/dormant_chk_false",
                data: {
                    member_idx: $("#dormant_chk_idx").val()
                },
                type: "POST",
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'success') {
                        generateModal('dormant-release-modal',"성공","계정 해제 처리가 완료되었습니다.", false);
                        // 모달창 닫기
                        $(".dormant-client-form").remove();
                        $(".screen_block")[0].remove();
                        setTimeout(function () {
                            window.location.href = '/'
                        }, 200);

                    } else if (data === 'failed') {
                        generateModal('dormant-release-modal',"실패","계정 해제 처리가 실패했었습니다.", false);
                    }
                },
                error: function (e) {
                    generateModal('dormant-release-modal',e.status,"통신 오류", false);
                }
            });

        });

        /* 비밀번호 찾기 ajax */


        /*비밀번호 변경 안내 폼(expired)*/
        /*비밀번호 변경 안내 입력 이벤트*/
        $(".password-change-modal--form--box input").on('keyup', function (e) {
            var len = $(this).val().length;

            if (len > 0) {
                $(this).next('button').addClass('active');
            } else {
                $(this).next('button').removeClass('active');
            }
            
        }).on('keydown', function () {
            var len = $(this).val().length;

            if (len > 0) {
                $(this).next('button').addClass('active');
            } else {
                $(this).next('button').removeClass('active');
            }
        });

        /*폼*/
        $(".__remove-val-button").on('click', function (e) {
            var isActive = $(this).hasClass('active');
            var dataSet = $(this).attr('data-name');
            if (isActive) {
                $("input[name=" + dataSet + "]").val("");
                $("input[name=" + dataSet + "]").focus();
                $(this).removeClass('active')
            }
            return
        });


        /*다음에 변경하기*/
        $(".__none-password-change-button").on('click', function(e){
            window.location.replace('/');
        });//click


        /*4주간 표시 안함*/
        $(".__later-password-change-button").on('click', function () {
        	$.ajax({
        		url: "/login/password_renew_delay",
        		type: "GET",
        		dataType: "text",
        		success: function(data) {
        			if(data === 'success') {
        				window.location.replace('/');
        			} else if(data === 'auth_failed') {
        				alert("잘못된 접근입니다.");
        			}
        		},
        		error: function() {
        			alert("비번 2주후 변경 ajax 통신 에러");
        		}
        	});
            
        });

        /* 변경하기 버튼 클릭시*/
        $(".__submit-password-change-button").on('click', function (e) {
            e.preventDefault();
            var newPassword = $("input[name='new_password']").val();
            var newPasswordRe = $("input[name='new_password_re']").val();

            // 1. 비밀번호, 비밀번호 확인 값 같은지 체크
            if ( newPassword===newPasswordRe ) {
                $.ajax({
                    url: "/updatePassword",
                    type: "POST",
                    data: {
                        new_password: newPassword
                    },
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === "1") {
                            generateModal("sign-up-validate-success", "성공", '비밀번호 변경이 완료되었습니다.', true, function () {
                                window.location.replace('/');
                            }, true, true);
                        } else {
                            generateModal('password-change-modal','실패',"서버 오류입니다.", false);
                        }
                    },
                    error: function (e) {
                        generateModal('dormant-release-modal',e.status,"통신 오류", false);
                    }
                });
            } else {
                generateModal('password-change-modal','실패',"비밀번호가 일치하지 않습니다.", false);
            }

        });

    });
})();

