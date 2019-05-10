(function () {
    $(function () {


        /*체크 변수 */
        var currentPasswordResult = false;
        var passwordResult = false;
        var rePasswordResult = false;


        /*기존 비밀번호*/
        var oldPassword = $('[name="old_password"]');

        $(oldPassword).on('blur', function () {
            if (isEmpty(oldPassword.val())) {
                currentPasswordResult = false;
            } else {
                currentPasswordResult = true;
            }
        });

        var passwordValidation = new Validations();
        /*새로운 비밀번호 검사*/
        var passwordName = '[name="new_password"]';

        $(passwordName).on('blur', function () {
            var len = $(this).val().length
            var result = passwordValidation.checkPassword($(passwordName));
            var dataSet = $(this).attr('name');

            if (len !== 0) {
                if (result) {
                    passwordResult = true;
                    $(".littleone-common-section--form--error[data-name=" + dataSet + "] p").text('적합.');

                } else {
                    passwordResult = false;
                    $(".littleone-common-section--form--error[data-name=" + dataSet + "] p").text('공백 제외한 8~20자로 영문, 숫자, 특수문자의 조합으로 설정하세요.');
                    $(this).val('');
                    $(this).focus();

                }
            }

        });//on blur

        /*변경할 비밀번호와 새로운 비밀번호가 일치하는 지 확인*/

        var rePasswordName = '[name="new_password_config"]';

        $(rePasswordName).on('blur', function (e) {
            var newPasswordVal = $(passwordName).val();
            var newRePasswordVal = $(this).val();
            var dataSet = $(this).attr('name');

            if (newPasswordVal !== newRePasswordVal) {
                if (newRePasswordVal.length > 0) {
                    rePasswordResult = false;
                    $(".littleone-common-section--form--error[data-name=" + dataSet + "] p").text('위의 비밀번호와 같이 입력해주세요.');
                    $(this).val('');
                    $(this).focus();
                }
            } else {
                if (newRePasswordVal.length >= 8) {
                    rePasswordResult = true;
                    $(".littleone-common-section--form--error[data-name=" + dataSet + "] p").text('적합');
                }
            }

        });//on blur

        /*FORM 전송*/
        $("#password-change-form .__submit-btn").on('click', function (e) {
            e.preventDefault();
            var submitVal = $("#password-change-form");

            /*되돌아 와서 클릭시 input 한번 돌면서 정규식 다시 체크*/
            $('input[type="password"]').each(function (idx, ele) {
                $(ele).focus();
                $(ele).blur();
            });

            if (passwordResult === true && rePasswordResult === true && currentPasswordResult === true) {
                submitVal.submit();
            } else if (!currentPasswordResult) {
                generateModal('현재 비밀번호를 확인해주세요.');
            } else if (!passwordResult) {
                generateModal('새 비밀번호를 확인해주세요.');
            } else if (!rePasswordResult) {
                generateModal('비밀번호 입력 확인을 받아주세요.');
            }

        });

    });
})();