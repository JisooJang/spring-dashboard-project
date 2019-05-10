(function () {
    $(function () {





        /*대쉬보드에서 헤더는 고정*/
        document.getElementsByTagName('header')[0].style.position = 'relative';

        /*## 일정목록, 일기목록 클릭시 탭 활성화*/
        var tabBtn = $(".__tab-button");

        tabBtn.on('click', function (e) {
            $(this).siblings().removeClass('active');
            $(this).addClass('active');

            if ($(".__schedule-list").hasClass('active')) {
                $('#client-schedule-list').css('display', 'block');
                $('#schedule-handler').css('display', 'block');
                $('#babybook-handler').css('display', 'none');
                $('#babybook-list-container').css('display', 'none');

                var isActive = $(".dashboard-babybook--box--section.babybook-list").hasClass('active');
                if (isActive) {
                    $(".dashboard-babybook--box--section.babybook-list").find('.active').removeClass('active');
                }

                /*육아일기 안 보이게*/
                $("#dashboard-babybook-section").css('display', 'none');
                /*육알일정 섹션 보이기*/
                $("#dashboard-schedule-section").css('display', 'block');


            } else if ($(".__babybook-list").hasClass('active')) {
                $('#babybook-handler').css('display', 'block');
                $('#babybook-list-container').css('display', 'block');
                $('#client-schedule-list').css('display', 'none');
                $('#schedule-handler').css('display', 'none');
                $("#schedule-list-viewbox").html('');

                var isActive = $(".dashboard-babybook--box--section.schedule-list").hasClass('active');
                if (isActive) {
                    $(".dashboard-babybook--box--section.schedule-list").find('.active').removeClass('active');
                }
                /*육알일기 섹션 보이기*/
                $("#dashboard-schedule-section").css('display', 'none');
                /*육아일정은 안 보이게*/
                $("#dashboard-babybook-section").css('display', 'block');

            }
        });

        /*일정,일기 초기화*/
        $(window).on('resize', function (e) {
            if (getCurrentWidth() > 639) {
                $(".dashboard-babybook--box--section.schedule-list").addClass('active');
                $(".dashboard-babybook--box--section.babybook-list").addClass('active');
            }
        });

        /* 그룹초대 카카오 */
        var kakaoInviteBtn = $(".__invite-kakaotalk-button");

        kakaoInviteBtn.on("click", function (e) {
            e.preventDefault();
            inviteKaKaoTalkApi()
        });

        /*##검색 기능*/
        var searchEle = $("input[name='search_member']");
        var searchResultBox = $('.group-search-component-user-list');

        searchEle.on("keyup mouseenter", function (e) {
            var target = this;
            var inputValue = byteCheck(e.target.value, 32, target);
            var searchLen = $(this).val().length;

            if (searchLen < 1) {
                return
            }

            $.ajax({
                type: "GET",
                url: "/searchMember/" + inputValue,
                dataType: "text",
                async: true,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    var jsonData = JSON.parse(data);
                    var object = {};
                    object = jsonData;
                    if (data.length > 2) {
                        // 검색결과에 리스트를 추가
                        searchResultBox.addClass('active');
                        $(".group-search-component-user-list--container").html(renderSearchList(object));
                    } else {
                        // 검색결과에 '검색결과가 없습니다.' 표시
                        // 검색결과에 리스트를 추가
                        searchResultBox.addClass('active');
                        var html = $("#search-template2").html();
                        $(".group-search-component-user-list--container").html(html);
                    }

                },
                complete: function () {

                }
            });	//ajax 통신
        });

        /*그룹 위젯 영역 벗어날 떄 검색 창도 닫힌다.*/

        $('li[data-widget-id="6"]').on('mouseleave', function () {
            $(".group-search-component-user-list").removeClass('active');
        });


        function renderSearchList(data) {
            var groupRequestCheck = data[0].group_request_check === 0 ? '<a href="javascript:void(0)" data-idx="' + (data[0].search_idx) + '" class="btn_search_invite">초대</a>' : '<a href="javascript:void(0)" data-idx="' + (data[0].search_idx) + '" class="btn_search_cancel">취소</a>';
            var isThumbnail = data[0].search_thumbnail !== null ? data[0].search_thumbnail : '/images/default-person.svg';

            var tag = ' <li class="group-search-component-user-list--container--client-info" data-search-idx="' + (data[0].search_idx) + '">\n' +
                '                                <img src="' + (isThumbnail) + '">\n' +
                '                                <div class="__client_nickname">\n' +
                '                                    <div>' + (data[0].search_name) + '</div>\n' +
                '                                </div>\n' + groupRequestCheck +
                '                            </li>';

            return tag;
        }


        //보호자 가족/초대하기 신청 버튼 클릭
        $(document).on("click", ".btn_search_invite", function (e) {
            e.preventDefault();
            var get_idx = $(this).attr("data-idx");
            var session_idx = $("#session_idx").val();
            $.ajax({
                type: "GET",
                url: "/request_group/" + get_idx,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'success') {
                        //소켓통신연결
                        group_request_added(session_idx, get_idx);

                        generateModal('success-invitation-modal', '성공', '상대방에게 그룹요청을 하였습니다.', false);
                        $(e.target).parent().siblings(".search_info.left").find("div:nth-child(2)").find(".invitation_status").show();
                        $(e.target).removeClass('btn_search_invite');
                        $(e.target).addClass('btn_search_cancel');
                        $(e.target).text('취소');
                    }
                    else if (data === "duplicated") {
                        generateModal('success-invitation-modal', '알림', '이미 그룹요청을 한 회원입니다.', false);
                    } else if (data === "infant" || data === "no_infant") {
                        generateModal('fail-invitation-modal', '알림', '그룹요청에 앞서 아이정보를 등록해주세요.', false);
                    } else if (data === "fulled") {
                        generateModal('fail-invitation-modal', '알림', '신청하신 그룹은 정원이 다찼습니다.(5명)', false);
                    } else if (data === "receiver_failed") {
                        generateModal('fail-invitation-modal', '알림', '이미 그룹이 존재하는 회원입니다.', false);
                    } else if (data === "have_infant") {
                        generateModal('fail-invitation-modal', '알림', '아이 정보를 등록한 회원은 그룹원으로 초대할 수 없습니다.', false);
                    } else if (data === "failed") {
                        generateModal('fail-invitation-modal', '실패', '서버오류입니다. 잠시 후에 다시 시도해 주세요.', false);
                    } else if (data === "search_idx_null") {
                        generateModal('fail-invitation-modal', '실패', '잘못된 접근 방법입니다.', false);
                    } else if (data === "no_member") {
                        generateModal('fail-invitation-modal', '실패', '존재하지 않는 회원입니다.', false);
                    } else {
                        generateModal('fail-invitation-modal', '실패', '잘못된 접근 방법입니다.', false);
                    }
                },
                error: function (e) {
                    generateModal('fail-invitation-modal', e.status, 'ajax 통신 에러', false);
                }
            });
        });

        /*보호자 가족 초대요청 취소하기*/
        $(document).on("click", ".btn_search_cancel", function (e) {
            e.preventDefault();
            var requester = $("#session_idx").val();
            var recipient = $(this).attr("data-idx");
            $.ajax({
                type: "GET",
                url: "/cancel_group_request/" + requester + "/" + recipient,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'success') {
                        $(e.target).removeClass('btn_search_cancel');
                        $(e.target).parent().siblings(".search_info").find("div:nth-child(2)").find(".invitation_status").hide();
                        $(e.target).addClass('btn_search_invite');
                        $(e.target).text('신청');

                        // 소켓 통신으로 기존 데이터 삭제되게 리로드
                        group_request_added(requester, recipient);
                    } else {
                        generateModal('fail-invitation-modal', data, '서버 통신 에러', false);
                    }
                },
                error: function (e) {
                    generateModal('fail-invitation-modal', e.status, 'ajax 통신 에러', false);
                }
            });
        });


        /*육아일기 전체 정보 클릭시 modal창 생성*/
        $("#__babybook-more-info").on('click', function (e) {

            var screenBlock = '<div style="position: fixed;top:0;left:0;width:100%;height:150%;background-color: #000;opacity: .5; z-index: 5;" class="babybook-blocker"></div>';
            $("body").append(screenBlock);
            if ($(".dashboard-babybook--main-modal").hasClass('active')) {
                $(".dashboard-babybook--main-modal").removeClass('active');
            }
            $(".dashboard-babybook--main-modal").addClass('active');

            $(".dashboard-babybook--main-modal").innerCenter();
            $(".babybook-blocker").on('click', function () {
                $(".dashboard-babybook--main-modal").removeClass('active');
                $(this).remove();
                $(this).off('click');
                $('.babybook-blocker').off('scroll touchmove mousewheel', null);
            });

        });
        /*육아일기 전체 정보 클릭시 modal창 생성*/


        /*input text의 글자수 카운트*/
        galleryTextCount('#dashboard-schedule-title');
        galleryTextCount('#dashboard-babybook--container--title');
        galleryTextCount('#dashboard-babybook--container--contents');


        /*대시보드 - 헤더 달력*/
        $('.dashboard--header--date__calendar').datepicker({
            inline: false,
            language: 'en',
            autoClose: true,
            onSelect: function onSelect(fd, date) {

                /*달력 생성*/
                /*년도*/
                var cYear = date.getFullYear();
                /*월*/
                var cMonth = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1;
                /*일*/
                var cDay = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
                /*선택 날짜*/
                var cToday = [cYear, cMonth, cDay].join('-');

                if (cToday > today) {
                    generateModal('오늘 날짜 이후로는 정보가 없습니다.');
                    return;

                } else {
                    currentDateView.text(cToday);
                    var infant_idx = $("#current-baby-info").attr('data-baby-idx');
                    /*선택 날짜로 이동*/
                    var url = "/select/" + cToday + "/" + infant_idx;
                    window.location.href = location.href + url;
                }
            }
        });

        /*서버에서 보낸 날짜와 오늘 날짜가 같다면 다음 버튼 비활성화*/
        if (selected_date === today) {
            $(".dashboard--header--date--navigation__next").css('opacity', .3);
        }

        /*이전 날짜 버튼 클릭*/
        $(document).on('click', ".dashboard--header--date--navigation__prev", function (e) {
            e.preventDefault();
            var url = "/dashboard/select_before/" + selected_date;
            window.location.href = url;
        });

        /*다음 날짜 버튼 클릭*/
        $(document).on('click', ".dashboard--header--date--navigation__next", function (e) {
            e.preventDefault();
            if (today === selected_date) {
                return;
            } else {
                var url = "/dashboard/select_next/" + selected_date;
                window.location.href = url;
            }
        });


        /*태그 이벤트*/

        var hashInput = $("#hash-tag-input");

        /*태그입력 태그에 포커스후에 키보드 입력시 태그 추가*/

        hashInput.on('keydown', function (e) {

            var k = e.keyCode;
            if ((k === 13 || k === 32 || k === 9)) {
                e.preventDefault();
                var hashVal = $(this).val();
                var tagCount = document.querySelectorAll("input[name=hashtag]");
                var hashTag = '<a href="javascript:void(0)" class="__hash-tag"><div>#' + hashVal + '<input type="hidden" name="hashtag" value="' + hashVal + '"></div><span class="__remove-tag"></span></a>'

                if (tagCount.length > 19) {
                    alert('해쉬태그는 20개 까지만 작성가능합니다.');
                    $(this).val('');
                    return;
                } else if (hashVal.length < 1) {
                    return;
                }

                var hashPos = $("#hash-station");
                $(hashTag).insertBefore(hashPos);
                $(this).val('');
            }
        });

        /*해쉬 태그 삭제*/
        $(document).on('click', '.__hash-tag', function (e) {
            $(this).remove();
        });

        /*추가버튼으로 해쉬태그 추가*/
        $(".__add-tags").on('click', function () {
            var hashVal = hashInput.val();
            var tagCount = document.querySelectorAll("input[name=hashtag]");
            var hashTag = '<a href="javascript:void(0)" class="__hash-tag"><div>#' + hashVal + '<input type="hidden" name="hashtag" value="' + hashVal + '"></div><span class="__remove-tag"></span></a>'

            if (tagCount.length > 19) {
                alert('해쉬태그는 20개 까지만 작성가능합니다.');
                $(hashInput).val('');
                return;
            } else if (hashVal.length < 1) {
                return;
            }

            var hashPos = $("#hash-station");
            $(hashTag).insertBefore(hashPos);
            $(hashInput).val('');
        });

    });//jqb


    /*이메일로 초대 버튼 클릭시*/
    var inviteEmailBtn = $(".__invite-email-button");

    inviteEmailBtn.on('click', function (e) {

        var emailModal = '<div class="mail-invite-form modal-form" data-modal-name="email-invitation-modal">\n' +
            '    <form action="/" method="post" enctype="multipart/form-data">\n' +
            '        <div class="mail-invite-form--title">\n' +
            '            <span class="email-icon"></span>\n' +
            '            <h2 class="modal-title" data-i18n="emailInvitation.title"></h2>\n' +
            '        </div>\n' +
            '        <div class="mail-invite-input-box">\n' +
            '            <a href="#"></a>\n' +
            '            <div>\n' +
            '                <input type="email" id="invite-email" data-i18n="[placeholder]emailInvitation.placeholder"placeholder="이메일 주소를 입력해주세요." maxlength="50"/>\n' +
            '                <div class="__filter"></div>\n' +
            '                <span class="__close-button"></span>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '        <label for="invite-email" class="__validation-text">잘못된 입력입니다.</label>\n' +
            '        <div class="mail-invite-buttons">\n' +
            '            <button type="button" role="button" class="__invite-cancel-button" data-modal-name="email-invitation-modal" data-i18n="emailInvitation.cancel"></button>\n' +
            '            <button type="button" role="button" class="__invite-accept-button" data-modal-name="email-invitation-modal" data-i18n="emailInvitation.confirm"></button>\n' +
            '        </div>\n' +
            '    </form>\n' +
            '</div>'


        var screenblock = '<div class="modal-screen-block" data-modal-name="email-invitation-modal"></div>';
        $('body').append(screenblock);
        $("body").append(emailModal);
        $(".mail-invite-form").innerCenter();

        Localize(getCookie('lang'), $(".mail-invite-form"));


        $(".modal-screen-block").on('click', function (e) {
            var modalName = $(this).attr('data-modal-name');
            $('div[data-modal-name=' + modalName + ']').remove();
        });

        /*이메일로 가족초대*/

        var emailEle = $("#invite-email");
        var emailInitBtn = $(".mail-invite-input-box .__close-button");
        var sendInviteBtn = $(".mail-invite-buttons .__invite-accept-button");
        var emailReg = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
        var validationText = $(".__validation-text");

        emailEle.on('focus', function (e) {

            $(this).on('keydown', function (e) {
                var k = e.keyCode;
                var tagVal;
                var tagValueArray = [];
                var limitCount = 0;
                var emailEle = $("input[name='invite_email']");
                tagVal = e.target.value;
                validationText.removeClass('active');

                if ((k === 13 || k === 32 || k === 9 || k === 188)) {
                    e.preventDefault();

                    if (emailEle.length > 4) {
                        validationText.addClass('active');
                        validationText.text('이메일은 한 번에 5명까지만 보낼 수 있습니다.');
                        return;
                    } else if (tagVal.length < 1) {
                        validationText.addClass('active');
                        validationText.text('이메일을 입력해주세요.');
                        return;
                    }

                    if (tagVal.match(emailReg) != null) {
                        var newTagBox = '<a href="javascript:void(0)" class="email-tag"><div>' + tagVal + '<input type="hidden" name="invite_email" value="' + tagVal + '"/></div><span class="__remove-tag"></span></a>';

                        /*배열에 값 배열*/
                        tagValueArray.push(tagVal);
                        /*히든 input에 값 넣기(tag)*/

                        /*태그가 넣어질 기준 태그*/
                        var tagBox = document.querySelectorAll('.mail-invite-input-box a');
                        /*같은 태그가 여러개 추가되니까 그 주엥서 마지막 태그*/
                        var lastTag = tagBox[tagBox.length - 1];

                        lastTag.insertAdjacentHTML('beforebegin', newTagBox);

                        validationText.removeClass('active');
                        $(this).val("");
                    } else {
                        validationText.text('잘못된 입력입니다.');
                        validationText.addClass('active');

                    }

                }
            });//key down
        }).on('keydown', function (e) {
            var targetVal = $(this).val();
            if (targetVal.length > 0) {
                emailInitBtn.addClass('active')
            } else {
                emailInitBtn.removeClass('active');
            }
        }).on('keyup', function (e) {
            var targetVal = $(this).val();
            console.log(targetVal.length);
            if (targetVal.length > 0) {
                emailInitBtn.addClass('active')
            } else {
                emailInitBtn.removeClass('active');
            }
        });


        //현재 입력 글 초기화버튼(x) 클릭시
        emailInitBtn.on('click', function (e) {
            emailEle.val('');
            $(this).removeClass('active');
            emailEle.focus();
        });

        /*초대하기 버튼 클릭시*/
        sendInviteBtn.on('click', function (e) {

            /*인풋에 작성 중이던 밸류 가져오기*/
            var tagVal = $("#invite-email").val();
            var tagValueArray = [];
            var emailEle = $("input[name='invite_email']");

            validationText.removeClass('active');

            if (emailEle.length > 4) {
                validationText.addClass('active');
                validationText.text('이메일은 한 번에 5명까지만 보낼 수 있습니다.');
                return;
            }

            if (tagVal.match(emailReg) != null) {
                var newTagBox = '<a href="javascript:void(0)" class="email-tag"><div>' + tagVal + '<input type="hidden" name="invite_email" value="' + tagVal + '"/></div><span class="__remove-tag"></span></a>';

                /*배열에 값 배열*/
                tagValueArray.push(tagVal);
                /*히든 input에 값 넣기(tag)*/

                /*태그가 넣어질 기준 태그*/
                var tagBox = document.querySelectorAll('.mail-invite-input-box a');
                /*같은 태그가 여러개 추가되니까 그 주엥서 마지막 태그*/
                var lastTag = tagBox[tagBox.length - 1];

                lastTag.insertAdjacentHTML('beforebegin', newTagBox);

                validationText.removeClass('active');
                $("#invite-email").val('');
                emailInitBtn.removeClass('active');
            }


            /*추가한 이메일들의 주소값이 담길 배열*/
            var emailVal = [];
            /*반복문 돌면서 배열에 다 넣기*/

            $("input[name=invite_email]").each(function (idx, ele) {
                emailVal.push(ele.value);
                console.log(ele.value);
            });

            /*배열에 값이 없다면 return*/
            if (emailVal.length < 1) {
                validationText.text('이메일을 입력해주세요.');
                validationText.addClass('active');
                return;
            } else {

                $.ajax({
                    url: '/dashboard/invite_family',
                    type: "POST",
                    dataType: 'text',
                    data: {
                        invite_email: emailVal.join()
                    },
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === "success") {
                            $("div[data-modal-name='email-invitation-modal']").remove();
                            generateModalInner('success-invitation-modal', '초대 완료', '성공적으로 초대하였습니다.');
                            $(".email-tag").remove();
                        } else if (data === "email_length_failed") {
                            generateModalInner('fail-invitation-modal', '실패', '메일 초대는 최대 5명까지 가능합니다.');
                        } else if (data.indexOf("already_member:") >= 0) {
                            generateModalInner('fail-invitation-modal', '실패', data.split(":")[1] + " 계정은 이미 가입된 회원입니다.");
                        } else if (data === 'group_size_error') {
                            generateModalInner('fail-invitation-modal', '실패', "그룹은 최대 5명까지만 가입가능합니다.");
                        }
                    },
                    complete: function () {
                    },
                    error: function (e) {
                        generateModalInner('fail-invitation-modal', e.status, "AJAX통신 에러");
                    }
                })
            }
        });


        /*이메일 태그 삭제 버튼(x)클릭시*/
        $(document).on('click', '.email-tag', function (e) {
            $(this).fadeOut(400, function () {
                $(this).remove();
            });
        });


        /*취소 버튼 클릭시*/
        $(".__invite-cancel-button").on('click', function (e) {
            var modalName = $(this).attr('data-modal-name');
            $('div[data-modal-name=' + modalName + ']').remove();
            $(".__invite-cancel-button").off('click', null);
        });

        /*이메일로 가족 초대*/
    });


    /*이메일로 초대 버튼 클릭시*/

    var deviceRegiSter = $(".dashboard--header--devices--setting__register");

    deviceRegiSter.on('click', function (e) {
        var deviceTag = '<div class="device-register-box modal-form">\n' +
            '        <div>\n' +
            '            <input type="text" id="serial-form" name="serial_number" placeholder="시리얼 넘버를 입력해주세요.(‘-’없이)"/>\n' +
            '            <span class="__initialize-input-button"></span>\n' +
            '        </div>\n' +
            '        <div>\n' +
            '            <a href="#" role="button" class="__register-serial-button">\n' +
            '                <span>기기등록</span>\n' +
            '            </a>\n' +
            '            <a href="javascript:void(0)" role="button" class="__close-serail-button">\n' +
            '                <span>취소</span>\n' +
            '            </a>\n' +
            '        </div>\n' +
            '        <label for="serial-form" class="__validation-text">잘못된 입력입니다.</label>\n' +
            '    </div>'

        $("body").prepend(deviceTag);

        /*가운데에 정렬*/
        $(".modal-form").innerCenter();

        /*스크린 블락 생성*/
        generateScreenBlock();

        $("#serial-form").focus();

        $("#serial-form").on('keyup', function () {
            var serialVal = $(this).val();

            if (serialVal.length > 0) {
                $(".__initialize-input-button").addClass('active');
            } else {
                $(".__initialize-input-button").removeClass('active');
            }

        });

        $(".__initialize-input-button").on('click', function () {
            $("#serial-form").val('');
            $("#serial-form").focus();
            $(this).removeClass('active');
        });


        /*창 닫기*/
        $(".__close-serail-button").on('click', function (e) {
            $(".modal-form").remove();
            $(".screen_block").remove();

        });

        /*ajax통신 시리얼번호*/

        $(".__register-serial-button").on('click', function (e) {
            alert('시리얼 번호 ajax통신 후 페이지 리로드');
            location.reload();
        })


    });
    /*기기 등록 모달창 띄우기*/

    /*기기 연결 해제 모달창 띄우기*/
    var unLinkDevice = $(".dashboard--header--devices--setting__cancel");

    unLinkDevice.on('click', function (e) {
        var isActive = $(this).hasClass('active');
        if (!isActive) {
            return;
        } else {
            generateModal('연결을 해제하시겠습니까?', true, function (e) {
                alert("이곳에서 연결이 해제된 ajax통신과 reload를..");
            });
        }
    });

    var todayCalendar = new Date();
    var todayYear = todayCalendar.getFullYear();
    /*월*/
    var todayMonth = todayCalendar.getMonth() + 1 < 10 ? '0' + (todayCalendar.getMonth() + 1) : todayCalendar.getMonth() + 1;
    /*일*/
    var todayDate = todayCalendar.getDate() < 10 ? '0' + todayCalendar.getDate() : todayCalendar.getDate();
    /*선택 날짜*/
    var today = [todayYear, todayMonth, todayDate].join('-');

    /*h2태그 에서 이동한 날짜를 가져와서 담음, 처음 접속시에는 오늘 날짜*/
    var selected_date = $('.dashboard--header--date--navigation time').text();

    /*현재 선택된 아이 태그*/
    var currentBaby = $(".current-baby");


    if (today === selected_date) {
        $(".dashboard--header--date__date-navigation__next").remove();
    }

    /*좌측 상단 달력 옆에 현재 날짜*/
    var currentDateView = $(".dashboard--header--date__date-navigation>h2");

    // /*현재 연결된 디바이스 클릭시*/
    // var connectedDevice = $(".dashboard--header--devices__connected img.active");
    // connectedDevice.on('click', function (e) {
    //     $(this).toggleClass('selected');
    //     /*연결해제 버튼 활성화*/
    //     $(".dashboard--header--devices--setting__cancel").toggleClass('active');
    //
    // });
    // /*현재 연결된 디바이스 클릭시*/


    // /*아이 선택시 아이 변경*/
    // $(".dashboard--header--babies__my-baby").on('click', function (e) {
    //     e.preventDefault();
    //     getBabyAjax();
    // });

    /*좌측 상단 아이 썸네일 클릭시 형제,자매 선택창 보여주기*/
    var babyIcon = $(".baby-selected");
    var babyListMenu = $('.dashboard--header--babies>ul');

    babyIcon.on('click', function (e) {
        e.preventDefault();
        babyListMenu.toggleClass('active');
    });//click

    /*현재 client가 가진 아이*/
    var currentBaby = $(".current-baby");

    /*아이 등록 버튼 클릭시*/
    $('.baby-selected, .__baby-add-button').on('click', function (e) {
        e.preventDefault();
        var babyIdx = $(e.target).attr('data-baby-idx');
        var isBabyOn = $(this).hasClass('baby-active');
        if (isBabyOn) {

            var updateBtn  = $(".__baby-update-button");
            var state = {
                url: updateBtn.attr('data-baby-img'),
                name: updateBtn.attr('data-baby-name'),
                gender: updateBtn.attr('data-baby-gender'),
                height:updateBtn.attr('data-baby-height'),
                weight: updateBtn.attr('data-baby-weight'),
                year: updateBtn.attr('data-baby-year'),
                month:updateBtn.attr('data-baby-month'),
                date:updateBtn.attr('data-baby-date'),
                blood:updateBtn.attr('data-baby-blood'),
                idx:updateBtn.attr('data-baby-idx')
            };

            updateBabyInfo(state);
        } else {
            registerBabyInfo();

        }
    });//아기 등록

    /*아기 수정 버튼 클릭시*/
    $(".__baby-update-button").on('click', function (e) {

        var state = {
            url: $(this).attr('data-baby-img'),
            name: $(this).attr('data-baby-name'),
            gender: $(this).attr('data-baby-gender'),
            height: $(this).attr('data-baby-height'),
            weight: $(this).attr('data-baby-weight'),
            year: $(this).attr('data-baby-year'),
            month: $(this).attr('data-baby-month'),
            date: $(this).attr('data-baby-date'),
            blood: $(this).attr('data-baby-blood'),
            idx: $(this).attr('data-baby-idx')
        };
        updateBabyInfo(state)
    });//아기 수정


    /*그룹 관리 옵션 버튼*/
    $(".group-widget--head--setting").on('click', function () {
        var isActive = $(this).hasClass('active');
        if (isActive) {
            $(".option-menu-select").toggleClass('active');
        }
    });

    /*그룹 관리 버튼을 클릭하면 삭제버튼 on*/

    $(".__menu-handle-group").on('click', function (e) {

        var isActive = $(".my-group-list").length > 0;

        if (isActive) {
            // 그룹원이 존재하면 아래 코드 실행, 없으면 alert로 경고 문구 띄워줌 : "편집할 그룹원이 없습니다."
            $('.__unscribe-group-member').toggleClass('active');
        } else {
            generateModalInner('error-modal', '알림', '편집할 그룹원이 없습니다.', false);
        }


    });


    /*

    이름 : 그룹 탈퇴 메서드
    기능: 그룹 탈퇴 버튼 클릭하면 그룹장이라면 그룹이 사라지고, 그룹원이라면 그룹을 나간다.*/
    $(".__menu-leave-group").on('click', function (e) {
        e.preventDefault();
        $.ajax({
            type: "GET",
            url: "/delete_group_self",
            dataType: "text",
            async: true,
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                if (data === 'not_this_group') {
                    alert("잘못된 그룹 정보 입니다.");
                } else if (data === 'group_auth_failed') {
                    alert("잘못된 그룹 정보 입니다.");
                } else if (data === 'member_leave') {
                    alert("탈퇴가 완료되었습니다.");
                    window.location.replace('/dashboard');

                    // 해당 그룹원 뷰에서 제거시킬것
                } else if (data === 'admin_leave') {
                    alert("그룹 전체가 삭제처리 되었습니다.");
                    window.location.replace('/dashboard');
                    // 그룹 전체 삭제 처리
                }
                else if (data === 'no_group') {
                    alert("그룹이 존재하지 않습니다.");
                } else if(data === 'delete_group') {
                	alert("모든 그룹원이 삭제되어 그룹이 삭제 처리 되었습니다.");
                } else {
                    alert("잘못된 통신입니다.");
                }

            },
            complete: function () {

            },
            error: function (e) {
                generateModalInner('ajax-error', e.status, '잘못된 통신입니다.', false);
            }
        });


    });


    /*그룹원 삭제 메서드(그룹장만 가능)*/
    $(".__unscribe-group-member").on('click', function (e) {

        //그룹원 idx
        var memberIdx = $(this).attr('data-member-idx');

        if (isEmpty(memberIdx)) {
            return;
        }

        generateModal('delete-request-modal', '삭제', '정말로 삭제하시겠습니까?', true, function () {
            $.ajax({
                type: "GET",
                url: "/delete_group_member/" + memberIdx,
                dataType: "text",
                async: true,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    if (data === 'not_group_leader') {
                        alert("그룹장만 삭제 권한을 가질 수 있습니다.");
                    } else if (data === 'group_auth_failed') {
                        alert("잘못된 통신입니다. 삭제 권한이 없습니다.");
                    } else if (data === 'success') {
                        alert("그룹원 삭제가 완료되었습니다.");
                        $(".my-group-list[data-member-idx=" + memberIdx + "]").remove();

                        // 해당 그룹원 뷰에서 제거시킬것
                    } else if (data === 'delete_group') {
                    	alert("모든 그룹원이 삭제되어 그룹이 삭제 처리 되었습니다.");
                        window.location.replace('/dashboard');
                    } else {
                        alert("잘못된 통신입니다.");
                    }

                },
                complete: function () {

                },
                error: function (e) {
                    generateModalInner('ajax-error', e.status, '잘못된 통신입니다.', false);
                }
            });//end ajax
        });


    });//end click


})();


function byteCheck(str, lengths) {
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
            console.log('제한 문자수 넘김');
            break; // 제한 문자수를 넘길경우.
        } else {
            newStr = newStr + nv;
            console.log('현재: ' + newStr);
        } //if~else
    }
    return newStr;
}

function galleryTextCount(cssSelector) {
    $(cssSelector).on("keydown", function (e) {
        var currentText = $(this).val().length;
        var maxLength = $(this).next(".dashboard-babybook--container__count").attr('data-cs-maxlength');
        var contentName = $(this).prev("label[data-cs-title]").attr('data-cs-title');
        $(this).next(".dashboard-babybook--container__count").text(currentText + " / " + maxLength);
        if (currentText > maxLength) {

            $(this).val('');
            return false;
        }
    }).on('blur', function () {
        var currentText = $(this).val().length;
        var maxLength = $(this).next(".dashboard-babybook--container__count").attr('data-cs-maxlength');
        var contentName = $(this).prev("label[data-cs-title]").attr('data-cs-title');
        $(this).next(".dashboard-babybook--container__count").text(currentText + " / " + maxLength);
        if (currentText > maxLength) {
            $(this).val('');
            return false;
        }
    });
}

/*시간을 00으로 초기화*/
function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear(),
        hour = d.getHours(),
        min = 00

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
    if (hour.length < 2) hour = '0' + hour;

    return [year, month, day].join('-') + ' ' + '' + hour + ':' + min;
}




