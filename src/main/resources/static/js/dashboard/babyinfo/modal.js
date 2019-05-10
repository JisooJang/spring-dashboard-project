var cropper = null;
var originName;
var mimeType;


function registerBabyInfo() {


    var tag = '<div class="alarm-modal-component modal-form" data-modal-name="baby-register-modal">\n' +
        '    <div class="alarm-modal-component--head">\n' +
        '        <h2 class="alarm-modal-component--head--title" data-i18n="babyRegisterModal.title">아이정보</h2>\n' +
        '    </div>\n' +
        '    <form class="alarm-modal-component--form baby-register-form" id="baby-register-form" role="form">\n' +
        '        <fieldset form="baby-register-form">\n' +
        '            <legend data-i18n="babyRegisterModal.content01">아이정보를 등록하실 수 있는 폼입니다.</legend>\n' +
        '            <div class="baby-register-form--thumb-box">\n' +
        '                <div>\n' +
        '                    <img src="data:image/png;base64,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"\n' +
        '                         class="baby-register-form--thumb-box--image" width="auto" height="auto"/>\n' +
        '                </div>\n' +
        '                <button type="button" class="__remove-baby-thumbnail" data-i18n="babyRegisterModal.content02">삭제</button>\n' +
        '                <label for="baby-thumbnail-component" class="__find-picture" data-i18n="[label]babyRegisterModal.content03">사진찾기\n' +
        '                    <input type="file" name="baby_file" id="baby-thumbnail-component"/>\n' +
        '                </label>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-name" class="__default-label-component" data-i18n="babyRegisterModal.content04">이름</label>\n' +
        '                <input type="text" name="name" id="client-baby-name" class="__default-input-component" required="required" autocapitalize="off" placeholder="이름을 입력해주세요." data-i18n="[placeholder]babyRegisterModal.content04" maxlength="20"/>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container" style="padding: 6px 0;">\n' +
        '                <div class="__default-label-component" data-i18n="babyRegisterModal.content05">성별</div>\n' +
        '                <div class="__select-radio-button active" role="radiogroup">\n' +
        '                    <label for="__radio-gender-component-male">\n' +
        '                        <input type="radio" name="sex" id="__radio-gender-component-male" checked="checked" role="radio" value="m"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '                <div class="__select-radio-button" role="radiogroup">\n' +
        '                    <label for="__radio-gender-component-female">\n' +
        '                        <input type="radio" name="sex" id="__radio-gender-component-female" role="radio" value="f"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-year" class="__default-label-component" data-i18n="babyRegisterModal.content06">생년월일</label>\n' +
        '                <div class="birthdate--input-box">\n' +
        '                    <input type="text" name="year" id="client-baby-year" class="__default-input-component" maxlength="4" required="required" autocapitalize="off" placeholder="Year"/>\n' +
        '                    <input type="text" name="month" id="client-baby-month" class="__default-input-component" maxlength="2" required="required" autocapitalize="off" placeholder="Month"/>\n' +
        '                    <input type="text" name="date" id="client-baby-date" class="__default-input-component" maxlength="2" required="required" autocapitalize="off" placeholder="Day"/>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-height" class="__default-label-component" data-i18n="babyRegisterModal.content07">키</label>\n' +
        '                <input type="text" name="height" id="client-baby-height" class="__default-input-component" maxlength="6" required="required" autocapitalize="off" placeholder="키를 입력해주세요.              Cm"/>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-weight" class="__default-label-component" data-i18n="babyRegisterModal.content08">몸무게</label>\n' +
        '                <input type="text" name="weight" id="client-baby-weight" class="__default-input-component" maxlength="5" required="required" autocapitalize="off" placeholder="몸무게를 입력해주세요.        Kg"/>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container blood-type-box" style="padding: 6px 0;">\n' +
        '                <div class="__default-label-component" data-i18n="babyRegisterModal.content09">혈액형</div>\n' +
        '                <div>\n' +
        '                    <div class="__select-radio-button active" role="radiogroup">\n' +
        '                        <label for="blood-type-a">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-a" checked="checked" role="radio" value="A"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                    <div class="__select-radio-button" role="radiogroup">\n' +
        '                        <label for="blood-type-b">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-b" role="radio" value="B"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                    <div class="__select-radio-button" role="radiogroup">\n' +
        '                        <label for="blood-type-o">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-o" role="radio" value="O"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                    <div class="__select-radio-button" role="radiogroup">\n' +
        '                        <label for="blood-type-ab">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-ab" role="radio" value="AB"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <p class="baby-register-form--warn" data-i18n="babyRegisterModal.warning">* 현재 아이등록은 1명만 가능합니다.</p>\n' +
        '        </fieldset>\n' +
        '    </form>\n' +
        '    <div class="alarm-modal-component--bar"></div>\n' +
        '    <div class="alarm-modal-component--buttons">\n' +
        '        <button type="button" role="button" class="__cancel-modal-button default-cancel" data-modal-name="baby-register-modal" data-i18n="babyRegisterModal.cancel">취소</button>\n' +
        '        <button type="button" role="button" class="__confirm-modal-button default-confirm" data-modal-name="baby-register-modal" data-i18n="babyRegisterModal.confirm">등록</button>\n' +
        '    </div>\n' +
        '    <button type="button" role="button" class="__close-modal-form" data-modal-name="baby-register-modal" data-i18n="common.button01">ENTER입력시 모달창을 닫습니다.</button>\n' +
        '</div>';

    var BlockTag = '<div class="modal-screen-block" data-modal-name="baby-register-modal"></div>';

    var docs = $('body');
    docs.prepend(tag);
    docs.append(BlockTag);
    $(".modal-form").innerCenter();

    var closeModal = $(".modal-screen-block[data-modal-name='baby-register-modal']");
    var screenBlock = $(".modal-screen-block[data-modal-name='baby-register-modal']");
    var cancel = $(".__cancel-modal-button[data-modal-name='baby-register-modal']");
    var ajaxSubmit = $('.__confirm-modal-button[data-modal-name="baby-register-modal"]');

    /*모달창 닫는 이벤트 할당*/
    $(".__close-modal-form").on('click', function (e) {
        $(".modal-form[data-modal-name='baby-register-modal']").remove();
        $(".modal-screen-block[data-modal-name='baby-register-modal']").remove();
        closeModal.off('click', null);
        screenBlock.off('click', null);
        cancel.off('click', null);
        ajaxSubmit.off('click', null);
    });

    /*스크린 블락에 클릭시 모달창 제거하는 이벤트 할당*/
    $(".modal-screen-block[data-modal-name='baby-register-modal']").on('click', function (e) {
        $(".modal-form[data-modal-name='baby-register-modal']").remove();
        $(".modal-screen-block[data-modal-name='baby-register-modal']").remove();
        closeModal.off('click', null);
        screenBlock.off('click', null);
        cancel.off('click', null);
        ajaxSubmit.off('click', null);
    });
    /*취소 클릭*/
    $(".__cancel-modal-button[data-modal-name='baby-register-modal']").on('click', function (e) {
        $(".modal-form[data-modal-name='baby-register-modal']").remove();
        $(".modal-screen-block[data-modal-name='baby-register-modal']").remove();
        $('.__confirm-modal-button[data-modal-name="baby-register-modal"]').off('click');
        closeModal.off('click', null);
        screenBlock.off('click', null);
        cancel.off('click', null);
        ajaxSubmit.off('click', null);
    });

    /*성별 선택*/
    $(".__select-radio-button").on('click', function (e) {
        var isActive = $(this).hasClass('active');
        if (isActive) {

        } else {
            $(this).addClass('active');
            $(this).siblings('.__select-radio-button').removeClass('active');
        }
        var target = $("input[name='gender']:checked").val();
        console.log(target);
    });

    /*썸네일 크롭퍼 태그*/
    var cropperTag = '<section class="crop-image-modal">\n' +
        '    <div class="crop-image-modal--container">\n' +
        '        <h1>자르기</h1>\n' +
        '        <div class="crop-image-modal--container--buttons">\n' +
        '            <button type="reset" role="button" aria-selected="true" id="__cropper-cancel-button" data-i18n="cropper.cancel">취소</button>\n' +
        '            <button type="button" role="button" aria-selected="true" id="__crop-button" data-i18n="cropper.confirm">썸네일 이미지 자르기</button>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '    <div class="crop-image-modal--canvas-box">\n' +
        '        <canvas id="crop-canvas" data-i18n="cropper.warning">현재 브라우저는 캔버스를 지원하지 않습니다.</canvas>\n' +
        '    </div>\n' +
        '</section>';


    /*썸네일 변경*/
    var inputFile = $("#baby-thumbnail-component");

    inputFile.on('change', function (e) {
        var file = this.files[0];
        var size = file.size;
        var type = file.type;
        originName = file.name;
        mimeType = type;

        var isValidSize = size <= 3000000;
        var isValidType = type.match(/^image\//);


        if (isValidSize && isValidType) {

            var fileReader = new FileReader();

            fileReader.onload = function (e) {

                $(".baby-register-form--thumb-box--image").attr('src', e.target.result);
                $("body").append(cropperTag);

                var canvas = document.getElementById('crop-canvas');
                var ctx = canvas.getContext('2d');


                var image = new Image();
                image.src = e.target.result;

                image.onload = function (e) {
                    ctx.canvas.width = image.width;
                    ctx.canvas.height = window.innerHeight;
                    ctx.drawImage(image, 0, 0);

                    cropper = new Cropper(canvas, {
                        fillColor: '#fff',
                        aspectRatio: 1,
                        width: image.width,
                        height:  window.innerHeight,
                        center: true,
                        highlight: true,
                        zoomable: false,
                        zoomOnWheel: false
                    });

                    /*자르기*/
                    $("#__crop-button").on('click', function (e) {
                        var dataImg = cropper.getCroppedCanvas().toDataURL();
                        $(".baby-register-form--thumb-box--image").attr('src', dataImg);
                        $(".crop-image-modal").remove();
                        $(this).off('click', null);
                        $("#__cropper-cancel-button").off('click', null);
                    });
                    $("#__cropper-cancel-button").on('click', function (e) {
                        $(".crop-image-modal").remove();
                        $("#__crop-button").off('click', null);
                        $(this).off('click', null);
                    });

                };

            };

            fileReader.readAsDataURL(file);

        } else {
            generateModalInner('error-modal','실패','이미지 파일만 가능합니다.', false);
            file = null;
        }

        e.target.value = null;

    });

    /*썸네일 삭제*/
    var thumbDelete = $(".__remove-baby-thumbnail");
    var defaultImg = 'data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAwIDEwMCIgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48Y2lyY2xlIGZpbGw9IiNDM0NCRDUiIGZpbGwtcnVsZT0ibm9uemVybyIgY3g9IjUwIiBjeT0iNTAiIHI9IjUwIi8+PHBhdGggZD0iTTE5LjM0MSA2Mi4wMTJjLS4yMTcuMDY4LS40NDEuMTItLjY3MS4xNTYtMy4wNDUuNDcyLTUuOTg2LTIuMTg4LTYuNTctNS45NDItLjU4MS0zLjc1NCAxLjQxNS03LjE4IDQuNDYtNy42NTMuNjQtLjA5OSAxLjI3NS0uMDYgMS44ODUuMDk4IDIuNzY5LTIyLjk1IDE0LjYzMS0zMi41MTQgMzIuMzY2LTMyLjUxNCAxOC41MDcgMCAyOS4yODMgMTAuNTEyIDMyLjMzMSAzMi4zNDYuMTgtLjAwOC4zNjItLjAwNS41NDUuMDEgMy4wNzMuMjM0IDUuMzI5IDMuNDk2IDUuMDM4IDcuMjg0LS4yOSAzLjc4OC0zLjAxNiA2LjY2OC02LjA5IDYuNDMzYTQuNTkgNC41OSAwIDAgMS0uNTY0LS4wNzljLTQuMjQ2IDEyLjg3Ny0xNy4zMDcgMjEuODM2LTMxLjAzNyAyMS44MzYtMTQuMDc0IDAtMjcuNjYtOC4xMTMtMzEuNjkzLTIxLjk3NXoiIGZpbGw9IiNGQUZBRkEiLz48cGF0aCBkPSJNMzcuNSA0MS42MDNjMC0xLjU2OC41MDgtMy4xNTggMS41MjUtNC43NjggMS4wMTYtMS42MSAyLjUtMi45NDMgNC40NS00IDEuOTUtMS4wNTYgNC4yMjUtMS41ODUgNi44MjUtMS41ODUgMi40MTYgMCA0LjU1LjQ0MiA2LjQgMS4zMjUgMS44NS44ODQgMy4yNzkgMi4wODUgNC4yODggMy42MDQgMS4wMDggMS41MiAxLjUxMiAzLjE3IDEuNTEyIDQuOTU0IDAgMS40MDMtLjI4OCAyLjYzNC0uODYzIDMuNjlhMTEuMzA1IDExLjMwNSAwIDAgMS0yLjA1IDIuNzM3Yy0uNzkxLjc2OC0yLjIxMiAyLjA2LTQuMjYyIDMuODc3YTE3LjI1IDE3LjI1IDAgMCAwLTEuMzYzIDEuMzVjLS4zNDEuMzg4LS41OTYuNzQzLS43NjIgMS4wNjRhNS4xNyA1LjE3IDAgMCAwLS4zODguOTY3Yy0uMDkxLjMyMS0uMjI5Ljg4Ny0uNDEyIDEuNjk2LS4zMTcgMS43MTgtMS4zMDkgMi41NzYtMi45NzUgMi41NzYtLjg2NyAwLTEuNTk2LS4yOC0yLjE4OC0uODQyLS41OTEtLjU2MS0uODg3LTEuMzk1LS44ODctMi41MDIgMC0xLjM4Ny4yMTYtMi41ODguNjUtMy42MDNhOS4xMyA5LjEzIDAgMCAxIDEuNzI1LTIuNjc1Yy43MTYtLjc2OCAxLjY4My0xLjY4IDIuOS0yLjczNyAxLjA2Ni0uOTI1IDEuODM3LTEuNjIzIDIuMzEyLTIuMDkzLjQ3Ni0uNDcxLjg3NS0uOTk1IDEuMi0xLjU3M2EzLjc3NiAzLjc3NiAwIDAgMCAuNDg4LTEuODgzYzAtMS4zMi0uNDk2LTIuNDM1LTEuNDg3LTMuMzQ0LS45OTItLjkwNy0yLjI3Mi0xLjM2Mi0zLjgzOC0xLjM2Mi0xLjgzNCAwLTMuMTg0LjQ1OC00LjA1IDEuMzc1LS44NjcuOTE2LTEuNiAyLjI2Ni0yLjIgNC4wNS0uNTY3IDEuODY2LTEuNjQyIDIuNzk4LTMuMjI1IDIuNzk4LS45MzQgMC0xLjcyMS0uMzI1LTIuMzYzLS45NzgtLjY0MS0uNjUyLS45NjItMS4zNTgtLjk2Mi0yLjExOHpNNDkuNyA2OC43NWMtMS4wMTcgMC0xLjkwNC0uMzI2LTIuNjYzLS45NzgtLjc1OC0uNjUyLTEuMTM3LTEuNTY1LTEuMTM3LTIuNzM3IDAtMS4wNC4zNjYtMS45MTYgMS4xLTIuNjI2LjczMy0uNzEgMS42MzMtMS4wNjUgMi43LTEuMDY1IDEuMDUgMCAxLjkzMy4zNTUgMi42NSAxLjA2NS43MTYuNzEgMS4wNzUgMS41ODUgMS4wNzUgMi42MjYgMCAxLjE1Ni0uMzc1IDIuMDY0LTEuMTI1IDIuNzI0LS43NS42Ni0xLjYxNy45OTEtMi42Ljk5MXoiIGZpbGw9IiNDM0NCRDUiIGZpbGwtcnVsZT0ibm9uemVybyIvPjwvZz48L3N2Zz4=';
    thumbDelete.on('click', function () {

        $(".baby-register-form--thumb-box--image").attr('src', defaultImg);
        /*file value초기화*/
        inputFile.value = '';
        /*크랍퍼에 담긴 값도 초기화*/
        cropper = null;

        if (!/safari/i.test(navigator.userAgent)) {
            inputFile.type = '';
            inputFile.type = 'file';
        }

    });


    var ajaxReady = true;
    $('.__confirm-modal-button[data-modal-name="baby-register-modal"]').on("click", function (e) {
        e.preventDefault();

        var validations = new Validations();
        var name = $("input[name='name']").val();
        var height = $("input[name='height']").val();
        var weight = $("input[name='weight']").val();
        var year = $("input[name='year']").val();
        var month = $('input[name="month"]').val();
        var date = $('input[name="date"]').val();
        var birth = year + month + date;

        /*범위*/
        var isScopeYear = (year.length === 4) && (year >= 1900 && year <= 2099);
        var isScopeMonth = (month.length === 2) && (month >= 1 && month <= 12);
        var isScopeDate = (date.length === 2) && (date >= 1 && date <= 31);
        /*정해진 포맷*/
        var isValidYear = validations.onlyCheckNumber(year) && isScopeYear;
        var isValidMonth = validations.onlyCheckNumber(month) && isScopeMonth;
        var isValidDate = validations.onlyCheckNumber(date) && isScopeDate;

        var isValidName = validations.checkNickname(name) && !isEmpty(name);
        var isValidHeight = validations.checkFloatDoublePoint(height) && !isEmpty(height);
        var isValidWeight = validations.checkFloatDoublePoint(weight) && !isEmpty(weight);
        var isValidBirth = (isValidYear && isValidMonth && isValidDate) === true;

        var inputEle;

        if (ajaxReady === true && isValidName && isValidHeight && isValidWeight && isValidBirth) {

            if (cropper === null) {
                var formData = new FormData($("#baby-register-form")[0]);
                formData.append('birth', birth);
                formData.delete('baby_file');
                formData.append('baby_file', null);
                formData.append('originName', originName);

                $.ajax({
                    type: "POST",
                    url: "/dashboard/register_infant",
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    cache: false,
                    data: formData,
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === 'success') {
                            $(".__close-modal-form").click();
                            generateModal('success-baby-modal', "등록 완료", "아기 정보를 성공적으로 등록하였습니다.", false);
                            setTimeout(function () {
                                window.location.replace('/dashboard');
                            }, 1200);
                        } else if (data === 'file_size_error') {
                            alert("크기가 2MB 미만의 파일만 첨부 가능합니다.");
                        } else if (data === 'file_name_error') {
                            alert("잘못된 파일 형식입니다.");
                        } else if (data === 'file_name_size_error') {
                            alert("파일 이름은 50바이트 이하만 첨부 가능합니다.");
                        } else if (data === 'file_exception_error') {
                            alert("파일 업로드에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                        } else if (data === 'infant_size_failed') {
                            alert("아이 정보는 최대 3명까지만 등록 가능합니다.");
                        } else if(data === 'infant_birth_failed') {
                        	alert("아이의 생년월일을 정확히 입력해주세요.");
                        } else if(data === 'infant_name_failed') {
                        	alert("아이의 이름을 입력해주세요.");
                        } else if(data === 'infant_gender_failed') {
                        	alert("잘못된 성별 정보입니다.");
                        } else if(data === 'infant_blood_type_failed') { 
                        	alert("잘못된 혈액형 정보입니다.")
                        } else if(data === 'infant_height_failed') { 
                        	alert("아이의 키를 숫자만 정확히 입력해주세요.")
                        } else if(data === 'infant_weight_failed') { 
                        	alert("아이의 몸무게를 숫자만 정확히 입력해주세요.")
                        } else {
                            alert("정보등록에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                        }
                    },
                    error: function (e) {

                    }//error
                });//ajax

            } else {
                var blobAjax = cropper.getCroppedCanvas().toBlob(function (blob) {
                    var formData = new FormData($("#baby-register-form")[0]);
                    formData.append('birth', birth);
                    formData.delete('baby_file');
                    formData.append('baby_file', blob);
                    formData.append('originName', originName);

                    $.ajax({
                        type: "POST",
                        url: "/dashboard/register_infant",
                        enctype: 'multipart/form-data',
                        processData: false,
                        contentType: false,
                        cache: false,
                        data: formData,
                        dataType: "text",
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                        },
                        success: function (data) {
                            if (data === 'success') {
                                $(".__close-modal-form").click();
                                generateModal('success-baby-modal', "성공", "아기 정보 등록을 성공적으로 완료하였습니다.", false);
                                setTimeout(function () {
                                    window.location.replace('/dashboard');
                                }, 1200);
                            } else if (data === 'file_size_error') {
                                alert("크기가 2MB 미만의 파일만 첨부 가능합니다.");
                            } else if (data === 'file_name_error') {
                                alert("잘못된 파일 형식입니다.");
                            } else if (data === 'file_name_size_error') {
                                alert("파일 이름은 50바이트 이하만 첨부 가능합니다.");
                            } else if (data === 'file_exception_error') {
                                alert("파일 업로드에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                            } else if (data === 'infant_size_failed') {
                                alert("아이 정보는 최대 3명까지만 등록 가능합니다.");
                            } else {
                                alert("정보등록에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                            }
                        },
                        error: function () {
                        }//error
                    });//ajax
                }, mimeType);

            }

        } else if (!isValidName) {
            inputEle = $("input[name='name']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidYear) {
            inputEle = $("input[name='year']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidMonth) {
            inputEle = $("input[name='month']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidDate) {
            inputEle = $("input[name='date']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidBirth) {
            alert('적합한 생년월일이 아닙니다.');
        } else if (!isValidHeight) {
            inputEle = $("input[name='height']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidWeight) {
            inputEle = $("input[name='weight']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else {
            alert('입력값을 확인해주세요.');
        }





    });//end method click

    $(".alarm-modal-component input[name]").on('blur', function () {
        $(this).removeClass('invalid');
    });

    Localize(getCookie('lang'),$(".modal-form"));

}

/*아기 정보 수정*/
function updateBabyInfo(state) {

    var url = state.url !== undefined ? state.url : 'data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAwIDEwMCIgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48Y2lyY2xlIGZpbGw9IiNDM0NCRDUiIGZpbGwtcnVsZT0ibm9uemVybyIgY3g9IjUwIiBjeT0iNTAiIHI9IjUwIi8+PHBhdGggZD0iTTE5LjM0MSA2Mi4wMTJjLS4yMTcuMDY4LS40NDEuMTItLjY3MS4xNTYtMy4wNDUuNDcyLTUuOTg2LTIuMTg4LTYuNTctNS45NDItLjU4MS0zLjc1NCAxLjQxNS03LjE4IDQuNDYtNy42NTMuNjQtLjA5OSAxLjI3NS0uMDYgMS44ODUuMDk4IDIuNzY5LTIyLjk1IDE0LjYzMS0zMi41MTQgMzIuMzY2LTMyLjUxNCAxOC41MDcgMCAyOS4yODMgMTAuNTEyIDMyLjMzMSAzMi4zNDYuMTgtLjAwOC4zNjItLjAwNS41NDUuMDEgMy4wNzMuMjM0IDUuMzI5IDMuNDk2IDUuMDM4IDcuMjg0LS4yOSAzLjc4OC0zLjAxNiA2LjY2OC02LjA5IDYuNDMzYTQuNTkgNC41OSAwIDAgMS0uNTY0LS4wNzljLTQuMjQ2IDEyLjg3Ny0xNy4zMDcgMjEuODM2LTMxLjAzNyAyMS44MzYtMTQuMDc0IDAtMjcuNjYtOC4xMTMtMzEuNjkzLTIxLjk3NXoiIGZpbGw9IiNGQUZBRkEiLz48cGF0aCBkPSJNMzcuNSA0MS42MDNjMC0xLjU2OC41MDgtMy4xNTggMS41MjUtNC43NjggMS4wMTYtMS42MSAyLjUtMi45NDMgNC40NS00IDEuOTUtMS4wNTYgNC4yMjUtMS41ODUgNi44MjUtMS41ODUgMi40MTYgMCA0LjU1LjQ0MiA2LjQgMS4zMjUgMS44NS44ODQgMy4yNzkgMi4wODUgNC4yODggMy42MDQgMS4wMDggMS41MiAxLjUxMiAzLjE3IDEuNTEyIDQuOTU0IDAgMS40MDMtLjI4OCAyLjYzNC0uODYzIDMuNjlhMTEuMzA1IDExLjMwNSAwIDAgMS0yLjA1IDIuNzM3Yy0uNzkxLjc2OC0yLjIxMiAyLjA2LTQuMjYyIDMuODc3YTE3LjI1IDE3LjI1IDAgMCAwLTEuMzYzIDEuMzVjLS4zNDEuMzg4LS41OTYuNzQzLS43NjIgMS4wNjRhNS4xNyA1LjE3IDAgMCAwLS4zODguOTY3Yy0uMDkxLjMyMS0uMjI5Ljg4Ny0uNDEyIDEuNjk2LS4zMTcgMS43MTgtMS4zMDkgMi41NzYtMi45NzUgMi41NzYtLjg2NyAwLTEuNTk2LS4yOC0yLjE4OC0uODQyLS41OTEtLjU2MS0uODg3LTEuMzk1LS44ODctMi41MDIgMC0xLjM4Ny4yMTYtMi41ODguNjUtMy42MDNhOS4xMyA5LjEzIDAgMCAxIDEuNzI1LTIuNjc1Yy43MTYtLjc2OCAxLjY4My0xLjY4IDIuOS0yLjczNyAxLjA2Ni0uOTI1IDEuODM3LTEuNjIzIDIuMzEyLTIuMDkzLjQ3Ni0uNDcxLjg3NS0uOTk1IDEuMi0xLjU3M2EzLjc3NiAzLjc3NiAwIDAgMCAuNDg4LTEuODgzYzAtMS4zMi0uNDk2LTIuNDM1LTEuNDg3LTMuMzQ0LS45OTItLjkwNy0yLjI3Mi0xLjM2Mi0zLjgzOC0xLjM2Mi0xLjgzNCAwLTMuMTg0LjQ1OC00LjA1IDEuMzc1LS44NjcuOTE2LTEuNiAyLjI2Ni0yLjIgNC4wNS0uNTY3IDEuODY2LTEuNjQyIDIuNzk4LTMuMjI1IDIuNzk4LS45MzQgMC0xLjcyMS0uMzI1LTIuMzYzLS45NzgtLjY0MS0uNjUyLS45NjItMS4zNTgtLjk2Mi0yLjExOHpNNDkuNyA2OC43NWMtMS4wMTcgMC0xLjkwNC0uMzI2LTIuNjYzLS45NzgtLjc1OC0uNjUyLTEuMTM3LTEuNTY1LTEuMTM3LTIuNzM3IDAtMS4wNC4zNjYtMS45MTYgMS4xLTIuNjI2LjczMy0uNzEgMS42MzMtMS4wNjUgMi43LTEuMDY1IDEuMDUgMCAxLjkzMy4zNTUgMi42NSAxLjA2NS43MTYuNzEgMS4wNzUgMS41ODUgMS4wNzUgMi42MjYgMCAxLjE1Ni0uMzc1IDIuMDY0LTEuMTI1IDIuNzI0LS43NS42Ni0xLjYxNy45OTEtMi42Ljk5MXoiIGZpbGw9IiNDM0NCRDUiIGZpbGwtcnVsZT0ibm9uemVybyIvPjwvZz48L3N2Zz4=';
    var name = state.name;
    var isBoy = null;
    var isGirl = null;
    var checkedBoy = null;
    var checkedGirl = null;

    var gender = state.gender;
    var year = state.year;
    var month = state.month;
    var date = state.date;
    var babyIdx = state.idx;


    var weight = state.weight;
    var height = state.height;
    var bloodType = state.blood;
    console.log(bloodType);


    var tag = '<div class="alarm-modal-component modal-form" data-modal-name="baby-register-modal">\n' +
        '    <div class="alarm-modal-component--head">\n' +
        '        <h2 class="alarm-modal-component--head--title">아이정보</h2>\n' +
        '    </div>\n' +
        '    <form class="alarm-modal-component--form baby-register-form" id="baby-update-form" role="form">\n' +
        '        <fieldset form="baby-register-form">\n' +
        '            <legend>아이정보를 등록하실 수 있는 폼입니다.</legend>\n' +
        '            <div class="baby-register-form--thumb-box">\n' +
        '                <div>\n' +
        '                    <img src="' + url + '"\n' +
        '                         class="baby-register-form--thumb-box--image" width="auto" height="auto"/>\n' +
        '                </div>\n' +
        '                <button type="button" class="__remove-baby-thumbnail">삭제</button>\n' +
        '                <label for="baby-thumbnail-component" class="__find-picture">사진찾기\n' +
        '                    <input type="file" name="thumbnail" id="baby-thumbnail-component"/>\n' +
        '                </label>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-name" class="__default-label-component">이름</label>\n' +
        '                <input type="text" name="name" id="client-baby-name" value="' + name + '" class="__default-input-component" required="required" autocapitalize="off" placeholder="이름을 입력해주세요." maxlength="20"/>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container" style="padding: 6px 0;">\n' +
        '                <div class="__default-label-component">성별</div>\n' +
        '                <div class="__select-radio-button' + (gender === 'm' ? ' active' : '') + '" role="radiogroup">\n' +
        '                    <label for="__radio-gender-component-male">\n' +
        '                        <input type="radio" name="sex" id="__radio-gender-component-male" ' + (gender === 'm' ? 'checked=checked' : '') + ' role="radio" value="m"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '                <div class="__select-radio-button' + (gender === 'f' ? ' active' : '') + '" role="radiogroup">\n' +
        '                    <label for="__radio-gender-component-female">\n' +
        '                        <input type="radio" name="sex" id="__radio-gender-component-female" ' + (gender === 'f' ? 'checked=checked' : '') + ' role="radio" value="f"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-year" class="__default-label-component">생년월일</label>\n' +
        '                <div class="birthdate--input-box">\n' +
        '                    <input type="text" name="year" id="client-baby-year" class="__default-input-component" value="' + year + '" maxlength="4" required="required" autocapitalize="off" placeholder="Year"/>\n' +
        '                    <input type="text" name="month" id="client-baby-month" class="__default-input-component" value="' + month + '"  maxlength="2" required="required" autocapitalize="off" placeholder="Month"/>\n' +
        '                    <input type="text" name="date" id="client-baby-date" class="__default-input-component" value="' + date + '" maxlength="2" required="required" autocapitalize="off" placeholder="Day"/>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-height" class="__default-label-component">키</label>\n' +
        '                <input type="text" name="height" id="client-baby-height" class="__default-input-component" value="' + height + '" maxlength="6" required="required" autocapitalize="off" placeholder="키를 입력해주세요.              Cm"/>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container">\n' +
        '                <label for="client-baby-weight" class="__default-label-component">몸무게</label>\n' +
        '                <input type="text" name="weight" id="client-baby-weight" class="__default-input-component" value="' + weight + '" maxlength="5" required="required" autocapitalize="off" placeholder="몸무게를 입력해주세요.        Kg"/>\n' +
        '            </div>\n' +
        '            <div class="baby-register-form--container blood-type-box" style="padding: 6px 0;">\n' +
        '                <div class="__default-label-component">혈액형</div>\n' +
        '                <div>\n' +
        '                    <div class="__select-radio-button' + (bloodType === 'a' ? ' active' : '') + '" role="radiogroup">\n' +
        '                        <label for="blood-type-a">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-a" ' + (bloodType === 'a' ? 'checked=checked ' : '') + 'role="radio" value="A"/>' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                    <div class="__select-radio-button' + (bloodType === 'b' ? ' active' : '') + '" role="radiogroup">\n' +
        '                        <label for="blood-type-b">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-b" ' + (bloodType === 'b' ? 'checked=checked ' : '') + 'role="radio" value="B"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                    <div class="__select-radio-button' + (bloodType === 'o' ? ' active' : '') + '"role="radiogroup">\n' +
        '                        <label for="blood-type-o">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-o"  ' + (bloodType === 'o' ? 'checked=checked ' : '') + 'role="radio" value="O"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                    <div class="__select-radio-button' + (bloodType === 'ab' ? ' active' : '') + '" role="radiogroup">\n' +
        '                        <label for="blood-type-ab">\n' +
        '                            <input type="radio" name="blood_type" id="blood-type-ab" role="radio" ' + (bloodType === 'ab' ? 'checked=checked ' : '') + ' value="AB"/>\n' +
        '                        </label>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </fieldset>\n' +
        '    </form>\n' +
        '    <div class="alarm-modal-component--bar"></div>\n' +
        '    <div class="alarm-modal-component--buttons">\n' +
        '        <button type="button" role="button" class="__baby-delete-button default-delete" data-modal-name="baby-register-modal">삭제</button>\n' +
        '        <button type="button" role="button" class="__confirm-modal-button default-edit" data-modal-name="baby-register-modal">수정</button>\n' +
        '    </div>\n' +
        '    <button type="button" role="button" class="__close-modal-form" data-modal-name="baby-register-modal">ENTER입력시 모달창을 닫습니다.</button>\n' +
        '</div>';
    var BlockTag = '<div class="modal-screen-block" data-modal-name="baby-register-modal"></div>';

    var docs = $('body');
    docs.prepend(tag);
    docs.append(BlockTag);
    $(".modal-form").innerCenter();

    var closeModal = $(".modal-screen-block[data-modal-name='baby-register-modal']");
    var screenBlock = $(".modal-screen-block[data-modal-name='baby-register-modal']");
    var cancel = $(".__cancel-modal-button[data-modal-name='baby-register-modal']");
    var ajaxSubmit = $('.__confirm-modal-button[data-modal-name="baby-register-modal"]');

    /*모달창 닫는 이벤트 할당*/
    $(".__close-modal-form").on('click', function (e) {
        $(".modal-form[data-modal-name='baby-register-modal']").remove();
        $(".modal-screen-block[data-modal-name='baby-register-modal']").remove();
        closeModal.off('click', null);
        screenBlock.off('click', null);
        cancel.off('click', null);
        ajaxSubmit.off('click', null);
    });

    /*스크린 블락에 클릭시 모달창 제거하는 이벤트 할당*/
    $(".modal-screen-block[data-modal-name='baby-register-modal']").on('click', function (e) {
        $(".modal-form[data-modal-name='baby-register-modal']").remove();
        $(".modal-screen-block[data-modal-name='baby-register-modal']").remove();
        closeModal.off('click', null);
        screenBlock.off('click', null);
        cancel.off('click', null);
        ajaxSubmit.off('click', null);
    });
    // /*취소 클릭*/
    // $(".__cancel-modal-button[data-modal-name='baby-register-modal']").on('click', function (e) {
    //     $(".modal-form[data-modal-name='baby-register-modal']").remove();
    //     $(".modal-screen-block[data-modal-name='baby-register-modal']").remove();
    //     $('.__confirm-modal-button[data-modal-name="baby-register-modal"]').off('click');
    //     closeModal.off('click', null);
    //     screenBlock.off('click', null);
    //     cancel.off('click', null);
    //     ajaxSubmit.off('click', null);
    // });
    /*아이 삭제 클릭시*/

    /*최초 활성화된 baby-idx의 data-baby-idx 값*/
    var isActiveBabyIdx = $(".baby-selected img").attr('data-baby-idx');

    $(".__baby-delete-button").on('click', function (e) {
        /*삭제 실행 후 location.reload*/
        e.preventDefault();
        generateModalInner('remove-baby-modal', '삭제', '대시보드의 모든 정보가 사라지게 됩니다.<br/> 정말로 삭제하시겠습니까?', true, function () {
            $.ajax({
                url: '/dashboard/delete_infant/' + isActiveBabyIdx,
                method: 'GET',
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data == "success") {
                        location.replace('/dashboard');
                    } else {
                        alert("아기 삭제 권한이 없습니다.");
                    }
                }
            })
        });
    });//click 아이 삭제

    /*성별 선택*/
    $(".__select-radio-button").on('click', function (e) {
        var isActive = $(this).hasClass('active');
        if (isActive) {

        } else {
            $(this).addClass('active');
            $(this).siblings('.__select-radio-button').removeClass('active');
        }
        var target = $("input[name='gender']:checked").val();
        console.log(target);
    });


    /*썸네일 크롭퍼 태그*/
    var cropperTag = '<section class="crop-image-modal">\n' +
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

    /*썸네일 변경*/
    var inputFile = $("#baby-thumbnail-component");

    inputFile.on('change', function (e) {
        var file = this.files[0];
        var size = file.size;
        var type = file.type;
        originName = file.name;
        mimeType = type;

        var isValidSize = size <= 3000000;
        var isValidType = type.match(/^image\//);

        if (isValidSize && isValidType) {
            var fileReader = new FileReader();

            fileReader.onload = function (e) {

                $(".baby-register-form--thumb-box--image").attr('src', e.target.result);
                $("body").append(cropperTag);

                var canvas = document.getElementById('crop-canvas');
                var ctx = canvas.getContext('2d');


                var image = new Image();
                image.src = e.target.result;

                image.onload = function () {
                    ctx.canvas.width = image.width;
                    ctx.canvas.height = image.height + 300;
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
                        $(".baby-register-form--thumb-box--image").attr('src', dataImg);
                        $(".crop-image-modal").remove();
                        $(this).off('click', null);
                        $("#__cropper-cancel-button").off('click', null);
                    });
                    $("#__cropper-cancel-button").on('click', function (e) {
                        $(".crop-image-modal").remove();
                        $("#__crop-button").off('click', null);
                        $(this).off('click', null);
                    });

                };

            };

            fileReader.readAsDataURL(file);
        } else {
            alert("이미지 파일만 가능합니다.");
            file = null;
        }

        e.target.value = null;
    });

    /*썸네일 삭제*/
    var thumbDelete = $(".__remove-baby-thumbnail");
    var defaultImg = 'data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAwIDEwMCIgd2lkdGg9IjEwMCIgaGVpZ2h0PSIxMDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48Y2lyY2xlIGZpbGw9IiNDM0NCRDUiIGZpbGwtcnVsZT0ibm9uemVybyIgY3g9IjUwIiBjeT0iNTAiIHI9IjUwIi8+PHBhdGggZD0iTTE5LjM0MSA2Mi4wMTJjLS4yMTcuMDY4LS40NDEuMTItLjY3MS4xNTYtMy4wNDUuNDcyLTUuOTg2LTIuMTg4LTYuNTctNS45NDItLjU4MS0zLjc1NCAxLjQxNS03LjE4IDQuNDYtNy42NTMuNjQtLjA5OSAxLjI3NS0uMDYgMS44ODUuMDk4IDIuNzY5LTIyLjk1IDE0LjYzMS0zMi41MTQgMzIuMzY2LTMyLjUxNCAxOC41MDcgMCAyOS4yODMgMTAuNTEyIDMyLjMzMSAzMi4zNDYuMTgtLjAwOC4zNjItLjAwNS41NDUuMDEgMy4wNzMuMjM0IDUuMzI5IDMuNDk2IDUuMDM4IDcuMjg0LS4yOSAzLjc4OC0zLjAxNiA2LjY2OC02LjA5IDYuNDMzYTQuNTkgNC41OSAwIDAgMS0uNTY0LS4wNzljLTQuMjQ2IDEyLjg3Ny0xNy4zMDcgMjEuODM2LTMxLjAzNyAyMS44MzYtMTQuMDc0IDAtMjcuNjYtOC4xMTMtMzEuNjkzLTIxLjk3NXoiIGZpbGw9IiNGQUZBRkEiLz48cGF0aCBkPSJNMzcuNSA0MS42MDNjMC0xLjU2OC41MDgtMy4xNTggMS41MjUtNC43NjggMS4wMTYtMS42MSAyLjUtMi45NDMgNC40NS00IDEuOTUtMS4wNTYgNC4yMjUtMS41ODUgNi44MjUtMS41ODUgMi40MTYgMCA0LjU1LjQ0MiA2LjQgMS4zMjUgMS44NS44ODQgMy4yNzkgMi4wODUgNC4yODggMy42MDQgMS4wMDggMS41MiAxLjUxMiAzLjE3IDEuNTEyIDQuOTU0IDAgMS40MDMtLjI4OCAyLjYzNC0uODYzIDMuNjlhMTEuMzA1IDExLjMwNSAwIDAgMS0yLjA1IDIuNzM3Yy0uNzkxLjc2OC0yLjIxMiAyLjA2LTQuMjYyIDMuODc3YTE3LjI1IDE3LjI1IDAgMCAwLTEuMzYzIDEuMzVjLS4zNDEuMzg4LS41OTYuNzQzLS43NjIgMS4wNjRhNS4xNyA1LjE3IDAgMCAwLS4zODguOTY3Yy0uMDkxLjMyMS0uMjI5Ljg4Ny0uNDEyIDEuNjk2LS4zMTcgMS43MTgtMS4zMDkgMi41NzYtMi45NzUgMi41NzYtLjg2NyAwLTEuNTk2LS4yOC0yLjE4OC0uODQyLS41OTEtLjU2MS0uODg3LTEuMzk1LS44ODctMi41MDIgMC0xLjM4Ny4yMTYtMi41ODguNjUtMy42MDNhOS4xMyA5LjEzIDAgMCAxIDEuNzI1LTIuNjc1Yy43MTYtLjc2OCAxLjY4My0xLjY4IDIuOS0yLjczNyAxLjA2Ni0uOTI1IDEuODM3LTEuNjIzIDIuMzEyLTIuMDkzLjQ3Ni0uNDcxLjg3NS0uOTk1IDEuMi0xLjU3M2EzLjc3NiAzLjc3NiAwIDAgMCAuNDg4LTEuODgzYzAtMS4zMi0uNDk2LTIuNDM1LTEuNDg3LTMuMzQ0LS45OTItLjkwNy0yLjI3Mi0xLjM2Mi0zLjgzOC0xLjM2Mi0xLjgzNCAwLTMuMTg0LjQ1OC00LjA1IDEuMzc1LS44NjcuOTE2LTEuNiAyLjI2Ni0yLjIgNC4wNS0uNTY3IDEuODY2LTEuNjQyIDIuNzk4LTMuMjI1IDIuNzk4LS45MzQgMC0xLjcyMS0uMzI1LTIuMzYzLS45NzgtLjY0MS0uNjUyLS45NjItMS4zNTgtLjk2Mi0yLjExOHpNNDkuNyA2OC43NWMtMS4wMTcgMC0xLjkwNC0uMzI2LTIuNjYzLS45NzgtLjc1OC0uNjUyLTEuMTM3LTEuNTY1LTEuMTM3LTIuNzM3IDAtMS4wNC4zNjYtMS45MTYgMS4xLTIuNjI2LjczMy0uNzEgMS42MzMtMS4wNjUgMi43LTEuMDY1IDEuMDUgMCAxLjkzMy4zNTUgMi42NSAxLjA2NS43MTYuNzEgMS4wNzUgMS41ODUgMS4wNzUgMi42MjYgMCAxLjE1Ni0uMzc1IDIuMDY0LTEuMTI1IDIuNzI0LS43NS42Ni0xLjYxNy45OTEtMi42Ljk5MXoiIGZpbGw9IiNDM0NCRDUiIGZpbGwtcnVsZT0ibm9uemVybyIvPjwvZz48L3N2Zz4=';
    thumbDelete.on('click', function () {

        $(".baby-register-form--thumb-box--image").attr('src', defaultImg);
        /*file value초기화*/
        inputFile.value = '';
        cropper = null;
        mimeType = null;

        if (!/safari/i.test(navigator.userAgent)) {
            inputFile.type = '';
            inputFile.type = 'file';
        }

    });


    /*아기정보 수정 ajax*/
    var ajaxReady = true;
    $('.__confirm-modal-button[data-modal-name="baby-register-modal"]').on("click", function (e) {
        e.preventDefault();
        var validations = new Validations();
        var name = $("input[name='name']").val();
        var height = $("input[name='height']").val();
        var weight = $("input[name='weight']").val();
        var year = $("input[name='year']").val();
        var month = $('input[name="month"]').val();
        var date = $('input[name="date"]').val();
        var birth = year + month + date;

        console.log($("input[name='blood_type']:checked").val());
        /*범위*/
        var isScopeYear = (year.length === 4) && (year >= 1900 && year <= 2099);
        var isScopeMonth = (month.length === 2) && (month >= 1 && month <= 12);
        var isScopeDate = (date.length === 2) && (date >= 1 && date <= 31);
        /*정해진 포맷*/
        var isValidYear = validations.onlyCheckNumber(year) && isScopeYear;
        var isValidMonth = validations.onlyCheckNumber(month) && isScopeMonth;
        var isValidDate = validations.onlyCheckNumber(date) && isScopeDate;

        var isValidName = validations.checkNickname(name) && !isEmpty(name);
        var isValidHeight = validations.checkFloatDoublePoint(height) && !isEmpty(height);
        var isValidWeight = validations.checkFloatDoublePoint(weight) && !isEmpty(weight);
        var isValidBirth = (isValidYear && isValidMonth && isValidDate) === true;

        var inputEle;

        if (ajaxReady === true && isValidName && isValidHeight && isValidWeight && isValidBirth) {

            /*수정 blob*/

            if (cropper === null) {
                var formData = new FormData($("#baby-update-form")[0]);
                formData.append('birth', birth);
                /*기존 썸네일 제거*/
                formData.delete('thumbnail');
                /*crop된 썸네일 추가*/
                formData.append('thumbnail', null);
                formData.append('originName', originName);

                $.ajax({
                    type: "POST",
                    url: "/dashboard/modify_infant_info/" + babyIdx,
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    cache: false,
                    data: formData,
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === 'success') {
                            $(".__close-modal-form").click();
                            generateModal('success-baby-modal', "WELCOME!", "아기 정보 수정이 완료되었습니다.", false);
                            setTimeout(function () {
                                window.location.replace('/dashboard');
                            }, 1200);

                            cropper = null;
                            originName = null;
                            mimeType = null;

                        } else if (data === 'file_size_error') {
                            alert("크기가 2MB 미만의 파일만 첨부 가능합니다.");
                        } else if (data === 'file_name_error') {
                            alert("잘못된 파일 형식입니다.");
                        } else if (data === 'file_exception_error') {
                            alert("파일 업로드에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                        } else if (data === 'infant_size_failed') {
                            alert("아이 정보는 최대 3명까지만 등록 가능합니다.");
                        } else if (data === 'infant_auth_failed') {
                            alert("해당 아이정보를 수정할 권한이 없습니다.");
                        } else if (data === 'file_name_size_error') {
                            alert("파일 이름은 50바이트 이하까지만 첨부 가능합니다.");
                        } else if(data === 'infant_birth_failed') {
                        	alert("아이의 생년월일을 정확히 입력해주세요.");
                        } else if(data === 'infant_name_failed') {
                        	alert("아이의 이름을 입력해주세요.");
                        } else if(data === 'infant_gender_failed') {
                        	alert("잘못된 성별 정보입니다.");
                        } else if(data === 'infant_blood_type_failed') { 
                        	alert("잘못된 혈액형 정보입니다.")
                        } else if(data === 'infant_height_failed') { 
                        	alert("아이의 키를 숫자만 정확히 입력해주세요.")
                        } else if(data === 'infant_weight_failed') { 
                        	alert("아이의 몸무게를 숫자만 정확히 입력해주세요.")
                        } 
                        else {
                            alert("정보등록에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                        }
                    },
                    error: function () {
                        alert("ajax 통신 에러");
                    }//error
                });//ajax

            } else {
                var blobAjax = cropper.getCroppedCanvas().toBlob(function (blob) {

                    var formData = new FormData($("#baby-update-form")[0]);
                    formData.append('birth', birth);
                    /*기존 썸네일 제거*/
                    formData.delete('thumbnail');
                    /*crop된 썸네일 추가*/
                    formData.append('thumbnail', blob);
                    formData.append('originName', originName);

                    $.ajax({
                        type: "POST",
                        url: "/dashboard/modify_infant_info/" + babyIdx,
                        enctype: 'multipart/form-data',
                        processData: false,
                        contentType: false,
                        cache: false,
                        data: formData,
                        dataType: "text",
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                        },
                        success: function (data) {
                            if (data === 'success') {
                                $(".__close-modal-form").click();
                                generateModal('success-baby-modal', "수정완료", "아기정보 수정이 완료되었습니다.", false);
                                setTimeout(function () {
                                    window.location.replace('/dashboard');
                                }, 1200);

                                cropper = null;
                                originName = null;
                                mimeType = null;

                            } else if (data === 'file_size_error') {
                                alert("크기가 2MB 미만의 파일만 첨부 가능합니다.");
                            } else if (data === 'file_name_error') {
                                alert("잘못된 파일 형식입니다.");
                            } else if (data === 'file_exception_error') {
                                alert("파일 업로드에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                            } else if (data === 'infant_size_failed') {
                                alert("아이 정보는 최대 3명까지만 등록 가능합니다.");
                            } else if (data === 'infant_auth_failed') {
                                alert("해당 아이정보를 수정할 권한이 없습니다.");
                            } else if (data === 'file_name_size_error') {
                                alert("파일 이름은 50바이트 이하까지만 첨부 가능합니다.");
                            } else {
                                alert("정보등록에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                            }
                        },
                        error: function () {
                            alert("ajax 통신 에러");
                        }//error
                    });//ajax

                }, mimeType);
            }
//to blob
        } else if (!isValidName) {
            inputEle = $("input[name='name']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidYear) {
            inputEle = $("input[name='year']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidMonth) {
            inputEle = $("input[name='month']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidDate) {
            inputEle = $("input[name='date']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidBirth) {
            alert('적합한 생년월일이 아닙니다.');
        } else if (!isValidHeight) {
            inputEle = $("input[name='height']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else if (!isValidWeight) {
            inputEle = $("input[name='weight']");
            inputEle.addClass('invalid');
            inputEle.val("");
            inputEle.focus();
        } else {
            alert('입력값을 확인해주세요.');
        }

    });//end method click

    $(".alarm-modal-component input[name]").on('blur', function () {
        $(this).removeClass('invalid');
    });


}

