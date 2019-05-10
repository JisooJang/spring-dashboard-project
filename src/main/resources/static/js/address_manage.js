$(function () {

    /*윈도우 리사이징시 모달창 항상 가운데에*/
    $(window).on('resize', function () {
        $(".modal-form").innerCenter();
    });


    $(document).on('keyup', '.delivery-add-modal--form', function () {
        $(this).parents('.delivery-add-modal--form--container').removeClass('active');
    });
    $(document).on("click", ".__register-address-btn.active", function (e) {

        $.ajax({
            type: "GET",
            url: "/count_address_list",
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data >= 3) {
                    alert("배송지 추가는 최대 3개까지만 가능합니다.");
                    return;
                } else {
                    deliveryAddForm();
                    $(".modal-form").innerCenter();
                }
            },
            error: function () {
                alert("AJAX 오류");
            }
        });
    });


    /*배송지 추가 취소*/

    /* 기본 배송지로 등록 버튼 클릭시 */
    $(document).on("click", ".__set-default-btn", function () {
        var idx = $(this).attr("data-client-idx");
        $.ajax({
            type: "GET",
            url: "/set_default_address/" + idx,
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data === 'success') {
                    location.reload();
                } else {
                    alert('기본 배송지 설정 중 오류가 발생하였습니다. 잠시 후에 다시 시도해주세요.');
                }
            },
            error: function (e) {
                alert(e.status + " AJAX 오류");
            }
        });
    });

    /* 배송지 삭제버튼 클릭시 */
    $(document).on("click", ".__remove-delivery-btn", function () {
        var idx = $(this).attr("data-client-idx");
        generateModal("delte-address-modal", '삭제', '이 배송지 주소를 삭제하시겠습니까?', true, function () {
            $.ajax({
                type: "GET",
                url: "/delete_address/" + idx,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'success') {
                        location.reload();
                    } else if (data === 'default_failed') {
                        alert("기본배송지는 최소한 1개가 존재해야 합니다.");
                    }
                    else {
                        alert('배송지 삭제 중 오류가 발생하였습니다. 잠시 후에 다시 시도해주세요.');
                    }
                },
                error: function () {
                    alert("AJAX 오류");
                }
            });

        });

    });


    /*배송지 수정 버튼 클릭시*/
    $(document).on("click", ".__modify-delivery-btn", function (e) {
        var idx = $(this).attr("data-client-idx");

        $.ajax({
            type: "GET",
            url: "/modify_address/" + idx,
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {

                var editStatus = {
                    name: data.recipient_name,
                    phone: data.recipient_phone,
                    phone2: data.recipient_phone2,
                    addressName: data.address_name,
                    zipcode: data.zipcode,
                    address1: data.address1,
                    address2: data.address2
                };

                deliveryEditForm(idx, editStatus);

            },
            error: function () {
                alert("AJAX 오류");
            }
        });
    });

    // 배송지 추가시 기본정보 불러오기 체크박스 서택시 기본 주소 로드
    // $(document).on("click", "#get_user_button", function () {
    //     if ($("#get_user_button").prop("checked") === true) {
    //         $.ajax({
    //             type: "GET",
    //             url: "/load_default_info",
    //             beforeSend: function (xhr) {
    //                 sendCsrfToken(xhr);
    //             },
    //             success: function (data) {
    //                 $("#recipient_name").attr("value", data.name);
    //                 $("#recipient_phone").attr("value", data.phone);
    //                 $("#address1").attr("value", data.address1);
    //                 $("#address2").attr("value", data.address2);
    //                 $("#zipcode").attr("value", data.zipcode);
    //             },
    //             error: function () {
    //                 alert("AJAX 오류");
    //             }
    //         });
    //     }
    // });


    /*키업 이벤트*/
    $(document).on("keyup", ".delivery-add-modal--form input", function (e) {
        var leng = $(this).val().length;
        if (leng > 0) {
            $(this).next('button').addClass('active');
        } else {
            $(this).next('button').removeClass('active');
        }

        var dataSet = $(this).attr('name');
        var parentNode = $("div[data-input-component=" + dataSet + "]");
        parentNode.removeClass('active');

    }).on("keydown", "input", function (e) {

        var leng = $(this).val().length;
        if (leng > 0) {
            $(this).next('button').addClass('active');
        } else {
            $(this).next('button').removeClass('active');
        }
        var dataSet = $(this).attr('name');
        var parentNode = $("div[data-input-component=" + dataSet + "]");
        parentNode.removeClass('active');
    });


    /*x클릭시 글자 삭제*/
    $(document).on('click', '.__remove-txt-button.active', function () {
        $(this).prev('input').val("");
        $(this).removeClass('active');

    });

    $(document).on('click', ".__set-default-delivery-address", function () {
        var isActive = $(this).hasClass('active');
        if(isActive){
            $(this).removeClass('active');
            $('input[name="default"]').prop('checked',false);
            $('input[name="default"]').val(false);
        } else {
            $(this).addClass('active');
            $('input[name="default"]').prop('checked',true);
            $('input[name="default"]').val(true);
        }
    });

});

function deliveryEditForm(clientIdx, status) {

    $("modal-form").remove();
    console.log(status);

    var tag = '<div class="delivery-add-modal modal-form" data-modal-name="delivery-edit">\n' +
        '    <div class="delivery-add-modal--logo">리틀원의 배송지 수정 로고입니다.</div>\n' +
        '    <div class="delivery-add-modal--bar"></div>\n' +
        '    <div class="delivery-add-modal--desc">\n' +
        '        <h1>배송지 수정</h1>\n' +
        '        <p>필수 정보(<em>*</em>)는 빠짐없이 기입해주세요.</p>\n' +
        '    </div>\n' +
        '    <form id="delivery-edit-form" class="delivery-add-modal--form" role="form">\n' +
        '        <fieldset form="delivery-edit-form">\n' +
        '            <legend>배송지를 수정할 수 있는 모달폼 입니다.</legend>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="address_name">\n' +
        '                <label for="update_address_name" class="delivery-add-modal--form--container--info">배송지 이름<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box --default-input">\n' +
        '                    <input type="text" name="address_name" maxlength="30" id="update_address_name" autocomplete="none"\n' +
        '                           placeholder="배송지 이름을 입력하세요." value="' + status.addressName + '"/>\n' +
        '                    <button type="button" class="__remove-txt-button' + (status.addressName.length > 0 ? " active" : null) + '">지우기</button>\n' +
        '                </div>\n' +
        '                <div class="__set-default-delivery-address">\n' +
        '                    <span>\n' +
        '                        <svg></svg>\n' +
        '                    </span>\n' +
        '                    <label>기본배송지\n' +
        '                        <input type="checkbox" name="default" value="false"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="recipient_name">\n' +
        '                <label for="update_recipient_name" class="delivery-add-modal--form--container--info">이름<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="recipient_name" id="update_recipient_name" maxlength="30" autocomplete="none"\n' +
        '                           placeholder="받으시는 분의 전체 성함을 입력하세요." value="' + status.name + '"/>\n' +
        '                    <button type="button" class="__remove-txt-button' + (status.name.length > 0 ? " active" : null) + '">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="recipient_phone">\n' +
        '                <label for="update_recipient_phone"\n' +
        '                       class="delivery-add-modal--form--container--info">휴대전화<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="recipient_phone" id="update_recipient_phone" maxlength="30" autocomplete="none"\n' +
        '                           placeholder="\'-\'없이 연락처를 입력하세요." value="' + status.phone + '"/>\n' +
        '                    <button type="button" class="__remove-txt-button' + (status.phone.length > 0 ? " active" : null) + '">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container">\n' +
        '                <label for="update_recipient_phone2" class="delivery-add-modal--form--container--info">기타 연락처\n' +
        '                    &nbsp;&nbsp;</label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="recipient_phone2" id="update_recipient_phone2" maxlength="30" autocomplete="none"\n' +
        '                           placeholder="\'-\'없이 연락처를 입력하세요." value="' + status.phone2 + '"/>\n' +
        '                    <button type="button" class="__remove-txt-button' + (status.phone2.length > 0 ? " active" : null) + '">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="zipcode">\n' +
        '                <label for="zipcode" class="delivery-add-modal--form--container--info">주소<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box short">\n' +
        '                    <input type="text" name="zipcode" id="update_zipcode" maxlength="30" autocomplete="none" readonly="readonly" value="' + status.zipcode + '"/>\n' +
        '                </div>\n' +
        '                <button type="button" class="__find-post" onclick="execDaumPostcode(\'update_zipcode\',\'update_address1\',\'update_address2\')">\n' +
        '                    우편번호\n' +
        '                </button>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container">\n' +
        '                <div class="delivery-add-modal--form--container--info"></div>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="address1" id="update_address1" maxlength="30" autocomplete="none" readonly="readonly" value="' + status.address1 + '"/>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container">\n' +
        '                <div class="delivery-add-modal--form--container--info"></div>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="address2" id="update_address2" maxlength="36" autocomplete="none"\n' +
        '                           placeholder="상세 주소를 기입하세요." value="' + status.address2 + '"/>\n' +
        '                    <button type="button" class="__remove-txt-button' + (status.address2.length > 0 ? " active" : null) + '">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </fieldset>\n' +
        '        <div class="delivery-add-modal--form--submit">\n' +
        '            <button type="reset" class="__cancel-delivery-button" data-join-modal="delivery-edit">취소</button>\n' +
        '            <button type="submit" class="__edit-delivery-button" data-join-modal="delivery-edit" data-client-idx=' + clientIdx + '>확인</button>\n' +
        '        </div>\n' +
        '    </form>\n' +
        '    <button type="button" class="__close-modal-form" data-join-modal="delivery-edit">close</button>\n' +
        '</div>';

    $("body").append(tag);

    $(".modal-form").innerCenter();
    defaultScreenBlock('delivery-edit');


    /*취소*/
    $(".__cancel-delivery-button").on("click", function () {
        var joinComponent = $(this).attr('data-join-modal');
        $(".modal-screen-block[data-modal-name=" + joinComponent + "]").remove();
        $(".modal-form[data-modal-name=" + joinComponent + "]").remove();
    });

    /*입력 모달폼 닫기*/
    $(".__close-modal-form").on('click', "", function () {
        var joinComponent = $(this).attr('data-join-modal');
        $(".modal-screen-block[data-modal-name=" + joinComponent + "]").remove();
        $(".modal-form[data-modal-name=" + joinComponent + "]").remove();
    });

    $(".__edit-delivery-button").on("click", function (e) {
        e.preventDefault();		// 페이지 이벤트가 넘어가지 않도록 제어
        var clientIdx = $(this).attr('data-client-idx');

        var updateAddressName = $("#update_address_name").val();
        var updateRecipientName = $("#update_recipient_name").val();
        var updateZipCode = $("#update_zipcode").val();
        var updateAddress1 = $("#update_address1").val();
        var updateAddress2 = $("#update_address2").val();
        var updateRecipientPhone1 = $("#update_recipient_phone").val();
        var updateRecipientPhone2 = $("#update_recipient_phone2").val();

        if (isEmpty(updateAddressName)) {
            $("label[for='update_address_name']").parent().addClass('active');
            return false;
        }
        if (isEmpty(updateRecipientName)) {
            $("label[for='update_recipient_name']").parent().addClass('active');
            return false;
        }
        if (isEmpty(updateRecipientPhone1)) {
            $("label[for='update_recipient_phone']").parent().addClass('active');
            return false;
        }

        if (isEmpty(updateZipCode) || isEmpty(updateAddress1)) {
            $("label[for='zipcode']").parent().addClass('active');
            return false;
        }


        $.ajax({
            type: "POST",
            url: "/modify_address/" + clientIdx,
            data: {
                recipient_name: $("#update_recipient_name").val(),
                recipient_phone: $("#update_recipient_phone").val(),
                recipient_phone2: $("#update_recipient_phone2").val(),
                address_name: $("#update_address_name").val(),
                address1: $("#update_address1").val(),
                address2: $("#update_address2").val(),
                zipcode: $("#update_zipcode").val()
            },
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data === "success") {
                    alert("배송지 수정이 완료되었습니다.");
                    location.reload();
                } else {
                    alert("배송지 수정 중 오류가 발생하였습니다. 잠시 후에 다시 시도해주세요.");
                }
            },
            error: function () {
                alert("ajax 오류");
            }
        });//end ajax
    });//emd click;
}


function deliveryAddForm() {

    var tag = '<div class="delivery-add-modal modal-form" data-modal-name="delivery-add">\n' +
        '    <div class="delivery-add-modal--logo">리틀원의 배송지 추가 로고입니다.</div>\n' +
        '    <div class="delivery-add-modal--bar"></div>\n' +
        '    <div class="delivery-add-modal--desc">\n' +
        '        <h1>배송지 추가</h1>\n' +
        '        <p>필수 정보(<em>*</em>)는 빠짐없이 기입해주세요.</p>\n' +
        '    </div>\n' +
        '    <form id="delivery-add-form" class="delivery-add-modal--form" role="form">\n' +
        '        <fieldset form="delivery-add-form">\n' +
        '            <legend>배송지를 추가할 수 있는 모달폼 입니다.(최대 3개까지 추가 가능합니다.)</legend>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="address_name">\n' +
        '                <label for="address_name" class="delivery-add-modal--form--container--info" >배송지 이름<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box --default-input">\n' +
        '                    <input type="text" name="address_name" maxlength="30" id="address_name" autocomplete="none"\n' +
        '                           placeholder="배송지 이름을 입력하세요."/>\n' +
        '                    <button type="button" class="__remove-txt-button">지우기</button>\n' +
        '                </div>\n' +
        '                <div class="__set-default-delivery-address">\n' +
        '                    <span>\n' +
        '                        <svg></svg>\n' +
        '                    </span>\n' +
        '                    <label>기본배송지\n' +
        '                        <input type="checkbox" name="default" value="false"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="recipient_name">\n' +
        '                <label for="recipient_name" class="delivery-add-modal--form--container--info">이름<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="recipient_name" id="recipient_name" maxlength="30" autocomplete="none"\n' +
        '                           placeholder="받으시는 분의 전체 성함을 입력하세요."/>\n' +
        '                    <button type="button" class="__remove-txt-button">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="recipient_phone">\n' +
        '                <label for="recipient_phone"\n' +
        '                       class="delivery-add-modal--form--container--info">휴대전화<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="recipient_phone" id="recipient_phone" maxlength="30" autocomplete="none"\n' +
        '                           placeholder="\'-\'없이 연락처를 입력하세요."/>\n' +
        '                    <button type="button" class="__remove-txt-button">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container">\n' +
        '                <label for="recipient_phone2" class="delivery-add-modal--form--container--info">기타 연락처\n' +
        '                    &nbsp;&nbsp;</label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="recipient_phone2" id="recipient_phone2" maxlength="30" autocomplete="none"\n' +
        '                           placeholder="\'-\'없이 연락처를 입력하세요."/>\n' +
        '                    <button type="button" class="__remove-txt-button">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container" data-input-component="zipcode">\n' +
        '                <label for="zipcode" class="delivery-add-modal--form--container--info">주소<span>*</span></label>\n' +
        '                <div class="delivery-add-modal--form--container--input-box short">\n' +
        '                    <input type="text" name="zipcode" id="zipcode" maxlength="30" autocomplete="none" readonly="readonly"/>\n' +
        '                </div>\n' +
        '                <button type="button" class="__find-post" onclick="execDaumPostcode(\'zipcode\',\'address1\',\'address2\')">\n' +
        '                    우편번호\n' +
        '                </button>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container">\n' +
        '                <div class="delivery-add-modal--form--container--info"></div>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="address1" id="address1" maxlength="30" autocomplete="none" readonly="readonly"/>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="delivery-add-modal--form--container">\n' +
        '                <div class="delivery-add-modal--form--container--info"></div>\n' +
        '                <div class="delivery-add-modal--form--container--input-box long">\n' +
        '                    <input type="text" name="address2" id="address2" maxlength="36" autocomplete="none"\n' +
        '                           placeholder="상세 주소를 기입하세요."/>\n' +
        '                    <button type="button" class="__remove-txt-button">지우기</button>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '        </fieldset>\n' +
        '        <div class="delivery-add-modal--form--submit">\n' +
        '            <button type="reset" class="__cancel-delivery-button" data-join-modal="delivery-add">취소</button>\n' +
        '            <button type="submit" class="__add-delivery-button" data-join-modal="delivery-add">확인</button>\n' +
        '        </div>\n' +
        '    </form>\n' +
        '    <button type="button" class="__close-modal-form" data-join-modal="delivery-add">close</button>\n' +
        '</div>';


    $("body").append(tag);
    defaultScreenBlock('delivery-add');

    /*취소*/
    $(".__cancel-delivery-button").on("click", function () {
        var joinComponent = $(this).attr('data-join-modal');
        $(".modal-screen-block[data-modal-name=" + joinComponent + "]").remove();
        $(".modal-form[data-modal-name=" + joinComponent + "]").remove();
    });

    /*입력 모달폼 닫기*/
    $(".__close-modal-form").on('click', "", function () {
        var joinComponent = $(this).attr('data-join-modal');
        $(".modal-screen-block[data-modal-name=" + joinComponent + "]").remove();
        $(".modal-form[data-modal-name=" + joinComponent + "]").remove();
    });

    /*확인 클릭*/
    $(".__add-delivery-button").on('click', function (e) {
        e.preventDefault();
        var tempAddressName = $("#address_name").val();
        var tempRecipientName = $("#recipient_name").val();
        var tempZipCode = $("#zipcode").val();
        var tempAddress1 = $("#address1").val();
        var tempAddress2 = $("#address2").val();
        var tempRecipientPhone1 = $("#recipient_phone").val();
        var tempRecipientPhone2 = $("#recipient_phone2").val();

        if (!(isEmpty(tempRecipientName) && isEmpty(tempAddressName) && isEmpty(tempZipCode) && isEmpty(tempAddress1) && isEmpty(tempAddress2) && !(isEmpty(tempRecipientPhone1)
            || isEmpty(tempRecipientPhone2)))) {
            $.ajax({
                type: "POST",
                url: "/add_address",
                data: {
                    recipient_name: $("#recipient_name").val(),
                    recipient_phone: $("#recipient_phone").val(),
                    recipient_phone2: $("#recipient_phone2").val(),
                    address_name: $("#address_name").val(),
                    address1: $("#address1").val(),
                    address2: $("#address2").val(),
                    zipcode: $("#zipcode").val()
                },
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {

                    if (data === 'success') {
                        $(".modal").hide();
                        $(".modal_block").hide();
                        location.reload();
                    }
                },
                error: function () {
                    alert("AJAX 오류");
                }
            });
        }

        if (isEmpty(tempAddressName)) {
            $("label[for='address_name']").parent().addClass('active');
            return false;
        }
        if (isEmpty(tempRecipientName)) {
            $("label[for='recipient_name']").parent().addClass('active');

            return false;
        }
        if (isEmpty(tempRecipientPhone1)) {
            $("label[for='recipient_phone']").parent().addClass('active');

            return false;
        }
        if (isEmpty(tempZipCode) || isEmpty(tempAddress1)) {
            $("label[for='zipcode']").parent().addClass('active');

            return false;
        }

        return false;
    });

}
