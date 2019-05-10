$(function () {
    (function () {

        var isIe = GetIEVersion();
        if (isIe !== 0) {
            $(".client-join-section").addClass('ie-mode');
        }

        var clientPassport = {
            email: false,
            password: false,
            repassword: false,
            nickname: false,
            name: false,
            year: false,
            month: false,
            date: false,
            phone: false,
            terms: false,
            notDuplicatedEmail: false,
            notDuplicatedNickname: false
        };

        var yearScope = {
            min: 1918,
            max: 2018
        };

        var validations = new Validations();

        inputFilled($("input"));
        removeInputVal($(".__remove-input-button"));
        radioSelect($("#gender-input-component--female"), $("#gender-input-component--male"));

        $(".__check-terms-agree-button").on('click', function (e) {
            e.preventDefault;

            var isChecked = ($("#term-check-input-component").prop('checked'));
            if (isChecked) {
                $(".svg-animated").removeClass('active');
                $("#term-check-input-component").prop('checked', false);
                clientPassport.terms = false;
            } else {
                $(".svg-animated").addClass('active');
                $("#term-check-input-component").prop('checked', true);
                clientPassport.terms = true;
            }
        });

        $(".__terms-and-agree").on('click', function () {
            var isChecked = ($("#term-check-input-component").prop('checked'));
            if (isChecked) {
                $(".svg-animated").removeClass('active');
                $("#term-check-input-component").prop('checked', false);
                clientPassport.terms = false;
            } else {
                $(".svg-animated").addClass('active');
                $("#term-check-input-component").prop('checked', true);
                clientPassport.terms = true;
            }
        });

        $("#term-check-input-component").on('change', function (e) {
            var isChecked = ($("#term-check-input-component").prop('checked'));
            if (isChecked) {
                $(".svg-animated").addClass('active');
            } else {
                $(".svg-animated").removeClass('active');
            }
        });

        /*이메일 정규식 검사*/

        var emailInput = $("input[type='email']");

        emailInput.on("blur", function (e) {
            var clientEmail = $(this).val() || null;
            var dataSet = $(this).attr('name');
            /*이메일 정규식 검사*/
            var reg_email = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/;
            var isValid = reg_email.test(clientEmail);

            /*null등 비었다면*/
            if (isEmpty(clientEmail)) {
                clientEmail = null;
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                clientPassport.email = false;
                return;
            }
            /*유효성 체크에 실패했다면*/
            if (!isValid) {

                if (clientEmail.length !== 0) {
                    $(this).focus();
                    $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('잘못된 형식입니다. 이메일 주소를 다시 확인하세요.').removeClass('pass');
                    clientPassport.email = false;

                }

                $(this).val("");

                return false

            } else {

                if (dataSet === "email" && clientEmail !== null) {
                    $.ajax({
                        type: "POST",
                        url: "/join/email_check",
                        data: {
                            email: clientEmail
                        },
                        dataType: "text",
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                        },
                        success: function (data) {
                            if (data === "duplicate") {
                                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('이미 사용중인 이메일입니다.').removeClass('pass');
                                clientPassport.email = false;
                                clientPassport.notDuplicatedEmail = false;
                            } else {
                                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                                clientPassport.email = true;
                                clientPassport.notDuplicatedEmail = true;
                                var isChecked = $(".__check-animation[data-name=" + dataSet + "]").hasClass('active');
                                if (isChecked) {
                                    return
                                } else {
                                    $(".__check-animation[data-name=" + dataSet + "]").addClass('active');
                                    checkAnimation(dataSet);
                                }
                            }
                        },
                        error: function (e) {
                            generateModal("ajax-connect-error", e.status, '통신에러가 발생했습니다', false);
                        }
                    });
                }
            }
        });//이메일 정규식 검사 END

        /*
        * 기능 : 비밀번호 유효성 체크(blur)
        * 설명 : 비밀번호에 대해 유효성 체크를 합니다.(password 한글,영문, 특수문자 혼합 8~20자인지 확인)
        * */
        $("input[name='password']").on("blur", function () {
            /*비밀번호 재입력 인풋의*/
            var dataSet = $(this).attr('name');
            var password = $(this).val();
            var rePassword = $("input[name='re-password']").val();
            var validPw = validations.checkPassword(this);
            /*유효성 통과시*/
            if (validPw && $(this).val().length >= 8) {
                clientPassport.password = true;
                $(".__check-animation[data-name=" + dataSet + "]").addClass('active');
                /*체크 애니메이션*/
                checkAnimation(dataSet);
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('');

            } else {
                clientPassport.password = false;
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('공백 및 특수문자를 포함한 8~20자로 영문, 숫자, 특수문자의 조합으로 설정하세요.').removeClass('pass');
            }
            /*비밀번호 확인 input값과 값이 같은지 확인 , (값 길이가 8이상일 경우에만)*/
            if ((password !== rePassword) && rePassword.length >= 1) {
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('비밀번호가 다릅니다.').removeClass('pass');
                clientPassport.password = false;
                clientPassport.repassword = false;
            }

            if (password.length === 0) {
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                clientPassport.password = false;
            }
        });

        /*비밀번호(re) 같은 지 검사*/
        $("input[name='re-password']").on("blur", function () {
            var dataSet = $(this).attr('name');
            var password = $("input[name='password']").val();
            var rePassword = $(this).val();
            var validPw = validations.checkPassword($("input[name='password']"));

            if (rePassword.length >= 8 && (password === rePassword)) {
                if(validPw){
                    clientPassport.repassword = true;
                    $(".__check-animation[data-name=" + dataSet + "]").addClass('active');
                    /*체크 애니메이션*/
                    checkAnimation(dataSet);
                    $(".client-join-section--form--warning[data-name='password']").find('em').text('');
                    $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('');
                } else{
                    $(".client-join-section--form--warning[data-name='password']").find('em').text('8~20자 영문, 숫자, 특수문자의 조합으로 설정하세요.');
                    $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('');

                }
            } else {
                if (rePassword.length !== 0) {
                    $(this).focus();
                    $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('비밀번호가 다릅니다.').removeClass('pass');
                    clientPassport.repassword = false;
                }
                $(this).val("");
            }

            if (rePassword.length === 0) {
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                clientPassport.repassword = false;
            }

        });

        /*기능 : 닉네임 유효성 검사(blur)
       * 설명 : 닉네임에 대해 유효성 체크를 합니다.(최대 16byte, 최소 1글자)
       *      : 유효성 통과후 ajax통신후 불건전 키워드 검사후 최종 확인 처리
       * */

        var nicknameInput = $("input[name='nickname']");

        nicknameInput.on('blur', function () {
            var nickname = $(this).val();
            var dataSet = $(this).attr('name');
            var isValid = validations.checkNickname(nickname);

            if (isValid) {
                /*닉네임의 값*/
                $.ajax({
                    url: "/join/nickname_check/" + nickname,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === 'unique') {
                            clientPassport.nickname = true;
                            clientPassport.notDuplicatedNickname = true;
                            $(".__check-animation[data-name=" + dataSet + "]").addClass('active');
                            $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').addClass('pass');
                            /*체크 애니메이션*/
                            checkAnimation(dataSet);
                            console.log(clientPassport);
                        } else if (data === 'duplicate') {
                            $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('중복된 닉네임입니다.').removeClass('pass');
                            clientPassport.nickname = false;
                            clientPassport.notDuplicatedNickname = false;
                            console.log(clientPassport);
                        }
                    },
                    error: function (e) {
                        generateModal('ajax-error-modal',e.status + "닉네임 중복체크 ajax 에러");
                    }
                });//ajax
            } else if ($(this).val().length === 0) {
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                clientPassport.nickname = false;
            } else {
                $(this).val("");
                $(this).focus();
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('8~20자 영문, 숫자, 특수문자의 조합으로 설정하세요.').removeClass('pass');
                clientPassport.nickname = false;
            }
        }).on('keyup', function (e) {
            var target = $(this);
            var inputValue = byteCheck(e.target.value, 16, target);
            target.val(inputValue);

        }).on('keydown', function (e) {
            var target = $(this);
            var inputValue = byteCheck(e.target.value, 16, target);
            target.val(inputValue);
        });


        /*성씨 유효성 검사*/

        var NameInput = $("input[name='name']");

        NameInput.on('blur', function (e) {
            var lastName = $(this).val();
            var dataSet = $(this).attr('name');
            var isValid = validations.onlyCheckText(lastName);
            if (isValid) {
                clientPassport.name = true;
                $(".__check-animation[data-name=" + dataSet + "]").addClass('active');
                /*체크 애니메이션*/
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                checkAnimation(dataSet);
            } else {
                if (lastName.length !== 0) {
                    $(this).focus();
                    $(this).val("");
                    $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('잘못된 입력입니다. 다시 확인해주세요.').removeClass('pass');
                    clientPassport.name = false;
                    console.log(clientPassport);
                } else if (lastName.length === 0) {
                    $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                    clientPassport.name = false;
                }
            }
        });

        /*생년월일 유효성 검사*/

        var birthdateInput = $(".__birth-date-input-component");

        birthdateInput.on('keyup', function (e) {
            var inputVal = $(this).val();
            var isValid = validations.onlyCheckNumber(inputVal);
            var dataType = $(this).attr('data-type');
            var parseNum;

            if (isValid) {
                acceptOnlyNumber(e);
            } else if (!isValid) {
                removeChar(e);
            }

            if (dataType === 'year' && inputVal.length === 4) {

                parseNum = parseInt(inputVal);
                if (parseNum > yearScope.max || parseNum < yearScope.min) {
                    $("input[data-type='year']").val("");
                    return
                }
                if (isEmpty($("input[data-type='month']").val())) {
                    $("input[data-type='month']").focus();
                }


            } else if (dataType === 'month' && inputVal.length === 2) {
                parseNum = parseInt(inputVal);

                if (parseNum > 12 || parseNum < 1) {
                    $("input[data-type='month']").val("");
                    return
                }
                if (isEmpty($("input[data-type='date']").val())) {
                    $("input[data-type='date']").focus();
                }

            } else if (dataType === 'date' && inputVal.length === 2) {
                parseNum = parseInt(inputVal);

                if (parseNum > 31 || parseNum < 1) {
                    $("input[data-type='date']").val("");
                    return
                }
                $(this).blur();
            }


        }).on('keydown', function (e) {
            var inputVal = $(this).val();
            var isValid = validations.onlyCheckNumber(inputVal);
            var dataType = $(this).attr('data-type');
            var parseNum;

            if (isValid) {
                acceptOnlyNumber(e);
            } else if (!isValid) {
                removeChar(e);
            }

            if (dataType === 'year' && inputVal.length === 4) {

                parseNum = parseInt(inputVal);
                if (parseNum > yearScope.max || parseNum < yearScope.min) {
                    $("input[data-type='year']").val("");
                    return
                }
                if (isEmpty($("input[data-type='month']").val())) {
                    $("input[data-type='month']").focus();
                }


            } else if (dataType === 'month' && inputVal.length === 2) {
                parseNum = parseInt(inputVal);

                if (parseNum > 12 || parseNum < 1) {
                    $("input[data-type='month']").val("");
                    return
                }
                if (isEmpty($("input[data-type='date']").val())) {
                    $("input[data-type='date']").focus();
                }

            } else if (dataType === 'date' && inputVal.length === 2) {
                parseNum = parseInt(inputVal);

                if (parseNum > 31 || parseNum < 1) {
                    $("input[data-type='date']").val("");
                    return
                }
                $(this).blur();
            }
        });

        birthdateInput.on('focus', function (e) {
            var inputVal = $(this).val();
            var dataType = $(this).attr('data-type');

            if (dataType === 'year' && inputVal.length === 4) {
                $(this).val("");
            } else if (dataType === 'month' && inputVal.length === 2) {
                $(this).val("");
            } else if (dataType === 'date' && inputVal.length === 2) {
                $(this).val("");
            }
        });

        birthdateInput.on('blur', function (e) {
            var inputVal = $(this).val();
            var isValid = validations.onlyCheckNumber(inputVal);
            var dataType = $(this).attr('data-type');

            if (dataType === 'year' && inputVal.length === 4 && isValid) {
                $(".client-join-section--form--warning[data-name='birthdate']").find('em').text('적합').addClass('pass');
                clientPassport.year = true;
                console.log(clientPassport);

            } else if (dataType === 'month' && inputVal.length === 2 && isValid) {
                $(".client-join-section--form--warning[data-name='birthdate']").find('em').text('적합').addClass('pass');
                clientPassport.month = true;
                console.log(clientPassport);

            } else if (dataType === 'date' && inputVal.length === 2 && isValid) {
                $(".client-join-section--form--warning[data-name='birthdate']").find('em').text('적합').addClass('pass');
                clientPassport.date = true;
                console.log(clientPassport);
            } else if (dataType === 'year' && inputVal.length !== 4 || !isValid) {
                $(".client-join-section--form--warning[data-name='birthdate']").find('em').text('년도를 입력해주세요.').removeClass('pass');
                clientPassport.year = false;
                console.log(clientPassport);
            } else if (dataType === 'month' && inputVal.length !== 2 || !isValid) {
                $(".client-join-section--form--warning[data-name='birthdate']").find('em').text('월을 입력해주세요.').removeClass('pass');
                clientPassport.month = false;
                console.log(clientPassport);
            } else if (dataType === 'date' && inputVal.length !== 2 || !isValid) {
                $(".client-join-section--form--warning[data-name='birthdate']").find('em').text('일을 입력해주세요.').removeClass('pass');
                clientPassport.date = false;
                console.log(clientPassport);
            }

        });


        //개인회원 가입__check-terms-agree-button
        var personalJoinBtn = $(".__join-member-button");
        personalJoinBtn.on("click", function (e) {
            e.preventDefault();
            /*핸드폰 인증 여부 체크*/
            var phoneVal = $("input[name='phone']").val();
            if (!isEmpty(phoneVal)) {
                clientPassport.phone = true;
            } else {
                clientPassport.phone = false;
            }

            var validEmail = clientPassport.email === true;
            var validPassword = clientPassport.password === true;
            var validNickname = clientPassport.nickname === true;
            var validName = clientPassport.name === true;
            var validBirthdate = clientPassport.year === true && clientPassport.month === true && clientPassport.date === true;
            var validPhone = clientPassport.phone === true;
            var validTerms = clientPassport.terms === true;

            /*유효성 모두 통과시에*/
            if (validEmail && validPassword && validNickname && validName && validBirthdate && validPhone && validTerms) {
                var email = $('input[type="email"]').val();
                var password = $("input[name='password']").val();
                var nickname = $("input[name='nickname']").val();
                var name = $("input[name='name']").val();
                var year = $("input[name='year']").val();
                var month = $("input[name='month']").val();
                var date = $("input[name='date']").val();
                var gender = $("input[name='gender']:checked").val();
                var phone = $("input[name='phone']").val();
                var terms = $("input[name='terms']").val();
                var token = $("input[name='token']").val();

                var submit_url = $("#client-join-section--form").attr('action');

                $.ajax({
                    type: "POST",
                    url: submit_url,
                    data: {
                        personal_email: email,
                        password: password,
                        phone: phone,
                        nickname: nickname,
                        user_name: name,
                        user_birthdate: year + month + date,
                        user_gender: gender,
                        token: token
                    },
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    error: function (e) {
                        generateModal("ajax-connect-error", e.status, '통신에러가 발생했습니다', false);
                    },
                    success: function (data) {
                        if (data === 'dup') {
                            generateModal("sign-up-validate-error", "회원가입", '이미 가입 이력이 있는 회원정보입니다.', false);
                            return false;
                        } else if (data === 'special') {
                            generateModal("sign-up-validate-error", "회원가입", '입력 값에 특수문자는 사용할 수 없습니다.', false);
                            return false;
                        } else if (data === 'join') {
                            generateModal("sign-up-validate-error", "회원가입", '입력 값이 잘못되었습니다.', false);
                            return false;
                        } else if (data === 'db') {
                            generateModal("sign-up-validate-error", "실패", '서버 에러', false);
                        } else if (data === 'success') {
                            generateModal("sign-up-validate-success", "회원가입", '회원가입이 완료되었습니다.', true, function () {
                                window.location.replace('/');
                            }, true, true);
                        } else if (data === 'email_error') {
                            generateModal("sign-up-validate-error", "회원가입", '잘못된 이메일 형식입니다.', false);
                            return false;
                        } else if (data === 'firebase_auth_error') {
                            generateModal("sign-up-validate-error", "회원가입", '잘못된 휴대폰 인증입니다. 재인증해주세요.', false);
                        } else if(data === 'password_error') {
                        	generateModal("sign-up-validate-error", "회원가입", '비밀번호는 영문, 숫자, 특수문자 조합으로 8~20자로 설정해주세요.', false);
                        } else if(data === 'nickname_error') {
                        	generateModal("sign-up-validate-error", "회원가입", '닉네임은 한글 1~8자, 영문 1~16자로 입력해주세요. 특수문자 사용은 불가능합니다.', false);
                        }
                    }
                });

            } else {
                if (clientPassport.email === false) {
                    generateModal("sign-up-validate-error", "알림", '이메일을 확인해주세요.', false);
                    $("input[type='email']").focus();
                } else if (clientPassport.password === false) {
                    generateModal("sign-up-validate-error", "알림", '비밀번호를 입력해주세요.', false);
                    $("input[name='password']").focus();
                } else if (clientPassport.repassword === false) {
                    generateModal("sign-up-validate-error", "알림", '비밀번호가 같지 않습니다.', false);
                    $("input[type='password']").val("");
                    $("input[type='password']").focus();
                } else if (clientPassport.nickname === false) {
                    generateModal("sign-up-validate-error", "알림", '닉네임을 입력해주세요.', false);
                    $("input[type='nickname']").focus();
                } else if (clientPassport.name === false) {
                    generateModal("sign-up-validate-error", "알림", '성/이름을 입력해주세요.', false);
                } else if (clientPassport.year === false || clientPassport.month === false || clientPassport.date === false) {
                    generateModal("sign-up-validate-error", "알림", '생년월일을 입력해주세요.', false);
                } else if (clientPassport.phone === false) {
                    generateModal("sign-up-validate-error", "알림", '휴대폰 인증을 받으셔야 합니다.', false);
                } else if (clientPassport.terms === false) {
                    generateModal("sign-up-validate-error", "알림", '약관에 동의하셔야 합니다.', false);
                } else {
                    generateModal("sign-up-validate-error", "알림", '입력값들을 다시 한 번 확인해주세요.', false);
                }
                return false;
            }

        });//#회원가입하기 클릭 이벤트
    })()//IIFE
});//JQB
