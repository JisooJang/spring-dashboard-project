(function () {
    $(function () {
        /*태그*/
        var tag = '<section class="crop-image-modal modal-form">\n' +
            '    <div class="crop-image-modal--container">\n' +
            '        <h1>자르기</h1>\n' +
            '        <div class="crop-image-modal--container--buttons">\n' +
            '            <button type="reset" role="button" aria-selected="true" id="__cropper-cancel-button">취소</button>\n' +
            '            <button type="button" role="button" aria-selected="true" id="__crop-button">썸네일 이미지 자르기</button>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '    <div class="crop-image-modal--canvas-box">\n' +
            '        <canvas id="crop-canvas">현재 브라우저는 캔버스를 지원하지 않습니다.</canvas>\n' +
            '    </div>\n' +
            '</section>';

        var validations = new Validations();
        var fileInput = $("input[type='file']");
        var resetButton = $('.__remove-client-thumbnail');
        var thumbnail = $(".thumbnail-container--image img");
        var modifyBtn = $(".__modify-member-button");
        var ajaxReady = true;

        var originName;
        var cropper;
        var imageFormat;
        var isChanged = false;

        var clientPassport = {
            nickname: false,
            name: false,
            year: false,
            month: false,
            date: false,
            phone: false,
            terms: false,
            notDuplicatedNickname: false
        };

        var yearScope = {
            min: 1918,
            max: 2018
        };
        /*썸네일 변경*/
        fileInput.on("change", function (e) {
            $(".modal-form").remove();
            var file = this.files[0];
            var type = file.type;
            imageFormat = type;
            var size = file.size;
            var name = file.name;

            if (size >= 3000000) {
                generateModal('client-modify-info-modal', '실패', '이미지의 용량은 3MB이하로 올려주세요.');
                file = null;
                return;
            } else if (type !== 'image/png' && type !== 'image/jpg' && type !== 'image/jpeg' && type !== 'image/gif') {
                generateModal('client-modify-info-modal', '확장자 체크', 'jpg, jpeg, gif, png만 가능합니다.');
                file = null;
                return;
            }
            /*확장자 분리*/
            var ext = name.substr(name.lastIndexOf('.'), name.length);
            name = name.replace(ext, '').trim();

            /*정규식 영문(소,대),한글 자음 모음,-,_ ,@가능하게*/
            /*정규식 검사*/
            var regResult = validations.checkFilename(name);

            if (regResult) {
                var fileReader = new FileReader();
                fileReader.onload = function (e) {

                    $("body").append(tag);


                    var canvas = document.getElementById('crop-canvas');
                    var ctx = canvas.getContext('2d');

                    var image = new Image();
                    image.src = e.target.result;

                    image.onload = function () {
                        ctx.canvas.width = image.width;
                        ctx.canvas.height = image.height;
                        ctx.drawImage(image, 0, 0);

                        cropper = new Cropper(canvas, {
                            fillColor: '#fff',
                            aspectRatio: 1,
                            width: 200,
                            height: 200,
                            center: true,
                            highlight: true,
                            zoomable: false,
                            zoomOnWheel: false
                        });

                        /*자르기*/
                        $("#__crop-button").on('click', function (e) {
                            var dataImg = cropper.getCroppedCanvas().toDataURL();
                            $(".thumbnail-container--image img").attr('src', dataImg);
                            $(".modal-form").remove();
                            $(this).off('click', null);
                            $("#__cropper-cancel-button").off('click', null);
                            isChanged = true;
                        });

                        $("#__cropper-cancel-button").on('click', function (e) {
                            $(".modal-form").remove();
                            $("#__crop-button").off('click', null);
                            $(this).off('click', null);
                            isChanged = false;
                        });


                    };//image onload

                    /*원본 이름*/
                    originName = file.name;

                    e.target.result = null;

                };

                fileReader.readAsDataURL(file);


            } else {
                generateModal('client-modify-info-modal', '경고', '파일명에 특수문자를 넣지 마세요.');
                file = null;
            }

            e.target.value = null;
        });

        /*삭제 버튼 클릭시 썸네일 초기화*/
        resetButton.on('click', function (e) {
            thumbnail.attr('src', '/images/default-person.svg');
            fileInput.value = '';

            $(".modal-form").remove();
            cropper = null;
        });

        inputFilled($("#client-update-section--form input"));
        removeInputVal($(".__remove-input-button"));
        radioSelect($("#gender-input-component--female"), $("#gender-input-component--male"));

        var nicknameInput = $("input[name='nickname']");

        nicknameInput.on('blur', function () {

            var nickname = $(this).val();
            var dataSet = $(this).attr('name');
            var isValid = validations.checkNickname(nickname);

            if (isValid) {
                $.ajax({
                    url: "/mypage/nickname_check/" + nickname,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === 'success') {
                            clientPassport.nickname = true;
                            clientPassport.notDuplicatedNickname = true;
                            $(".__check-animation[data-name=" + dataSet + "]").addClass('active');
                            $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('').addClass('pass');
                            /*체크 애니메이션*/
                            checkAnimation(dataSet);
                            console.log(clientPassport);
                        } else if (data === 'nickname_dup') {
                            $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('중복된 닉네임입니다.').removeClass('pass');
                            clientPassport.nickname = false;
                            clientPassport.notDuplicatedNickname = false;
                            console.log(clientPassport);
                        } else if (data === 'nickname_size') {
                            $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('한글은 1~8자, 영문은 1~16자로 설정해주세요.').removeClass('pass');
                            clientPassport.nickname = false;
                            clientPassport.notDuplicatedNickname = false;
                            console.log(clientPassport);
                        } else if (data === 'nickname_special') {
                            $(".client-join-section--form--warning[data-name=" + dataSet + "]").find('em').text('사용불가능한 특수문자입니다.').removeClass('pass');
                            clientPassport.nickname = false;
                            clientPassport.notDuplicatedNickname = false;
                            console.log(clientPassport);
                        }
                    },
                    error: function (e) {
                        generateModal('ajax-error-moal',e.status + "닉네임 중복체크 ajax 에러");
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


        modifyBtn.on('click', function (e) {
            e.preventDefault();
            
            var nickInput = $("input[name='nickname']").val();
            var nameInput = $("input[name='name']").val();
            var year = $("input[name='year']").val();
            var month = $("input[name='month']").val();
            var date = $("input[name='date']").val();
            var phone = $("input[name='phone']").val();
            var gender = $("input[name='gender']:checked").val();

            var validNick = !isEmpty(nickInput) && validations.checkNickname(nickInput);
            var validName = !isEmpty(nameInput) && validations.onlyCheckText(nameInput);
            var validBirth = !isEmpty(year) && !isEmpty(month) && !isEmpty(date);
            var validPhone = !isEmpty(phone);
            var validGender = !isEmpty(gender);

            if (validNick && validName && validBirth && validPhone && ajaxReady && validGender === true) {

                /* 11-19 썸네일  crop 테스트(form데이터는 blob 블락 안으로 다 이동하자..)*/
                if (isChanged) {
                    var blob = cropper.getCroppedCanvas().toBlob(function (blob) {
                        var formData = new FormData($("#client-update-section--form")[0]);
                        formData.append("birth", year + month + date);
                        formData.append("gender", gender);
                        formData.append("thumbnail", blob);
                        formData.append('originName', originName);
                        formData.delete('hidden-thumbnail');

                        $.ajax({
                            url: "/mypage/modify_info",
                            type: "POST",
                            data: formData,
                            enctype: "multipart/form-data",
                            cache: false,
                            processData: false,
                            contentType: false,
                            beforeSend: function (xhr) {
                                sendCsrfToken(xhr);
                                ajaxReady = false;
                            },
                            success: function (data) {
                                console.log(data);

                                if (data === 'file_type_error') {
                                    // 잘못된 파일형식(확장자)
                                    generateModal('client-modify-modal', "실패", '확장자가 잘못 되었습니다.이미지 파일만 올려주세요.', false);
                                } else if (data === 'file_size_error') {
                                    generateModal('client-modify-modal', "실패", '3MB이하의 파일을 올려주세요.', false);
                                } else if (data === 'file_exception_error') {
                                    generateModal('client-modify-modal', "실패", '파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.', false);
                                } else if (data === 'db_error') {
                                    generateModal('client-modify-modal', "실패", '정보 수정에 실패하였습니다. 잠시 후에 다시 시도해주세요.', false);
                                } else if (data === 'success') {
                                    generateModal("client-modify-modal", "완료", '회원정보가 변경되었습니다.', true, function () {
                                        window.location.replace('/mypage/modify_info');
                                    }, true, true);
                                }

                            },
                            complete: function (e) {
                                ajaxReady = true;
                            },
                            error: function (e) {
                                generateModal('ajax-error-modal', e.status, 'AJAX통신 에러', false);

                            }
                        });// END AJAX
                    }, imageFormat);

                } else {
                    var formData = new FormData($("#client-update-section--form")[0]);
                    formData.append("birth", year + month + date);
                    formData.append("gender", gender);
                    formData.delete('hidden-thumbnail');

                    $.ajax({
                        url: "/mypage/modify_info",
                        type: "POST",
                        data: formData,
                        enctype: "multipart/form-data",
                        cache: false,
                        processData: false,
                        contentType: false,
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                            ajaxReady = false;
                        },
                        success: function (data) {
                            console.log(data);

                            if (data === 'file_type_error') {
                                // 잘못된 파일형식(확장자)
                                generateModal('client-modify-modal', "실패", '확장자가 잘못 되었습니다.이미지 파일만 올려주세요.', false);
                            } else if (data === 'file_size_error') {
                                generateModal('client-modify-modal', "실패", '3MB이하의 파일을 올려주세요.', false);
                            } else if (data === 'file_exception_error') {
                                generateModal('client-modify-modal', "실패", '파일 업로드에 실패하였습니다. 잠시 후에 다시 시도해주세요.', false);
                            } else if(data === 'file_name_size_error') {
                            	generateModal('client-modify-modal', "실패", '파일 이름은 50바이트 이하만 첨부가능합니다.', false);
                            } else if (data === 'db_error') {
                                generateModal('client-modify-modal', "실패", '정보 수정에 실패하였습니다. 잠시 후에 다시 시도해주세요.', false);
                            } else if (data === 'success') {
                                generateModal("client-modify-modal", "완료", '회원정보가 변경되었습니다.', true, function () {
                                    window.location.replace('/mypage/modify_info');
                                }, true, true);
                            }

                        },
                        complete: function (e) {
                            ajaxReady = true;
                        },
                        error: function (e) {
                            generateModal('ajax-error-modal', e.status, 'AJAX통신 에러', false);

                        }
                    });// END AJAX
                }


            } else if(!validNick){
                generateModal('validation-fail-modal','Oops!','닉네임을 입력해주세요!');
            } else if(!validName){
                generateModal('validation-fail-modal','Oops!','이름을 입력해주세요!');
            } else if(!validBirth){
                generateModal('validation-fail-modal','Oops!','생년월일을 입력해주세요!');
            } else{
                /*유효성 체크에 실패하면 이전 페이지로(history 안남게)   **/
                window.location.replace('/mypage/modify_info');
            }
        });
    });
})();