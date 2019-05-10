(function () {
    $(function () {


        /*검색클릭*/
        $(".search-component-bar").on('click', function () {
            $(this).addClass('active');
        })
        $(".search-component").on('mouseleave', function () {
            $('.search-component-bar').removeClass('active');
        })


        var packeryBox = $('#gallery-container');
        var slideBtn = $('.__slide-top');


        $(document).on('click', ".__info-close-button", function (e) {
            $(".modal-form").remove();
            $(".screen_block").remove();
        });

        $(document).on('click', '.__who-likes-button', function () {
            $(".client-like-info-section").remove();

            var gallery_idx = $(this).attr("data-gallery-idx");
            $.ajax({
                url: "/gallery/" + gallery_idx + "/likes_view",
                type: "GET",
                success: function (data) {
                    var array = {};
                    array.likes = $("#gallery_likes").text();
                    array.likes_list = data;
                    var template = Handlebars.compile($("#like-client-list--template").html());
                    var html = template(array);
                    defaultScreenBlock('client-like-modal');
                    $("body").append(html);
                    $(".client-like-info-section").innerCenter();
                    $(".__info-close-button").on('click', function (e) {
                        $('div[data-modal-name="client-like-modal"]').remove();
                    });
                },
                error: function () {
                    alert("게시글 좋아요 리스트 통신 오류");
                }
            });
        });


        /*좋아요*/

        /*좋아요 클릭시*/
        $(document).on('click', '.gallery-board--info--like', function (e) {
            var diary_idx = $(this).attr("data-idx");
            $.ajax({
                url: "/gallery/" + diary_idx + "/likes",
                type: "GET",
                dataType: "text",
                success: function (data) {
                    var type = data.split(':')[0].trim();
                    var value = data.split(':')[1].trim();
                    console.log(type, value);
                    if (type === 'plus') {
                        // css 변경
                        $(".gallery-board--info--like[data-idx=" + diary_idx + "]").addClass('active');
                    } else {
                        // css 변경
                        $(".gallery-board--info--like[data-idx=" + diary_idx + "]").removeClass('active');
                    }
                    $(".gallery-board--info--like[data-idx='" + diary_idx + "']").text(value);	// 바뀐 좋아요값으로 변경
                    $(".__who-likes-button[data-gallery-idx='" + diary_idx + "']").text("좋아요 " + value + "개");	// 바뀐 좋아요값으로 변경

                    $(".gallery-board[data-board-idx='" + diary_idx + "']").find('.__who-likes-button').text('좋아요 ' + value + '개');
                },
                error: function () {
                    alert("갤러리 좋아요 ajax 통신 오류");
                }
            });
        });


        /*최상단으로 슬라이드*/
        slideBtn.on('click', function (e) {
            e.preventDefault();
            $('html,body').stop().animate({
                scrollTop: 0
            }, 1000, "easeOutQuart");


        });//click
        var searchBtn = $(".__search>div");
        var animateSearchBar = $(".gallery-search-form");


        searchBtn.on('click', function () {

            animateSearchBar.toggleClass('active')

        });

        /*레이지 로딩*/
        $("img.lazyload").lazyload({
            effect: "fadeIn",
            effectTime: 1000,
        });

        /*히스토리에 현재 페이지 스크롤 위치 저장시키기*/
        if ('scrollRestoration' in history) {
            // Back off, browser, I got this...
            history.scrollRestoration = 'manual';
        }

        /*팩커리 정의*/
        packeryBox.packery({
            itemSelector: '.gallery-board',
            gutter: 21
        });

        /*1초후 팩커리 처음 실행*/
        setTimeout(function () {
            packeryBox.packery();
        }, 1000);

        /*2초후 팩커리 두번째 실행*/
        setTimeout(function () {
            packeryBox.packery();
        }, 2000);

        /*3초후 지연로딩 대비 재실행*/
        setTimeout(function () {
            packeryBox.packery();
        }, 3000);

        // 전체 갤러리 갯수 통신 ajax
        var gallery_count = 0;

        /*스크롤로 지정한 바닥 높이에 닿을 때마다 ajax로 갤러리 글 불러오기*/
        $.ajax({
            url: "/gallery/total_count",
            type: "GET",
            async: false,
            dataType: "text",
            success: function (data) {
                gallery_count = parseInt(data);
            }, error: function () {
                generateModal("갤러리 갯수 ajax 에러");
            }
        });

        /*최대 페이지 계산 0으로 초기화*/
        var max_page_num = 0;

        /*갤러리 페이징 계산*/
        if (gallery_count % 20 > 0) {
            max_page_num = gallery_count / 20 + 1;
        } else {
            max_page_num = gallery_count / 20;
        }

        /*스크롤이 바닥에 닿을 떄 마다 새로운 이미지 40개씩 불러오기*/
        var paging = 1;
        $(window).scroll(function (e) {
            /*paging변수는 이미지(40개단위)들을 한 번 불러올때마다 1씩 증가합니다.*/

            /*팩커리로 위치 계속 조절*/
            setTimeout(function () {
                packeryBox.packery();
            }, 500);

            var htmlHeight = $(document).height();
            var windowHeight = $(window).height();

            var documentHeigt = htmlHeight - windowHeight;

            var point = Math.floor($('.footer-section').offset().top);

            //이전 스크롤의 좌표
            var lastScrollTop = 0;

            console.log(documentHeigt, point);

            //현재 스크롤의 좌표
            var currentScrollTop = $(window).scrollTop();

            if (currentScrollTop >= (documentHeigt - (htmlHeight - point) - 200) && paging <= max_page_num) {
                $.ajax({
                    url: "/gallery/paging/" + ++paging,
                    type: "GET",
                    dataType: "json",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        console.log(data);
                        console.log('현재 통신한 페이지는 ' + paging + ' 입니다.');
                        lastScrollTop = currentScrollTop;
                        if (data != null && data.length > 0) {

                            data.map(function (key, i) {
                                var item = $(renderGalleryList(key));
                                packeryBox.append(item).packery('appended', item);
                            });


                        } else {
                            alert("null");
                        }

                    }, //end success
                    error: function (data) {
                        // 더이상 페이징을 불러올 데이터가 없을때 error function으로 넘어옴
                        console.log(data.responseText);
                    },
                    complete: function () {
                        console.log('갤러리 AJAX통신 완료');
                    }
                });
            }
        });

        // 좋아요순 정렬
        $("#likes_list").on("click", function (e) {
            e.preventDefault();

            var htmlHeight = $(document).height();
            var windowHeight = $(window).height();

            var documentHeigt = htmlHeight - windowHeight;

            var point = Math.floor($('.footer').offset().top);

            //이전 스크롤의 좌표
            var lastScrollTop = 0;

            console.log(documentHeigt, point);

            //현재 스크롤의 좌표
            var currentScrollTop = $(window).scrollTop();

            $.ajax({
                url: "/gallery/paging_likes/" + paging++,
                type: "GET",
                dataType: "json",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    console.log('현재 통신한 페이지는 ' + paging + ' 입니다.');

                    lastScrollTop = currentScrollTop;

                    data.forEach(function (element) {
                        var items = $('<li class="post" data-board-idx=' + element.board_idx + '>' +
                        '                <a href="/gallery/view/' + element.board_idx + '"' + '>' +
                        '                    <div class="inner">' +
                        '                        <div class="grid_image">' +
                        '                            <img src=' + element.file_url + ' alt="' + element.nickname + '이 업로드한 사진"/>' +
                        '                        </div>' +
                        '                        <div class="grid_caption">' +
                        '                            <time class="grid_date">' + element.date_created + '</time>' +
                        '                            <h2 class="grid_board_title">' + element.contents + '</h2>' +
                        '                                ' + element.comment_count > 0 && element.comment_count != null || undefined ? '<span class="reply_count">+(' + element.comment_count + ')+</span>' : null +
                            '                            <div class="grid_writer">' +
                            '                                <span>by  </span>' + (element.nickname === null ? '<span style="opacity: .5">' : '<span>') + (element.nickname === null ? '탈퇴한 회원' : element.nickname) + '</span>' +
                            '                                <div class="box_tag">' + HashtagLink(element['hashtag2']) + '</div>' +
                            '                            </div>' +
                            '                        </div>' +
                            '                    </div>' +
                            '                </a>' +
                            '            </li>');


                        /*팩커리 재정렬*/
                        setTimeout(function () {
                            packeryBox.append(items).packery('appended', items);
                        }, 300)
                    })//for each
                }, //end success
                error: function () {
                    generateModal("갤러리 페이징 ajax 에러");
                },
                complete: function () {
                    console.log('갤러리 AJAX통신 완료');
                }
            });
        });


        /*그리드 불러오기*/

    });//JQB


    // 좋아요 클릭시 리스트
    /*$(".gallery-board--info--like").click(function () {
        var gallery_idx = $(this).attr("data-idx");
        $.ajax({
            url: "/gallery/" + gallery_idx + "/likes_view",
            type: "GET",
            success: function (data) {
                var array = {};
                array.likes = $("#gallery_likes").text();
                array.likes_list = data;
                var template = Handlebars.compile($("#like-client-list--template").html());
                var html = template(array);
                $(".gallery-view-section").append(html);
                $(".client-like-info-section").innerCenter();
            },
            error: function () {
                alert("게시글 좋아요 리스트 통신 오류");
            }
        });
    });*/

    /*해쉬태그 함수*/
    function HashtagLink(element) {
        if (typeof element === null || typeof element === undefined || element === null || element === undefined) {
            return '';
        } else {
            // 태그를 담을 변수 생성
            var hashValue = "";
            for (var i = 0; i < element.length; ++i) {
                //머저 배열의 i번째를 obj변수에 할당함
                var obj = element[i];

                /*변수에 계속 누적시킨다.*/
                hashValue += '<a href="/gallery/searchByTag/' + obj + '/1">'
                hashValue += '<span class="tagging">' + obj + '</span>';
                hashValue += '</a>'
            }//end for
        }

        return hashValue;
    }


})();//iife

function renderGalleryList(object) {


    console.log(object);

    var hashTag = object.hashtag2;
    var hashArr = [];
    hashTag.map(function (key, i) {
        var tag = '<a href="/gallery/searchByTag/' + (key) + '/1">#' + (key) + '</a>';
        hashArr.push(tag);
    });
    hashArr = hashArr.join('');

    var tag = '<li class="gallery-board" data-board-idx="' + (object.diary_idx) + '">\n' +
        '                    <div class="gallery-board--writer">\n' +
        '                        <a href="/community/view_member_info/' + (object.member_idx) + '/board/1">\n' +
        '                            <img src="' + (object.thumbnail) + '">\n' +
        '                        <span>' + (object.nickname) + '</span>\n' +
        '                        </a>\n' +
        '                        <time>' + (object.date_created) + '</time>\n' +
        '                    </div>\n' +
        '                    <div class="gallery-board--image">\n' +
        '                        <a href="/gallery/view/' + (object.diary_idx) + '">\n' +
        '                            <img src="' + (object.file_url) + '" class="lazyload" width="auto" height="auto">\n' +
        '                        </a>\n' +
        '                    </div>\n' +
        '                    <div class="pm-container">\n' +
        '                        <div class="gallery-board--desc">\n' +
        '                            <a href="javascript:void(0)" style="font-size: 10px;  display: block; font-weight: 500; white-space: nowrap;  width: 60px;  margin-bottom: 13px; opacity: .85;" class="__who-likes-button" data-gallery-idx="' + (object.diary_idx) + '">좋아요 ' + (object.likes) + '개</a>\n' +
        '                            <a href="/gallery/view/' + (object.diary_idx) + '">\n' +
        '                                <h2>' + (object.subject) + '</h2>\n' +
        '                                <div class="gallery-board--desc--paragraph">\n' +
        '                                    <p>' + (object.contents) + '</p>\n' +
        '                                    <span class="__more-info">더보기</span>\n' +
        '                                </div>\n' +
        '                            </a>\n' +
        '                        </div>\n' +
        '                        <div class="gallery-board--hash-tag">\n' + hashArr +
        '                        </div>\n' +
        '                        <div class="gallery-board--info">\n' +
        '                            <span class="gallery-board--info--like ' + (object.likes_check === 1 ? 'active' : null) + '" data-idx="' + (object.diary_idx) + '">' + (object.likes) + '</span>\n' +
        '                            <a href="/gallery/view/' + (object.diary_idx) + '?focus=on">\n' +
        '                                <span class="gallery-board--info--comment" id="target" data-idx="' + (object.diary_idx) + '">' + (object.comment_count) + '</span>\n' +
        '                            </a>\n' +
        '                            <span class="gallery-board--info--view">' + (object.hits) + '</span>\n' +
        '                        </div>\n' +
        '                    </div>\n' +
        '                </li>'

    return tag;
}