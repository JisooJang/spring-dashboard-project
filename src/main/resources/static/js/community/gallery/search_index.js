(function () {
    $(function () {
        /*애니메이션 효과를 위해 css 위치 초기화*/
        $(".post:nth-child(odd)").css('top', 0);
        $(".post:nth-child(even)").css('top', 0);
        $(".grid_board").packery({
            itemSelector: '.post'
        });
        setTimeout(function () {
            $(".grid_board").packery();
        }, 1000);
        
        // 전체 갤러리 갯수 통신 ajax
        var gallery_count = $("#list_size").val();
        
        var max_page_num = 0;
        if(gallery_count % 20 > 0) {
        	max_page_num = gallery_count / 20 + 1;
        } else {
        	max_page_num = gallery_count / 20;
        }
        /*스크롤이 바닥에 닿을 떄 마다 새로운 이미지 40개씩 불러오기*/
        var paging = 1;

        $(window).scroll(function (e) {
            /*paging변수는 이미지(40개단위)들을 한 번 불러올때마다 1씩 증가합니다.*/


            var htmlHeight = $(document).height();
            var windowHeight = $(window).height();

            var documentHeigt = htmlHeight - windowHeight;

            var point = Math.floor($('.footer').offset().top);

            //이전 스크롤의 좌표
            var lastScrollTop = 0;

            console.log(documentHeigt, point);

            //현재 스크롤의 좌표
            var currentScrollTop = $(window).scrollTop();

            //다운 스크롤(아랫방향)
            console.log('현재 좌표 '+currentScrollTop);
            console.log('바닥 좌표 '+ (documentHeigt - (htmlHeight - point)));
            if (currentScrollTop >= (documentHeigt - (htmlHeight - point)-150) && paging <= max_page_num) {
                $.ajax({
                    url: "/gallery/paging/" + paging++,
                    type: "GET",
                    dataType: "json",
                    beforeSend: function(xhr) {
            			sendCsrfToken(xhr);
            		},
                    success: function (data) {
                        console.log('현재 통신한 페이지는 '+ paging +' 입니다.');

                        lastScrollTop = currentScrollTop;

                            data.forEach(function (element) {
                                var items = $('<li class="post" data-board-idx=' + element.board_idx + '>' +
                                    '                <a href="/gallery/view/' + element.board_idx + '>' +
                                    '                    <div class="inner">' +
                                    '                        <div class="grid_image">' +
                                    '                            <img src=' + element.file_url + ' alt="' + element.nickname + '이 업로드한 사진">' +
                                    '                        </div>' +
                                    '                        <div class="grid_caption">' +
                                    '                            <time class="grid_date">' + element.date_created + '</time>' +
                                    '                            <h2 class="grid_board_title">' + element.contents + '</h2>' +
                                    '                                ' + '<span class="reply_count">(' + element.comment_count + ')</span>' +
                                    '                            <div class="grid_writer">' +
                                    '                                <span>by</span><span>' + element.nickname + '</span>' +
                                    '                                <div class="box_tag">' + HashtagLink(element['hashtag2']) + '</div>' +
                                    '                            </div>' +
                                    '                        </div>' +
                                    '                    </div>' +
                                    '                </a>' +
                                    '            </li>');
                                
                                /*팩커리 재정렬*/
                                setTimeout(function(){
                                    $(".grid_board").append(items).packery('appended', items);
                                },300)
                            })//for each
                    }, //end success
                    error: function () {
                        alert("갤러리 페이징 ajax 에러");
                    },
                    complete: function () {
                    }


                })

            }

            //업 스크롤(윗방향으로)


        });


        /*그리드 불러오기*/

        /*네비게이션 위로 클릭시 맨위로 스크롤*/
        $(document).on("click", ".gallery-button__anchor", function () {
            var CanvasTop = $("body").offset().top;
            if (!$("body,html").is(":animated")) {
                $("body,html").animate({
                    scrollTop: CanvasTop
                }, 400);
            } else {
                return false;
            }
        });
        /*갤러리 태그 검색*/
        $(".gallery-button__search button[type=submit]").on("click", function (e) {
            e.preventDefault();

            var keywordCheck = document.getElementById('search_gallery').value;
            if (isEmpty(keywordCheck)) {
                $(this).parents('.gallery-button__search').toggleClass('active');
                $(this).toggleClass('active');
                $(this).siblings('label').toggleClass('active');
                $(this).siblings('input').toggleClass('active');
            } else {
                $("#search_tag").submit();
                document.getElementById('search_gallery').value = '';
            }

        });// 갤러리 태그 검색


    });//JQB

    function HashtagLink(element) {
        if (typeof element === null || typeof element === undefined) {
            return;
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