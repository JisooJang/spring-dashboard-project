(function () {
    $(function () {

        /*헤더*/

        var LinkBar = $(".navigation__effect");

        /*메뉴 링크바*/
        $(".link_device").on('mouseenter', function (e) {
            if ($(this).find(LinkBar).hasClass('active')) {
                return;
            } else {
                LinkBar.removeClass('active');
                $(this).find(LinkBar).addClass('active');

            }
        }).on('mouseleave', function (e) {
            LinkBar.removeClass('active');
        });

        var menuIndex = 4;
        /*유저 정보창 클릭시 마이페이지 창 보여줌*/
        $(".header--menu__client-info").on('click', function () {
            $(".header--menu__client-info>div").css('z-index', menuIndex).toggleClass('active');
            menuIndex++;
        });

        /*문서 너비*/
        var docWidth = $(document).outerWidth();
        /*브라우저 가로 크기*/
        /*헤더 종모양 아이콘*/
        var headerBell = $(".header--menu__alarm");
        /*641px이상에서의 메시지 창*/
        var messageBox = $(".header--menu__alarm__message");
        /*640px이하에서의 메시지창*/
        var mobileMessageBox = $(".header--mobile__alarm__message");
        /*모바일 네비게이션 창*/
        var mobileNav = $(".header--mobile");
        /*햄버거 버튼*/
        var hamburgerBtn = $('.header--hamburger__menu');
        /*서브 메뉴들*/
        var subLink = $(".header--sublink");
        /*슬라이드 되어 올라오는 메뉴*/
        var slideNav = $(".slide-nav li");
        /*641px 이상 클라이언트 정보 메뉴*/
        var clientInfoBox = $(".header--menu__client-info > div");

        /*윈도우즈 리사이즈시*/
        $(window).on('resize', function () {
            docWidth = document.body.offsetWidth;
            mobileMessageBox.removeClass('active');
            messageBox.removeClass('active');
            mobileNav.removeClass('active');
            mobileNav.stop().animate({
                height: '0%'
            }, 600, 'easeInQuint');
            hamburgerBtn.removeClass('active');
            clientInfoBox.removeClass('active');
            subLink.css('display', 'none');
        }); //resize
        /*리사이징 및 스크롤시 모달창 가운데에*/
        $(window).on('resize', function () {
            $(".modal-form").innerCenter();

        }).on('scroll', function () {
            $(".modal-form").innerCenter();
        });


        /*알람 아이콘 클릭 메서드*/
        headerBell.on('click', function (e) {
            subLink.css('display', 'none');
            if (docWidth <= 768) {
                mobileMessageBox.css('z-index', menuIndex).toggleClass('active');
                menuIndex++;
            } else {
                messageBox.css('z-index', menuIndex).toggleClass('active');
                menuIndex++;
            }//end else~if
        });//on click


        // /*서브링크 마우스 오버 및 리브 이벤트*/
        $(".link_device.devices").on('mouseenter', function () {
            subLink.fadeIn();
        });

        subLink.on('mouseleave', function () {
            subLink.fadeOut();
        });


        /*헤더의 햄버거 메뉴 버튼 클릭시*/
        $('.header--hamburger').on('click', function () {
            subLink.css('display', 'none');
            $(document).clearQueue();

            if (mobileNav.hasClass('active')) {
                mobileNav.toggleClass('active');
                $(this).toggleClass('active');

                mobileNav.stop().animate({
                    height: '0%'
                }, 600, 'easeInQuint', function () {
                    slideNav.removeClass('active');
                    $(".header--mobile--user-info__head").clearQueue().stop().fadeOut();
                });

            } else {
                mobileNav.toggleClass('active');
                $(this).toggleClass('active');
                slideNav.each(function (idx, ele) {
                    $(this).clearQueue();
                    setTimeout(function () {
                        ele.classList.add('active');
                    }, idx * 77);
                });

                $(".header--mobile--user-info__head").clearQueue().stop().fadeIn(2500);
                mobileNav.stop().animate({
                    height: '100%'
                }, 600, 'easeInQuint');
            }


        });


        /*헤더 끝*/


        /*풋터 쿠키값에 따른 설정*/
        var footerEmail = $("#email-subscription-input");
        inputFilled(footerEmail);
        removeInputVal($(".__remove-input-button"));


        /* 메일 구독 서비스 ajax */
        var subsButton = $("#email-subscription-form .__subscribe-email-button");

        subsButton.on("click", function (e) {
            e.preventDefault();
            var validations = new Validations();
            var subscribedEmail = footerEmail.val();

            var isValidEmail = validations.checkEmail(subscribedEmail);

            if (!isEmpty(subscribedEmail) && isValidEmail) {
                $.ajax({
                    type: "POST",
                    url: "/subscribe_email",
                    data: {
                        email: subscribedEmail
                    },
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === "success") {

                            if(getCookie('lang')==='ko'){
                                generateModal('email-subscribe-success-modal', "성공", '메일 구독이 성공적으로 완료되었습니다.', false);
                            } else if(getCookie('lang')==='zh'){
                                generateModal('email-subscribe-success-modal', "성공", '메일 구독이 성공적으로 완료되었습니다.', false);
                            } else if(getCookie('lang')==='ja'){
                                generateModal('email-subscribe-success-modal', "성공", '메일 구독이 성공적으로 완료되었습니다.', false);
                            } else if(getCookie('lang')==='en'){
                                generateModal('email-subscribe-success-modal', "성공", '메일 구독이 성공적으로 완료되었습니다.', false);
                            } else {
                                generateModal('email-subscribe-success-modal', "성공", '메일 구독이 성공적으로 완료되었습니다.', false);
                            }

                        } else if (data === "input_failed") {
                            if(getCookie('lang')==='ko'){
                                generateModal('email-subscribe-fail-modal', "실패", '메일 구독에 실패했습니다.', false);
                            } else if(getCookie('lang')==='zh'){
                                generateModal('email-subscribe-fail-modal', "실패", '메일 구독에 실패했습니다.', false);
                            } else if(getCookie('lang')==='ja'){
                                generateModal('email-subscribe-fail-modal', "실패", '메일 구독에 실패했습니다.', false);
                            } else if(getCookie('lang')==='en'){
                                generateModal('email-subscribe-fail-modal', "실패", '메일 구독에 실패했습니다.', false);
                            } else {
                                generateModal('email-subscribe-fail-modal', "실패", '메일 구독에 실패했습니다.', false);
                            }
                        } else {
                            generateModal('email-subscribe-fail-modal', "실패", '잠시 후에 다시 시도해주세요.', false);
                        }
                        footerEmail.val("");
                    },
                    error: function (e) {
                        if(getCookie('lang')==='ko'){
                            generateModal('ajax-error-modal', e.status, '통신에러가 발생했습니다.', false);
                        } else if(getCookie('lang')==='zh'){
                            generateModal('ajax-error-modal', e.status, '통신에러가 발생했습니다.', false);
                        } else if(getCookie('lang')==='ja'){
                            generateModal('ajax-error-modal', e.status, '통신에러가 발생했습니다.', false);
                        } else if(getCookie('lang')==='en'){
                            generateModal('ajax-error-modal', e.status, '통신에러가 발생했습니다.', false);
                        } else {
                            generateModal('ajax-error-modal', e.status, '통신에러가 발생했습니다.', false);
                        }
                    },
                    complete: function () {
                    }
                });//end ajax
            } else if (!isValidEmail) {
                if(getCookie('lang')==='ko'){
                    generateModal('error-sub-modal', '실패', '적합한 이메일을 입력해주세요.', false);
                } else if(getCookie('lang')==='zh'){
                    generateModal('error-sub-modal', '실패', '적합한 이메일을 입력해주세요.', false);
                } else if(getCookie('lang')==='ja'){
                    generateModal('error-sub-modal', '실패', '적합한 이메일을 입력해주세요.', false);
                } else if(getCookie('lang')==='en'){
                    generateModal('error-sub-modal', '실패', '적합한 이메일을 입력해주세요.', false);
                } else {
                    generateModal('error-sub-modal', '실패', '적합한 이메일을 입력해주세요.', false);
                }

            } else {

                if(getCookie('lang')==='ko'){
                    generateModal('error-sub-modal', '실패', '이메일을 입력해주세요.', false);
                } else if(getCookie('lang')==='zh'){
                    generateModal('error-sub-modal', '실패', '이메일을 입력해주세요.', false);
                } else if(getCookie('lang')==='ja'){
                    generateModal('error-sub-modal', '실패', '이메일을 입력해주세요.', false);
                } else if(getCookie('lang')==='en'){
                    generateModal('error-sub-modal', '실패', '이메일을 입력해주세요.', false);
                } else {
                    generateModal('error-sub-modal', '실패', '이메일을 입력해주세요.', false);
                }
            }
        });

        /*언어 셋팅*/
        var locationLang = {
            korean: 'ko',
            english: 'en',
            japanese: 'ja',
            chinese: 'zh'
        };

        /*쿠키에서 가져온 쿠키값이 해당 언어라면 풋터 이미지 변경*/



        var myLang = getCookie('lang');
        var currentLocale = $(".__current-lang");

        if (myLang === locationLang.korean) {
            currentLocale.find('.locale-container--flag').addClass('korea-flag');
            currentLocale.find('.locale-container--country').text('한국');
        } else if (myLang === locationLang.english) {
            currentLocale.find('.locale-container--flag').addClass('usa-flag');
            currentLocale.find('.locale-container--country').text('US');
        } else if (myLang === locationLang.japanese) {
            currentLocale.find('.locale-container--flag').addClass('japan-flag');
            currentLocale.find('.locale-container--country').text('日本');
        } else if (myLang === locationLang.chinese) {
            currentLocale.find('.locale-container--flag').addClass('china-flag');
            currentLocale.find('.locale-container--country').text('中國');
        }

        /*쿠키 가져온 값*/

        /*국가선택 메뉴에 마우스 올렸을 때*/

        $("#footer-language-box").stop().hover(function () {
            $('#footer-language-box ol').addClass('active');
            $('#footer-language-box ul').addClass('active');
            $('#footer-language-box .locale-container--down-arrow').addClass('active');
        }, function () {
            $('#footer-language-box ol').removeClass('active');
            $('#footer-language-box ul').removeClass('active');
            $('#footer-language-box .locale-container--down-arrow').removeClass('active');
        });



        /*언어 셀렉트 박스 선택시// locale 관련 설정*/
        $(document).on('click', '#footer-language-box .footer-language-box--list', function (e) {
            e.preventDefault();
            var cookieToday = new Date();
            var expiryDate = new Date(cookieToday.getTime() + (365 * 86400000)); // a year

            var currentLang = $(this).attr('data-select-value');

            if (currentLang === '한국') {
                myLang = locationLang.korean;
                setCookie('lang','ko',expiryDate,'/',false,false)
            } else if (currentLang === '中國') {
                myLang = locationLang.chinese;
                setCookie('lang','zh',expiryDate,'/',false,false)

            } else if (currentLang === '日本') {
                myLang = locationLang.japanese;
                setCookie('lang','ja',expiryDate,'/',false,false)

            } else if (currentLang === 'US') {
                myLang = locationLang.english;
                setCookie('lang','en',expiryDate,'/',false,false)
            }

            var hrefParam = '?lang=';
            /*해당 언어의 url로 변경*/
            if (myLang === locationLang.korean) {
                location.href = hrefParam + locationLang.korean;
            } else if (myLang === locationLang.english) {
                location.href = hrefParam + locationLang.english;
            } else if (myLang === locationLang.japanese) {
                location.href = hrefParam + locationLang.japanese;
            } else if (myLang === locationLang.chinese) {
                location.href = hrefParam + locationLang.chinese;
            } else {
                generateModal('footer-error-modal', "에러", '지원하지 않는 언어 설정입니다.', false);
            }
        });
        // locale 관련 설정

        var color = {
            brown: "color:#560000;font-size:11px;",
            red: "color:#d83128;font-size:11px;",
            yellow: "color:#ea991b;font-size:11px;",
            green: "color:#93ba2f;font-size:11px;",
            blue: "color:#4da4e0;font-size:11px;",
            magenta: "color:#ff5274;font-size:11px",
            black: "color:#333333;font-size:11px",
            tangerineTango: 'color:#EF5442',
            violetPurple: 'color:#604C8D; font-size:11px'
        };


        console.log(
            "%c*************************************************************\n" +
            "%c                 __    __________________    ______          \n" +
            "%c                / /   /  _/_  __/_  __/ /   / ____/          \n" +
            "%c               / /    / /  / /   / / / /   / __/             \n" +
            "%c              / /____/ /  / /   / / / /___/ /___             \n" +
            "%c             /_____/___/ /_/   /_/ /_____/_____/             \n" +
            "%c                       ____   _   __._____                   \n" +
            "%c                     / __  / / | / / ____/                   \n" +
            "%c                    / / / / /  |/ / __/                      \n" +
            "%c                   / /_/ / / /|  / /___                      \n" +
            "%c                  ._____. /_/ |_/_____/                      \n\n" +
            "%c*************************************************************\n" +
            "%c-------------현재 인하우스 개발 중입니다.ver Beta------------\n",
            color.green, color.red, color.magenta, color.magenta, color.magenta,
            color.magenta, color.magenta, color.magenta, color.magenta, color.magenta,
            color.magenta, color.green, color.tangerineTango, color.blue, color.tangerineTango,
            color.violetPurple, color.yellow
        );


        var net_protocol = location.protocol;
        var net_host = location.host;
        var net_path = location.pathname;
        var net_search = location.search;
        console.log("현재 URL: " + net_protocol + net_host + net_path + net_search);

        /*******모든 페이지 공통**********/
        /*placeholder 이벤트*/
        placeholderHandler("input");
        /*label click 이벤트( input checkbox 애니메이션)*/
        labelClickEvent(".checkbox_label", "#ff5274");
        /*validation 경고 메시지 지우기*/
        $("input").on("keydown", function () {
            $(this).next(".validation_check").text("");
        });
        $("input").on('keydown', function () {
            $(this).next(".validation_check2").text("");
        });

        /*동적생성 요소를 화면의 가운데에 정렬시키는 함수*/
        jQuery.fn.innerCenter = function () {
            this.css("position", "absolute");
            this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) +
                $(window).scrollTop()) + "px");
            this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) +
                $(window).scrollLeft()) + "px");
            return this;
        };


        /******모든 페이지 공통(끝)*******/


        /*(모바일용)햄버거 버튼 클릭시 0이면 메뉴나오게 하고 1한다.*/
        var navigationSwitch = 0;
        $(".menu_hamburger").on("click", function (e) {
            e.preventDefault();
            $(this).toggleClass("active");
            if (navigationSwitch === 0) {
                $(".container_mobile").stop().animate({
                    left: "0"
                }, 300, "easeInQuad");
                navigationSwitch = 1;
            } else if (navigationSwitch === 1) {
                $(".container_mobile").stop().animate({
                    left: "-100%"
                }, 100, "easeInQuad");
                navigationSwitch = 0;
            }
        });// end 햄버거 버튼 slide event


        /*개인정보 수정 남/녀 성별 선택*/
        $(".radio_woman,label[for='mypage_radio_woman']").on("click", function () {
            var input = $("#mypage_radio_woman");
            var radioCheckStatus = input.prop("checked");
            if (radioCheckStatus) {
                /*radio이기에 하나만 선택되어야 함*/
            } else {
                input.prop("checked", true);
                $(".radio_woman").stop().animate({
                    backgroundColor: "#ff5274"
                }, 300);
                $(".radio_man").stop().animate({
                    backgroundColor: "transparent"
                }, 200);
            }
        });

        $(".radio_man,label[for='mypage_radio_man']").on("click", function () {
            var input = $("#mypage_radio_man");
            var radioCheckStatus = input.prop("checked");
            if (radioCheckStatus) {
            } else {
                input.prop("checked", true);
                $(".radio_man").stop().animate({
                    backgroundColor: "#ff5274"
                }, 300);
                $(".radio_woman").animate({
                    backgroundColor: "transparent"
                });
            }
        });

        $(document).on("click", ".screen_blocking", function () {
            $(".section_modal form")[0].reset();
            $(".section_modal").remove();
            $(".screen_blocking").remove();
        });

        /*페이지 불러들이기 */
        setTimeout(function () {
            $("html").fadeIn(830);
        }, 100);

        /*약관 페이지 펼치는 메서드*/
        $(".terms-section--terms-contents--title").on('click', function (e) {
            $(this).toggleClass('active');
            $(this).siblings('.terms-section--terms-contents--list').toggleClass('active');
        });


    });//JQB
})
()//IIFE


//여기서 사용되는 JQuery의 모든 객체에 대해 공통으로 적용되는 center 함수를 (재)정의하겠다는 의미
jQuery.fn.center = function () {
    this.css("position", "absolute");
    this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) +
        $(window).scrollTop()) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) +
        $(window).scrollLeft()) + "px");
    return this;
};


//아기 정보 입력 버튼 클릭시 form 전송

function submitBabyInfo(cssSelector) {
    $(cssSelector).html(addBabyInfoMore());
}

function changeCss(cssTarget, key, value) {
    var target = document.querySelector(cssTarget);
    return target.style[key] = value;
}

function alignDeviceCenter(cssSelector) {
    var targetHeight = document.querySelector(cssSelector).offsetHeight;
    var targetWidth = document.querySelector(cssSelector).offsetWidth;
    var windowHeight = window.document.body.clientHeight;
    var windowWidth = window.document.body.clientWidth;
    var target = document.querySelector(cssSelector);
    target.style.top = Math.max(((windowHeight - targetHeight) / 2) + window.scrollY) + "px";
    target.style.left = Math.max(((windowWidth - targetWidth) / 2) + window.scrollX) + "px";
}

function addBabyInfoMore() {
    var addInfo = $("<div class='complete_process'></div>");
    return addInfo;
}




function setCookie(name, value, expires, path, theDomain, secure) {

    value = escape(value);

    var theCookie = name + "=" + value +
        ((expires) ? "; expires=" + expires.toGMTString() : "") +
        ((path) ? "; path=" + path : "") +
        ((theDomain) ? "; domain=" + theDomain : "") +
        ((secure) ? "; secure" : "");

    document.cookie = theCookie;
}

function getCookie(Name) {
    var search = Name + "=";
    if (document.cookie.length > 0) { // if there are any cookies
        var offset = document.cookie.indexOf(search);
        if (offset != -1) { // if cookie exists
            offset += search.length;
            // set index of beginning of value
            var end = document.cookie.indexOf(";", offset);
            // set index of end of cookie value
            if (end == -1) end = document.cookie.length;
            return unescape(document.cookie.substring(offset, end))
        }
    }
}

function delCookie(name, path, domain) {
    if (getCookie(name)) document.cookie = name + "=" +
        ((path) ? ";path=" + path : "") +
        ((domain) ? ";domain=" + domain : "") +
        ";expires=Thu, 01-Jan-70 00:00:01 GMT";
}

function parseQueryString(query) {
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

/*input창에 값이 있으면 x버튼 생성/ */
function inputFilled(input) {
    input.on("keyup", function (e) {
        var thisEle = $(this);
        var len = $(this).val().length;
        if (len > 0) {
            thisEle.next('.__remove-input-button').addClass('active');
        } else {
            thisEle.next('.__remove-input-button').removeClass('active');
        }
    }).on("keydown", function (e) {
        var thisEle = $(this);
        var len = $(this).val().length;
        if (len > 0) {
            thisEle.next('.__remove-input-button').addClass('active');
        } else {
            thisEle.next('.__remove-input-button').removeClass('active');
        }
    }).on("focus", function (e) {
        var dataSet = $(this).attr('name');
        $(".__check-animation[data-name=" + dataSet + "]").removeClass('active');
    });
}


function removeInputVal(cssSelector) {
    cssSelector.on('click touch', function (e) {
        var isActive = $(this).hasClass('active');
        var dataSet = $(this).attr('data-name');
        var target = $("input[name=" + dataSet + "]");
        var checkSymnol = $(".__check-animation[data-name=" + dataSet + "]");
        if (isActive) {
            target.val("");
            target.focus();
            checkSymnol.removeClass('active');
            $(this).removeClass('active');
        }
        $(this).css('z-index', '0');
        checkSymnol.css('z-index', '0');
    }).on('mouseenter touchstart', function (e) {
        var dataSet = $(this).attr('data-name');
        var checkSymnol = $(".__check-animation[data-name=" + dataSet + "]");

        var nextisActive = checkSymnol.hasClass('active');

        if (!nextisActive) {
            $(this).css('z-index', '1');
            checkSymnol.css('z-index', '-1');
        }
    }).on("mouseleave touchend", function (e) {

        var dataSet = $(this).attr('data-name');
        var checkSymnol = $(".__check-animation[data-name=" + dataSet + "]");
        var nextisActive = checkSymnol.hasClass('active');

        if (!nextisActive) {
            $(this).css('z-index', '0');
            checkSymnol.css('z-index', '0');
        }

    });
}


function radioSelect(radio1, radio2) {
    radio1.on("focus", function (e) {
        var thisSet = $(this).attr('id');
        $('label[for=' + thisSet + ']').addClass('active');
        $("label[for=gender-input-component--male]").removeClass('active');
        $("#gender-input-component--male").prop('checked', false);
        $("#gender-input-component--female").prop('checked', true);
        console.log($("input[name=gender]:checked").val());

    });

    radio2.on("focus", function (e) {
        var thisSet = $(this).attr('id');
        $('label[for=' + thisSet + ']').addClass('active');
        $("label[for=gender-input-component--female]").removeClass('active');
        $("#gender-input-component--female").prop('checked', false);
        $("#gender-input-component--male").prop('checked', true);
        console.log($("input[name=gender]:checked").val());
    });
}

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


// 파이어베이스 휴대폰인증 성공시 토큰 서버세션에 저장하는 공통 함수
function sendAuthToken(authResult) {
    $.ajax({
        url: "/find_email/send_token",
        type: "POST",
        data: {
            token: authResult.user.G
        },
        dataType: "text",
        beforeSend: function (xhr) {
            sendCsrfToken(xhr);
        },
        success: function (data) {
            if (data === 'success') {
                $("input[name='token']").val(authResult.user.G);
                $("input[name='phone']").val(authResult.user.phoneNumber);
            }
        },
        error: function (e) {
            generateModal("ajax-error-modal", e.status, "인증 토큰 전송 ajax 통신 실패", false);
        }
    })
}

Array.prototype.swap = function (x, y) {
    var a = this[x];
    this[x] = this[y];
    this[y] = a;
    return this;
};

Date.prototype.toYYYYMMDD = function () {
    var mm = this.getMonth() + 1; // getMonth() is zero-based
    var dd = this.getDate();

    return [this.getFullYear(),
        (mm > 9 ? '' : '0') + mm,
        (dd > 9 ? '' : '0') + dd
    ].join('-');
};

/*현재 window의 width를 구한다.*/
function getCurrentWidth() {
    return (width = window.innerWidth && document.documentElement.clientWidth ?
        Math.min(window.innerWidth, document.documentElement.clientWidth) :
        window.innerWidth ||
        document.documentElement.clientWidth ||
        document.getElementsByTagName('body')[0].clientWidth)
}

function defaultScreenBlock(modalName) {
    var tag = '<div class="modal-screen-block" data-modal-name=' + modalName + '></div>';

    $('body').append(tag);

    var modalBtn = $(".modal-screen-block");
    modalBtn.on('click', function (e) {
        var modalName = $(this).attr('data-modal-name');
        $('div[data-modal-name=' + modalName + ']').remove();
        $(this).off('click', null);
    });
}

function Localize(langcookie, jquerySelector) {

    var option = {
        debug: true,
        whitelist: ['en-US', 'ko-KR', 'ja-JP', 'zh-CN'],
        fallbackLng: 'ko-KR',
        lng: 'ko',
        backend: {
            loadPath: 'js/language/{{lng}}.json'
        }
    };

    if (langcookie === 'en') {
        option.fallbackLng = 'en-US';
        option.lng = 'en';
    } else if (langcookie === 'zh') {
        option.fallbackLng = 'zh-CN';
        option.lng = 'zh';
    } else if (langcookie === 'ko') {
        option.fallbackLng = 'ko-KR';
        option.lng = 'ko';
    } else if (langcookie === 'ja') {
        option.fallbackLng = 'ja-JP';
        option.lng = 'ja';
    } else {
        option.fallbackLng = 'ko-KR';
        option.lng = 'ko';
    }

    i18next.use(window.i18nextXHRBackend).init(
        option,
        function (err, t) {
            jqueryI18next.init(i18next, $);
            jquerySelector.localize();
        });
}//i18n 로컬라이징
