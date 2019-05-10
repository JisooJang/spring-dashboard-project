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

        var inputEle = $("#client-join-section--form .__default-input-component");


        inputFilled($("input"));
        removeInputVal($(".__remove-input-button"));
        setTimeout(function () {
            radioSelect($("#gender-input-component--female"), $("#gender-input-component--male"));
        }, 200);


        $(".__check-terms-agree-button").on('click', function (e) {
            e.preventDefault;

            var isChecked = ($("#term-check-input-component").prop('checked'));
            if (isChecked) {
                $(".svg-animated").removeClass('active');
                $("#term-check-input-component").prop('checked', false);
                clientPassport.terms = false;
                console.log(clientPassport);
            } else {
                $(".svg-animated").addClass('active');
                $("#term-check-input-component").prop('checked', true);
                clientPassport.terms = true;
                console.log(clientPassport);
            }
        });

        $(".__terms-and-agree").on('click', function () {
            var isChecked = ($("#term-check-input-component").prop('checked'));
            if (isChecked) {
                $(".svg-animated").removeClass('active');
                $("#term-check-input-component").prop('checked', false);
                clientPassport.terms = false;
                console.log(clientPassport);
            } else {
                $(".svg-animated").addClass('active');
                $("#term-check-input-component").prop('checked', true);
                clientPassport.terms = true;
                console.log(clientPassport);
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
                        generateModal(e.status + "/닉네임 중복체크 ajax 에러");
                    }
                });//ajax
            } else if ($(this).val().length === 0) {
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').removeClass('pass');
                clientPassport.nickname = false;
                console.log(clientPassport);
            } else {
                $(this).val("");
                $(this).focus();
                $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('공백 제외한 한글 1~8자, 영문 4~16자로 설정하세요.').removeClass('pass');
                clientPassport.nickname = false;
                console.log(clientPassport);
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
                console.log(clientPassport);
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
                    console.log(clientPassport);
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

        inputEle.each(function (idx, ele) {
            ele.focus();
        });

        //소셜회원 가입
        var personalJoinBtn = $(".__join-sns-member-button");
        personalJoinBtn.on("click", function (e) {

            e.preventDefault();

            var validNickname = clientPassport.nickname === true;
            var validName = clientPassport.name === true;
            var year = $("input[name='year']").val().length === 4;
            var month = $("input[name='month']").val().length === 2;
            var date = $("input[name='date']").val().length === 2;
            var validBirthdate = year && month && date
            var validPhone = $("input[name='phone']").val().length > 0;
            var validTerms = clientPassport.terms === true;

            /*유효성 모두 통과시에*/
            if (validNickname && validName && validBirthdate && validPhone && validTerms) {
                var nickname = $("input[name='nickname']").val();
                var name = $("input[name='name']").val();
                var year = $("input[name='year']").val();
                var month = $("input[name='month']").val();
                var date = $("input[name='date']").val();
                var gender = $("input[name='gender']:checked").val();
                var phone = $("input[name='phone']").val();
                var terms = $("input[name='terms']").val();
                var token = $("input[name='token']").val();

                $.ajax({
                    type: "POST",
                    url: "/social_login/auth",
                    data: {
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
                        if (data === 'log_error') {
                            generateModal("sign-up-validate-error", "회원가입", '관리자에게 문의해주세요.', false);
                            return false;
                        } else if (data === 'nickname_dup') {
                            generateModal("sign-up-validate-error", "회원가입", '중복된 닉네임은 사용할 수 없습니다.', false);
                            return false;
                        } else if (data === 'auth_info_failed') {
                            generateModal("sign-up-validate-error", "회원가입", '입력 값이 잘못되었습니다.', false);
                            return false;
                        } else if (data === 'db') {
                            generateModal("sign-up-validate-error", "서버오류", '관리자에게 문의해주세요.', false);
                        } else if (data === 'success') {
                            generateModal("sign-up-validate-success", "WELCOME", '회원가입이 완료되었습니다.', true, function () {
                                window.location.replace('/');
                            }, true, true);
                        } else if (data === 'firebase_auth_failed') {
                            generateModal("sign-up-validate-error", "회원가입", '잘못된 휴대폰 인증입니다. 재인증해주세요.', false);
                        }
                    }
                });

            } else {
                if (clientPassport.nickname === false) {
                    generateModal("sign-up-validate-error", "회원가입", '닉네임을 입력해주세요.', false);
                    $("input[type='nickname']").focus();
                } else if (clientPassport.name === false) {
                    generateModal("sign-up-validate-error", "회원가입", '성/이름을 입력해주세요.', false);
                } else if (!validBirthdate) {
                    generateModal("sign-up-validate-error", "회원가입", '생년월일을 입력해주세요.', false);
                } else if (!validPhone) {
                    generateModal("sign-up-validate-error", "회원가입", '휴대폰인증을 받으셔야 합니다.', false);
                } else if (clientPassport.terms === false) {
                    generateModal("sign-up-validate-error", "회원가입", '약관에 동의하셔야 합니다.', false);
                } else {
                    generateModal("sign-up-validate-error", "회원가입", '입력값들을 다시 한 번 확인해주세요.', false);
                }
                return false;
            }

        });//#회원가입하기 클릭 이벤트


        // 소셜미디어(SMS) 최초 가입시 휴대폰 인증후 가입버튼 클릭시
        $("#social_submit").on("click", function (e) {
            e.preventDefault();
            $.ajax({
                url: "/social_login/auth",
                type: "POST",
                data: $("#social_form").serialize(),
                datatType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data == 'dup') {
                        alert("이미 가입 이력이 있는 회원정보입니다.");
                        return false;
                    } else if (data == 'special') {
                        alert("입력값에 특수문자는 사용이 불가합니다.");
                        return false;
                    } else if (data == 'join') {
                        alert("입력값이 잘못되었습니다.");
                        return false;
                    } else if (data == 'db') {
                        alert("서버 오류로 가입에 실패하였습니다. 관리자에게 문의해주세요.");
                    } else if (data == 'success') {
                        alert("회원가입이 성공적으로 완료되었습니다.");
                        window.location.replace("/");
                    } else if (data == 'nickname_dup') {
                        alert("중복된 닉네임입니다. 다른 닉네임을 사용해주세요.");
                        return false;
                    } else if (data == 'dup_required') {
                        alert("본인인증을 완료해주세요.");
                        return false;
                    } else if (data == 'auth_info_failed') {
                        alert("잘못된 본인인증 정보입니다.");
                        return false;
                    }
                },
                error: function () {
                    alert("소셜 가입 ajax 에러");
                }
            });
        });

        function byteCheck(str, lengths, target) {
            var len = 0;
            var newStr = '';

            for (var i = 0; i < str.length; i++) {
                var n = str.charCodeAt(i); // charCodeAt : String개체에서 지정한 인덱스에 있는 문자의 unicode값을 나타내는 수를 리턴한다.
                // 값의 범위는 0과 65535사이이여 첫 128 unicode값은 ascii문자set과 일치한다.지정한 인덱스에 문자가 없다면 NaN을 리턴한다.

                var nv = str.charAt(i); // charAt : string 개체로부터 지정한 위치에 있는 문자를 꺼낸다.

                if ((n >= 0) && (n < 256)) {
                    len++;
                } else {
                    len += 2; // 한글이면 2byte로 계산한다.
                } // if~else

                if (len > lengths) {
                    $(".client-join-section--form--warning[data-name='nickname']").find('em').text('한글 8자, 영문 16자 제한').removeClass('pass');
                    break; // 제한 문자수를 넘길경우.
                } else {
                    newStr = newStr + nv;
                } //if~else
            }
            return newStr;
        }


        function acceptOnlyNumber(e) {
            var event = event || window.event;
            var key = (event.which) ? event.which : event.keyCode;
            var keyBoard = (key >= 48 && key <= 57) || (key >= 96 && key <= 105) || key === 8 || key === 46 || key === 37 || key === 39;
            if (keyBoard) {
                return true;
            } else {
                return false;
            }

        }

        function removeChar(event) {
            event = event || window.event;
            var keyID = (event.which) ? event.which : event.keyCode;
            if (keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39)
                return;
            else
                event.target.value = event.target.value.replace(/[^0-9]/g, "");
        }

        function GetIEVersion() {
            var sAgent = window.navigator.userAgent;
            var Idx = sAgent.indexOf("MSIE");
            // 익스라면 버전 리턴
            if (Idx > 0)
                return parseInt(sAgent.substring(Idx + 5, sAgent.indexOf(".", Idx)));
            // If IE 11 then look for Updated user agent string.
            else if (!!navigator.userAgent.match(/Trident\/7\./))
                return 11;
            else
                return 0; //IE가 아님
        }

        /*체크 애니메이션*/

        function checkAnimation(dataSet) {
            var path = {
                one: 'M1.1,5.6',
                two: 'M1.1 5.6l4.1 4.2',
                three: 'M1.1 5.6l4.1 4.2 8.6-8.7'
            };

            var target = document.querySelector('.__check-animation[data-name=' + dataSet + ']');
            /*svg선택*/
            var snap = Snap(target);
            /*path요소 선택*/
            var check = snap.select('path');
            /*체크 애니메이션 함수 */
            var checkAction = function () {
                check.animate({
                    d: path.one,
                    stroke: '#9013fe'
                }, 50, mina.easeBounce, function () {
                    check.animate({
                        d: path.two
                    }, 100, mina.easeElastic, function () {
                        check.animate({
                            d: path.three,
                            stroke: '#19ebdd'
                        }, 250, mina.easeElastic);
                    })
                });
            };
            //실행
            checkAction();
        }
    })()//IIFE
});//JQB
