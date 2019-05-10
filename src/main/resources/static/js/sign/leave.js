(function () {

    $(function () {
        var sendAuthBtn = $("#send_auth_mail");
        var inputBox = $(".__input-component");
        var removeBtn = $(".__remove-input-value");
        var autoFocus = $("#autoFocus").val() || false;


        if (autoFocus === 'true') {
            inputBox.focus();
        }

        removeBtn.on('click', function (e) {
            var isActive = $(this).hasClass('active') || false;
            var dataSet = $(this).attr('data-name');
            if (isActive) {
                $(".__input-component[name="+dataSet+"]").val('');
                $(this).removeClass('active');
                $(".__input-component[name="+dataSet+"]").focus();
            } else {
                return
            }
        });


        inputBox.on('keydown', function (e) {
            var len = $(this).val().length;
            var dataSet = $(this).attr('name');
            var isActive = $(".__remove-input-value[data-name="+dataSet+"]").hasClass('active') || false;

            if (len > 0) {
                if (isActive) {
                    return;
                } else {
                    $(".__remove-input-value[data-name="+dataSet+"]").addClass('active');
                    $(".littleone-common-section--form--error[data-name="+dataSet+"] p").text("");
                }
            } else {
                $(".__remove-input-value[data-name="+dataSet+"]").removeClass('active');

            }
        }).on("keyup", function (e) {
            var len = $(this).val().length;
            var dataSet = $(this).attr('name');
            var isActive = $(".__remove-input-value[data-name="+dataSet+"]").hasClass('active') || false;

            if (len > 0) {
                if (isActive) {
                    return;
                } else {
                    $(".__remove-input-value[data-name="+dataSet+"]").addClass('active');
                    $(".littleone-common-section--form--error[data-name="+dataSet+"] p").text("");
                }
            } else {
                $(".__remove-input-value[data-name="+dataSet+"]").removeClass('active');

            }
        });

        /*sns회원 탈퇴*/
        sendAuthBtn.on("click", function (e) {
            e.preventDefault();
            generateModal("메일을 전송합니다.");
            $.ajax({
                type: "GET",
                url: "/mypage/member_leave/social",
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === "success") {
                        generateModal("인증 메일 전송을 완료하였습니다.");
                    } else {
                        generateModal("인증 메일 전송에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
                    }
                },
                error: function () {
                    generateModal("ajax 오류");
                }
            });
        })


    });//JQB
})();//IIFE