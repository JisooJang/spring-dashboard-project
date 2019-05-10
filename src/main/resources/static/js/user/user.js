(function () {
    $(function () {
        var windowSize = $(window).width();
        var profileEditBtn = $(".__edit-profile-button");
        var packeryOption;
        var galleryView = $(".mypage-section--gallery-list");
        var historyView = $(".mypage-section--history-list");
        var galleryBtn = $(".__gallery-button");
        var communiyBtn = $(".__community-button");
        var member_idx = $("#session_idx").val();
        var count = 0;
        var url = window.location.href;
        var url2 = window.location.href.split("/community/view_member_info/");
        var page_num = parseInt(window.location.href.substring(url.length - 1, url.length));
        var babyInfoBtn = $("#baby-info");
        var boardHistoryBtn = $(".__under.board-history");
        var currentParam = window.location.search.substring(1);
        var isActive = parse_query_string(currentParam);

        if (isActive.community === 'active') {
            $(".__gallery-button").removeClass('active');
            $(".mypage-section--gallery-list").removeClass('active');
            $(".__community-button").addClass('active');
            $(".mypage-section--history-list").addClass('active');
        }


        if (windowSize < 641) {
            packeryOption = {
                itemSelector: '.grid',
                gutter: 4,
            }
        } else {
            packeryOption = {
                itemSelector: '.grid',
                gutter: 10,
            }
        }
        /*팩커리 정의 후에 실행*/
        var isActiveGallery = $("#user--gallery-history").length;
        if(isActiveGallery > 0){
            setTimeout(function () {
                var pckry = new Packery('#user--gallery-history', packeryOption);
            }, 450);
        }



        /*갤러리 메뉴 탭 클릭시*/
        galleryBtn.on('click', function (e) {
            var isActive = $(this).hasClass('active');
            historyView.removeClass('active');
            galleryView.addClass('active');

            if (isActive) {
                return;
            } else {
                communiyBtn.removeClass('active');
                $(this).toggleClass('active');
            }
        });

        /*커뮤니티 게시글 리스트 탭 클릭시*/
        communiyBtn.on('click', function (e) {
            var isActive = $(this).hasClass('active');
            galleryView.removeClass('active');
            historyView.addClass('active');

            if (isActive) {
                return;
            } else {
                galleryBtn.removeClass('active');
                $(this).toggleClass('active');
            }

        });

        /*프로필 수정 버튼 클릭시 입력 모달창 생성*/
        profileEditBtn.on('click', function () {
            profileEditModal();
        });

        /*아이정보 보기 클릭시 정보 보는 모달창 생성*/
        babyInfoBtn.on('click', function () {
            mybabyInfoModal();
        });

        /*게시글 내역 보는 모달창 생성*/
        boardHistoryBtn.on('click', function () {
            boardHistroyModal();
        });

        // 게시물 삭제
        $(".__delete-button.mypage-section-button").on("click", function (e) {
            e.preventDefault();
            var board_idx = $(this).attr('data-board-idx');
            var result = confirm("정말 삭제하시겠습니까?");
            if (result) {
                $.ajax({
                    url: "/community/qna/delete/" + board_idx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === '1') {
                            alert("존재하지 않는 게시물입니다.");
                        } else if (data === '2') {
                            alert("삭제 권한이 없습니다.");
                        } else if (data === '0') {
                            alert("존재하지 않는 게시물입니다.");
                        } else if (data === 'success') {
                            window.location.reload();
                        }
                    }
                });
            }
        });//click

        $(".__txt").on("click", function (e) {
            if (url.split("/")[6] == 'gallery') {
                $(location).attr("href", url2[0] + "/community/view_member_info/" + member_idx + "/board/1");
            }
        });

        $(".__gallery").on("click", function (e) {
            if (url.split("/")[6] == 'board') {
                $(location).attr("href", url2[0] + "/community/view_member_info/" + member_idx + "/gallery/1");
            }
        });

        $("#first_index").on("click", function (e) {
            e.preventDefault();
            if (page_num != 1) {
                $(location).attr("href", url2[0] + "/community/view_member_info/" + member_idx + "/board/1");
            }
        });

        $("#before_index").on("click", function (e) {
            e.preventDefault();
            if (page_num > 1) {
                $(location).attr("href", url2[0] + "/community/view_member_info/" + member_idx + "/board/" + (page_num - 1));
            }
        });

        $("#next_index").on("click", function (e) {
            e.preventDefault();
            if ((page_num - 1) < parseInt(count)) {
                $(location).attr("href", url2[0] + "/community/view_member_info/" + member_idx + "/board/" + (page_num + 1));
            }
        });

        $("#last_index").on("click", function (e) {
            e.preventDefault();
            if ((page_num - 1) < parseInt(count)) {
                $(location).attr("href", url2[0] + "/community/view_member_info/" + member_idx + "/board/" + parseInt((count + 1)));
            }
        });

    });
})();//IIFE

function profileEditModal() {

    var member_idx = $("#session_idx").val();
    var updateText = $(".mypage-section--profile--desc--introduce").attr('data-contents-val');
    var profileContext = $(".mypage-section--profile--desc--introduce");

    if (updateText === undefined || typeof updateText === 'undefined') {
        updateText = '';
    }
    var tag = '<div class="edit-profile-modal modal-form" data-modal-name="profile-edit-modal">\n' +
        '            <form method="post" enctype="application/x-www-form-urlencoded">\n' +
        '                <div class="edit-profile-modal--title">\n' +
        '                    <h1>프로필 수정</h1>\n' +
        '                </div>\n' +
        '                <div class="edit-profile-modal--introduce">\n' +
        '                    <h2>자기소개</h2>\n' +
        '                </div>\n' +
        '                <div class="edit-profile-modal--input">\n' +
        '                    <label>\n' +
        '                        <input type="text" name="profile" placeholder="간단한 자기소개를 작성해주세요.(0/30)" value="' + updateText + '"/>\n' +
        '                    </label>\n' +
        '                </div>\n' +
        '                <div class="edit-profile-modal--buttons">\n' +
        '                    <a href="javascript:void(0)" class="__cancel-button edit-profile" role="button">\n' +
        '                        취소\n' +
        '                    </a>\n' +
        '                    <a href="javascript:void(0)" class="__confirm-button edit-profile" role="button">\n' +
        '                        확인\n' +
        '                    </a>\n' +
        '                </div>\n' +
        '            </form>\n' +
        '        </div>';

    $('body').append(tag);
    $(".edit-profile-modal").innerCenter();
    defaultScreenBlock('profile-edit-modal');
    $(".edit-profile-modal--input label").focus();
    /*취소*/
    $(".edit-profile-modal--buttons .__cancel-button").on('click', function (e) {
        $('div[data-modal-name="profile-edit-modal"]').off('scroll touchmove mousewheel keydown keyup',null);
        $('div[data-modal-name="profile-edit-modal"]').remove();
    });

    /*확인 통신*/
    /*프로필 수정 ajax통신*/
    $('.__confirm-button.edit-profile').on('click', function (e) {
            var editVal = $("input[name='profile']").val();
            $.ajax({
                url: '/mypage/update_introduction',
                type: "POST",
                dataType: 'text',
                data: {
                    member_idx: member_idx,
                    introduction: editVal
                },
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'success') {
                        if (editVal === '') {
                            profileContext.text('간단한 자기소개를 작성해주세요.')

                        } else {
                            profileContext.text(editVal);
                            profileContext.attr('data-contents-val', editVal);
                        }
                        // 모달창 닫기
                        $('div[data-modal-name="profile-edit-modal"]').off('scroll touchmove mousewheel keydown keyup',null);
                        $('div[data-modal-name="profile-edit-modal"]').remove();
                    } else if (data === 'failed') {
                        generateModalInner('fail-modal','실패','수정 권한이 없습니다.', false);
                    }
                },
                complete: function () {

                },
                error: function (e) {
                    generateModalInner('fail-modal',e.status,'ajax통신 실패', false);
                }
                //완료 ajax통신


            });//ajax
    });//click


    /*스크롤 막기*/
    $('div[data-modal-name="profile-edit-modal"]').on('scroll touchmove mousewheel', function(e){
        e.stopPropagation();
        e.preventDefault();
    });

    /*tab키 막기*/
    $('div[data-modal-name="profile-edit-modal"]').on('keydown keyup',function(e){
        var k  = e.keyCode;
        if(k === 9){
            e.stopPropagation();
            e.preventDefault();
        }
    });

}

function mybabyInfoModal() {
    var babyInfo = $("#baby-info");
    var babyGender = babyInfo.attr('data-baby-gender') === 'm' ? '__boy' : '__girl';
    var babyHeight = babyInfo.attr('data-baby-height');
    var babyWeight = babyInfo.attr('data-baby-weight');
    var babyBlood = babyInfo.attr('data-baby-blood');
    var babyName = babyInfo.attr('data-baby-name');
    var babyBirth = babyInfo.attr('data-baby-birth');
    var babyThumb = babyInfo.attr('data-baby-thumb');
    var babyAuth = babyInfo.attr('data-auth');

    if(babyAuth==='false'){
        return ;
    }

    var tag = '<div class="baby-profile-modal modal-form" data-modal-name="baby-info-modal">\n' +
        '            <div class="baby-profile-modal--title">\n' +
        '                <h1>아이정보</h1>\n' +
        '            </div>\n' +
        '            <div class="baby-profile-modal--baby-info">\n' +
        '                <img src="' + babyThumb + '" class="__baby-thumb">\n' +
        '                <div>\n' +
        '                    <div class="baby-profile-modal--baby-info--head">\n' +
        '                        <h2>' + babyName + '</h2><span class="' + babyGender + '">아기의 성별기호입니다.</span>\n' +
        '                        <a href="#" class="__link-baby">대시보드</a>\n' +
        '                    </div>\n' +
        '                    <div class="baby-profile-modal--baby-info--desc">\n' +
        '                        <p>생년월일:' + babyBirth + '</p>\n' +
        '                        <p>키:' + babyHeight + 'cm,몸무게:' + babyWeight + 'kg, 혈액형:' + babyBlood + '</p>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="baby-profile-modal--buttons">\n' +
        '                <a href="javascript:void(0)" class="__confirm-button baby-profile-button" id="baby-profile-confirm--button" role="button">\n' +
        '                    확인\n' +
        '                </a>\n' +
        '            </div>\n' +
        '        </div>';

    $('body').append(tag);
    $(".modal-form").innerCenter();
    defaultScreenBlock('baby-info-modal');


    $("#baby-profile-confirm--button").focus();


    /*메서드 정의*/
    $('#baby-profile-confirm--button').on("click", function (e) {
        $('div[data-modal-name="baby-info-modal"]').off('scroll touchmove mousewheel keydown keyup',null);
        $('div[data-modal-name="baby-info-modal"]').remove();
    });


    /*스크롤 막기*/
    $('div[data-modal-name="baby-info-modal"]').on('scroll touchmove mousewheel', function(e){
        e.stopPropagation();
        e.preventDefault();
    });


    /*tab키 막기*/
    $('div[data-modal-name="baby-info-modal"]').on('keydown keyup',function(e){
        var k  = e.keyCode;
        if(k === 9){
            e.stopPropagation();
            e.preventDefault();
        }
    });


}

function parse_query_string(query) {
    var vars = query.split("&");
    var query_string = {};
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        var key = decodeURIComponent(pair[0]);
        var value = decodeURIComponent(pair[1]);
        // If first entry with this name
        if (typeof query_string[key] === "undefined") {
            query_string[key] = decodeURIComponent(value);
            // If second entry with this name
        } else if (typeof query_string[key] === "string") {
            var arr = [query_string[key], decodeURIComponent(value)];
            query_string[key] = arr;
            // If third or later entry with this name
        } else {
            query_string[key].push(decodeURIComponent(value));
        }
    }
    return query_string;
}

function boardHistroyModal() {
    var boardInfo = $("#user-info");
    var galleryCount = boardInfo.attr('data-gallery-count') || 0;
    var communityCount = boardInfo.attr('data-community-count') || 0;
    var likeCount = boardInfo.attr('data-like-count') || 0;
    var commentCount = boardInfo.attr('data-comment-count') || 0;
    var tag = '<div class="baby-history-modal modal-form" data-modal-name="board-history-modal">\n' +
        '            <div class="baby-history-modal--title">\n' +
        '                <h1>활동내역</h1>\n' +
        '            </div>\n' +
        '            <div class="baby-history-modal--info-graphic">\n' +
        '                <ul>\n' +
        '                    <li class="baby-history-modal--info-graphic--list">\n' +
        '                        <span class="__icon gallery-icn"></span>\n' +
        '                        <h4>갤러리</h4>\n' +
        '                        <span class="__count">' + galleryCount + '개</span>\n' +
        '                    </li>\n' +
        '                    <li class="baby-history-modal--info-graphic--list">\n' +
        '                        <span class="__icon board-icn"></span>\n' +
        '                        <h4>게시글</h4>\n' +
        '                        <span class="__count">' + communityCount + '개</span>\n' +
        '                    </li>\n' +
        '                    <li class="baby-history-modal--info-graphic--list">\n' +
        '                        <span class="__icon like-icn"></span>\n' +
        '                        <h4>받은 좋아요</h4>\n' +
        '                        <span class="__count">' + likeCount + '개</span>\n' +
        '                    </li>\n' +
        '                    <li class="baby-history-modal--info-graphic--list">\n' +
        '                        <span class="__icon comment-icn"></span>\n' +
        '                        <h4>작성댓글</h4>\n' +
        '                        <span class="__count">' + commentCount + '개</span>\n' +
        '                    </li>\n' +
        '                </ul>\n' +
        '            </div>\n' +
        '            <div class="baby-history-modal--buttons">\n' +
        '                <button type="button" role="button">확인</button>\n' +
        '            </div>\n' +
        '        </div>';


    $('body').append(tag);
    $(".modal-form").innerCenter();
    defaultScreenBlock('board-history-modal');
    $(".baby-history-modal--buttons button").focus();


    /*메서드 정의*/
    $(".baby-history-modal--buttons button").on("click", function (e) {
        $('div[data-modal-name="board-history-modal"]').off('scroll touchmove mousewheel keydown keyup',null);
        $('div[data-modal-name="board-history-modal"]').remove();
    });


    /*스크롤 막기*/
    $('div[data-modal-name="board-history-modal"]').on('scroll touchmove mousewheel', function(e){
       e.stopPropagation();
       e.preventDefault();
    });


    /*tab키 막기*/
    $('div[data-modal-name="board-history-modal"]').on('keydown keyup',function(e){
        var k  = e.keyCode;
       if(k === 9){
           e.stopPropagation();
           e.preventDefault();
       }
    });

}


