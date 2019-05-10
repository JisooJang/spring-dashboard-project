/*모달창 생성 함수*/

/*기능 : 스크린 블락을 생성합니다.*/
function generateScreenBlock(jointModal) {

    /*기존에 생성된 screenblock이 있다면 삭제*/
    $(".screen_block").remove();
    /*태그 생성*/
    var tag = '<div class="screen_block" data-join-modal=' + jointModal + '></div>';
    /*body에 끝에 넣는다.*/
    $('body').append(tag);
    //css를 block으로 설정
    $(".screen_block").css('display', 'block');

    /*screen block클릭시 스크린 블락과 모달창들 닫히게 하기*/
    $(document).on('click', ".screen_block", function () {
        $(".screen_block").remove();
        /*현재 생성된 modal창이 있다면 삭제한다. 모달창들은 무조건 modal-form 붙일 것!!*/
        $(".modal-form").remove();
    });

}

/*메서드 사용법
* 2018-11-01 업데이트
*modalname : 모달의 class명 꼭 적어주며 되도록 중복되지 않을 이름을 사용.
* subject:'모달의 상단 주제'
* content:'모달 내용'
* boolean: true면 confirm형식의 모달창으로 생성 기본은 false;
* confirmcallback: confirm형식의 모달에서 확인 클릭시 callback함수 실행
* */
function generateModal(modalname, subject, content, boolean, confirmCallback, prevent, singleConfirmBoolean) {

    var lang = getCookie('lang') || 'en';

    var title = subject || "제목 입력해 주세요.";
    var contents = content || "내용을 입력해 주세요.";
    var preventRediction = prevent || false;
    var singleConfirm = singleConfirmBoolean || false;

    if (boolean === true) {
        var modalTag = '<div class="alarm-modal-component modal-form" data-modal-name="' + modalname + '">' +
            '    <div class="alarm-modal-component--head">' +
            '        <h2 class="alarm-modal-component--head--title" data-i18n="custom.">' + title + '</h2>' +
            '    </div>' +
            '    <div class="alarm-modal-component--text">' +
            '        <p class="alarm-modal-component--text--paragraph">' + contents + '</p>' +
            '    </div>' +
            '    <div class="alarm-modal-component--buttons">' +
            (singleConfirm === false ? ' <button type="reset" role="button" class="__cancel-modal-button" data-i18n="button.modalCancel" data-modal-name="' + modalname + '">취소</button>' : '\n') +
            '       <button type="button" role="button" class="__confirm-modal-button" data-i18n="button.modalConfirm" data-modal-name="' + modalname + '">확인</button>' +
            '    </div>' +
            '</div>';
    } else {
        var modalTag = '<div class="alarm-modal-component modal-form" data-modal-name="' + modalname + '">' +
            '    <div class="alarm-modal-component--head">' +
            '        <h2 class="alarm-modal-component--head--title">' + title + '</h2>' +
            '    </div>' +
            '    <div class="alarm-modal-component--text">' +
            '        <p class="alarm-modal-component--text--paragraph">' + contents + '</p>' +
            '    </div>' +
            '    <div class="alarm-modal-component--buttons">' +
            '       <button type="button" role="button" class="__confirm-modal-button" data-i18n="button.modalConfirm" data-modal-name="' + modalname + '">확인</button>' +
            '    </div>' +
            '</div>';
    } //end if ~else


    var BlockTag = '<div class="modal-screen-block" data-modal-name="' + modalname + '"></div>';

    $(".modal-form[data-modal-name=" + modalname + "]").remove();
    $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();

    $('body').append(modalTag);
    $('body').append(BlockTag);
    $(".modal-form[data-modal-name=" + modalname + "]").innerCenter();

    /*로컬라이징 화*/
    Localize(getCookie('lang'), $(".alarm-modal-component"));





    /*스크롤 막기*/
    $('div[data-modal-name=' + modalname + ']').on('scroll touchmove mousewheel', function (e) {
        e.stopPropagation();
        e.preventDefault();
    });

    /*tab키 막기*/
    $('div[data-modal-name=' + modalname + ']').on('keydown keyup', function (e) {
        if (e.keyCode === 9) {
            e.stopPropagation();
            e.preventDefault();
            return false;
        }
    });

    /*기본 포커스 주기*/
    if (singleConfirm) {
        $(".__confirm-modal-button").focus();
    }


    /*확인 버튼을 눌렀을 떄 boolean==true면 callback실행*/
    if (boolean === true) {
        $(".__confirm-modal-button[data-modal-name=" + modalname + "]").on('click', function () {
            confirmCallback();
            $(".__cancel-modal-button").click();
            $(this).off('click');
            $('div[data-modal-name=' + modalname + ']').off('scroll touchmove mousewheel keydown keyup', null);
        });
        $(".__cancel-modal-button[data-modal-name=" + modalname + "]").on('click', function () {
            $(".modal-form[data-modal-name=" + modalname + "]").remove();
            $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();
            $(this).off('click');
            $(".__confirm-modal-button[data-modal-name=" + modalname + "]").off('click');
            $('div[data-modal-name=' + modalname + ']').off('scroll touchmove mousewheel keydown keyup', null);
        });


    } else {
        $(".__confirm-modal-button[data-modal-name=" + modalname + "]").on('click', function () {
            $(".modal-form[data-modal-name=" + modalname + "]").remove();
            $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();
            $(this).off('click');
            $('div[data-modal-name=' + modalname + ']').off('scroll touchmove mousewheel keydown keyup', null);
        })
    }

    if (preventRediction === false) {
        /*스크린 블락 클릭시*/
        $(".modal-screen-block[data-modal-name=" + modalname + "]").on('click', function () {
            $(".modal-form[data-modal-name=" + modalname + "]").remove();
            $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();
            $(this).off('click');
            $('div[data-modal-name=' + modalname + ']').off('scroll touchmove mousewheel keydown keyup', null);
        });
    }


}


function generateModalInner(modalname, subject, content, boolean, confirmCallback) {
    var title = subject || "제목 입력해 주세요.";
    var contents = content || "내용을 입력해 주세요.";
    if (boolean === true) {
        var modalTag = '<div class="alarm-modal-component modal-form" data-modal-name="' + modalname + '" style="z-index: 10;">' +
            '    <div class="alarm-modal-component--head">' +
            '        <h2 class="alarm-modal-component--head--title">' + title + '</h2>' +
            '    </div>' +
            '    <div class="alarm-modal-component--text">' +
            '        <p class="alarm-modal-component--text--paragraph">' + contents + '</p>' +
            '    </div>' +
            '    <div class="alarm-modal-component--buttons">' +
            '       <button type="reset" role="button" class="__cancel-modal-button" data-modal-name="' + modalname + '" style="z-index: 10;" data-i18n="button.modalCancel"></button>' +
            '       <button type="button" role="button" class="__confirm-modal-button" data-modal-name="' + modalname + '" data-i18n="button.modalConfirm"></button>' +
            '    </div>' +
            '</div>';
    } else {
        var modalTag = '<div class="alarm-modal-component modal-form" data-modal-name="' + modalname + '" style="z-index: 10;">' +
            '    <div class="alarm-modal-component--head">' +
            '        <h2 class="alarm-modal-component--head--title">' + title + '</h2>' +
            '    </div>' +
            '    <div class="alarm-modal-component--text">' +
            '        <p class="alarm-modal-component--text--paragraph">' + contents + '</p>' +
            '    </div>' +
            '    <div class="alarm-modal-component--buttons">' +
            '       <button type="button" role="button" class="__confirm-modal-button" data-modal-name="' + modalname + '" data-i18n="button.modalConfirm"></button>' +
            '    </div>' +
            '</div>';
    } //end if ~else

    var BlockTag = '<div class="modal-screen-block" data-modal-name="' + modalname + '" style="z-index: 9;"></div>';


    $(".modal-form[data-modal-name=" + modalname + "]").remove();
    $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();

    $('body').append(modalTag);
    $('body').append(BlockTag);
    $(".modal-form[data-modal-name=" + modalname + "]").innerCenter();

    Localize(getCookie('lang'), $(".alarm-modal-component"));

    /*스크롤 막기*/
    $('div[data-modal-name=' + modalname + ']').on('scroll touchmove mousewheel', function (e) {
        e.stopPropagation();
        e.preventDefault();
    });


    /*확인 버튼을 눌렀을 떄 boolean==true면 callback실행*/
    if (boolean === true) {
        $(".__confirm-modal-button[data-modal-name=" + modalname + "]").on('click', function () {
            confirmCallback();


            $(".__cancel-modal-button").click();
            $(this).off('click');
        });

        $(".__cancel-modal-button[data-modal-name=" + modalname + "]").on('click', function () {
            $(".modal-form[data-modal-name=" + modalname + "]").remove();
            $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();
            $(this).off('click');
            $(".__confirm-modal-button[data-modal-name=" + modalname + "]").off('click');
        });


    } else {
        $(".__confirm-modal-button[data-modal-name=" + modalname + "]").on('click', function () {
            $(".modal-form[data-modal-name=" + modalname + "]").remove();
            $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();
            $(this).off('click');
        })
    }


    /*스크린 블락 클릭시*/
    $(".modal-screen-block[data-modal-name=" + modalname + "]").on('click', function () {
        $(".modal-form[data-modal-name=" + modalname + "]").remove();
        $(".modal-screen-block[data-modal-name=" + modalname + "]").remove();
        $(this).off('click');
        /*스크롤 막기*/
        $('div[data-modal-name=' + modalname + ']').on('scroll touchmove mousewheel', null);
    });

}