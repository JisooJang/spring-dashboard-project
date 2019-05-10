(function () {
    $(function () {

        /*알람창을 통해서 댓글 확인을 위해 들어왔을 경우 댓글 애니메이션 이벤트*/
        var AlarmReplyIdx = $("#alert_comment_idx").attr('value') || null;
        var ajaxReady = true;
        var writer_idx = $("#writer_idx").val();
        var session_idx = $("#session_idx").val();
        var editBtn = $(".__modify-reply-btn");
        /*댓글 버튼*/
        var replyBtn = $(".__add-reply-btn");

        /*댓글 input box*/
        var replyInput = $("#client-reply-component");


        // JisooJang [6:35 PM]
        // 갤러리 댓글 좋아요 ajax
        // /gallery/commentLikes/{comment_idx}
        // 응답값 형식 : "plus:3" / "minus:6"
        // 앞에 plus가 붙으면 좋아요추가된것(+1), minus 가 붙으면 좋아요가취소된것(-1)
        // 응답값에 ':' 기준으로 split해서 왼쪽이 좋아요된여부 css 체크로 사용하면 되고, 왼쪽 숫자가 해당 댓글 총 좋아요 숫자
        //
        // ==========================================
        // 기존 board 댓글 좋아요 구조랑 같음

        /*2018-10-17 리뉴얼*/
        /*페이스북 게시물 공유하기 클릭*/
        $(".__share-facebook").on('click', function (e) {
            var popUpFacebook = function () {
                window.open("https://www.facebook.com/sharer/sharer.php" + "?u=" + encodeURIComponent(window.location.href), '_blank');
            };
            popUpFacebook();
        });

        /*트위터 공유하기 클릭*/
        $(".__share-twitter").on('click', function (e) {
            var popUpTwitter = function () {
                window.open("https://twitter.com/intent/tweet"
                    + "&text=" + encodeURIComponent($(".gallery-view-section--desc h1").text()) // Title in this html document
                    + "&url=" + encodeURIComponent(window.location.href), "_blank"
                );
            };
            popUpTwitter();
        });


        /*게시글의 좋아요 누른 사람들 리스트 보기*/
        $(".__like-list-interaction").on('click', function (e) {
            $(".client-like-info-section").remove();

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
                    defaultScreenBlock('client-like-modal');
                    $("body").append(html);
                    $(".client-like-info-section").innerCenter();

                    $(".__info-close-button").on('click', function (e) {
                        $('div[data-modal-name="client-like-modal"]').remove();
                    });
                },
                error: function (e) {
                    alert("게시글 좋아요 리스트 통신 오류");
                }
            });

        });


        /*댓글 좋아요 클릭시 1 늘려주는 통신*/
        $(document).on('click', '.__add-likes-btn', function (e) {
            e.preventDefault();
            var commentIdx = $(this).attr('data-comment-idx');
            if (ajaxReady === true) {
                $.ajax({
                    url: '/gallery/commentLikes/' + commentIdx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        console.log(data);
                        var type = data.split(':')[0].trim();
                        var value = data.split(':')[1].trim();
                        console.log(type, value);
                        if (type === 'plus') {
                            $('.svg-fill[data-comment-idx=' + commentIdx + ']').attr('fill', '#ff5274');
                            $(".__count[data-comment-idx=" + commentIdx + "]").text(value);
                        } else {
                            $('.svg-fill[data-comment-idx=' + commentIdx + ']').attr('fill', '#181818');
                            $(".__count[data-comment-idx=" + commentIdx + "]").text(value);
                        }
                    },
                    complete: function () {
                        ajaxReady = true;
                    }
                })
            }

        });


        /*url클릭시 링크 복사*/
        $(".__copy-clipboard").on('click', function (e) {
            var target = document.getElementById('clip-board');
            target.innerHTML = window.location.href;
            target.select();
            document.execCommand('copy');
        });


        if (!isEmpty(AlarmReplyIdx)) {
            alarmReplyScrollEvent();
        }


        replyInput.on('keydown', function () {
            var len = $(this).val().length;

            if (len > 0) {
                $(".__add-reply-btn").addClass('active');
            } else {
                $(".__add-reply-btn").removeClass('active');

            }
        }).on('keyup', function () {
            var len = $(this).val().length;
            if (len > 0) {
                $(".__add-reply-btn").addClass('active');
            } else {
                $(".__add-reply-btn").removeClass('active');

            }
        });

        $(document).on('keydown', "#gallery-reply-edit", function () {
            var commentIdx = $(this).attr('data-comment-idx');
            var target = $(".__gallery-contents-edit-button[data-comment-idx=" + commentIdx + "]");
            var len = $(this).val().length;
            if (len > 0) {
                target.addClass('active');
            } else {
                target.removeClass('active');
            }
        }).on('keyup', "#gallery-reply-edit", function () {
            var commentIdx = $(this).attr('data-comment-idx');
            var target = $(".__gallery-contents-edit-button[data-comment-idx=" + commentIdx + "]");
            var len = $(this).val().length;
            if (len > 0) {
                target.addClass('active');
            } else {
                target.removeClass('active');
            }
        });


        /*수정창 벗어나면 창 닫기*/
        $(document).on('mouseleave', ".__modify-reply-container.active", function (e) {
            $(".__modify-reply-container.active").removeClass('active');
        });

        replyBtn.on('click', function (e) {
            var isActive = $(this).hasClass('active') || false;
            /*댓글의 value를 가져온다.*/
            var contents = $("input[name='reply']").val();
            /*댓글의 idx번호*/
            var board_idx = $("#board_idx").val();

            if (contents.length < 1 || isActive === false) {
                generateModal('error-modal','실패','댓글을 입력해주세요.', false);
                return;
            }

            if (ajaxReady === true && contents.length > 0 && isActive === true) {
                $.ajax({
                    url: "/gallery/" + board_idx + "/comment_write",
                    type: "POST",
                    data: {
                        contents: contents
                    },
                    dataType: "text",
                    beforeSend: function (xhr) {
                        ajaxReady = false;
                        sendCsrfToken(xhr);

                    },
                    success: function (data) {
                        /*댓글 갱신*/
                        get_comment_list();

                        /*웹 소켓에 알림 추가*/
                        if (session_idx !== writer_idx) {
                            group_request_added(session_idx, writer_idx);
                        }
                    },
                    error: function (e) {
                        generateModal('error-modal',e.status,'서버 통신 오류 입니다.', false);
                    },
                    complete: function () {
                        ajaxReady = true;
                    }

                });
            }
        });


        /*수정,삭제 버튼 클릭시*/
        $(document).on('click', '.__modify-reply-btn', function (e) {
            e.preventDefault();
            var commentIdx = $(this).attr('data-comment-idx');
            var childNode = $(".__modify-reply-container[data-comment-idx=" + commentIdx + "]");
            $(".__modify-reply-container.active").removeClass('active');
            childNode.toggleClass('active');
        });


        /*댓글 수정 input태그 띄우기*/
        $(document).on('click', '.__modify-reply-container.active .__modify-gallery-reply', function (e) {
            var commentIdx = $(this).attr('data-comment-idx');
            var originText = $(".__client-contents p[data-comment-idx=" + commentIdx + "]").text();

            var tag = '    <form>\n' +
                '                    <label for="gallery-reply-edit">\n' +
                '                        <input type="text" name="edit" id="gallery-reply-edit" class="gallery-reply-edit" data-comment-idx=' + commentIdx + '>' +
                '                    </label>\n' +
                '                    <button type="submit" class="__gallery-contents-edit-button" data-comment-idx=' + commentIdx + '>수정</button>\n' +
                '                </form><div class="__close-edit-button" data-comment-idx=' + commentIdx + '>닫기</div>';

            $(".__client-reply-list--contents-edit[data-comment-idx=" + commentIdx + "]").html(tag);
            $('.gallery-reply-edit[data-comment-idx=' + commentIdx + ']').val(originText).focus().select();
        });

        /*댓글 수정 닫기버튼 클릭시 수정창 삭제*/
        $(document).on('click', '.__close-edit-button', function () {
            var commentIdx = $(this).attr('data-comment-idx');
            $(".__client-reply-list--contents-edit[data-comment-idx=" + commentIdx + "]").html('');
        });


        /*갤러리_댓글 수정 ajax통신하기 (POST)*/
        $(document).on('click', '.__gallery-contents-edit-button', function (e) {
            e.preventDefault();
            var board_idx = $("#board_idx").val();
            var commentIdx = $(this).attr("data-comment-idx");
            var contents = $(this).prev().children().first().val();
            //입력칸 삭제 ajax 통신 성공하면 success:function으로 옮길것
            $.ajax({
                url: "/gallery/commentModify/" + commentIdx + "/" + board_idx,
                type: "POST",
                data: {
                    contents: contents
                },
                dataType: "text",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    $(".__client-reply-list--contents-edit[data-comment-idx=" + commentIdx + "]").html("");
                    get_comment_list();
                },
                error: function () {
                    alert("ajax 통신 에러");
                }
            });//END ajax
        });//end click


        // 댓글 삭제
        $(document).on("click", ".__modify-reply-container.active .__remove-gallery-reply", function (e) {
            var commentIdx = $(this).attr('data-comment-idx');
            var result = confirm('정말 삭제하시겠습니까?');
            if (result) {
                $.ajax({
                    url: "/gallery/deleteComment/" + commentIdx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === '2') {
                            $("#comment_area").html("");
                            get_comment_list();
                        } else if (data === '1') {
                            alert("존재하지 않는 댓글입니다.");
                        }

                    }, error: function () {
                        alert("ajax 통신 에러");
                    }

                });
            }
        });

        // 댓글리스트 ajax
        function get_comment_list() {
            var board_idx = $("#board_idx").val();
            $.ajax({
                url: "/gallery/" + board_idx + "/comment_list",
                type: "GET",
                dataType: "json",
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    var object = {};
                    object.comments = data;

                    if (object.comments.length > 0) {
                        $(".__active-all-comment").text('댓글 숨기기');
                        $(".client-reply-list").addClass('active');
                    } else if (object.comments.length === 0) {
                        $(".__active-all-comment").text('');
                        $(".client-reply-list").removeClass('active');
                    }

                    $('.gallery-view-section--count .__comments').text(object.comments.length);

                    var template = Handlebars.compile($("#gallery_comment_template").html());
                    var html = template(object);	// data는 댓글 배열
                    $(".client-reply-list").html(html);

                    /*댓글입력칸 초기화*/
                    $("input[name='reply']").val('');

                }, error: function () {
                    alert("댓글 데이터 불러오기에 실패하였습니다.");
                }
            });
        }

        function alarmReplyScrollEvent() {

            var newReplyIdx = $("#alert_comment_idx").val() || null;
            var addedReply = $('div[data-reply-idx=' + newReplyIdx + ']');
            addedReply.css({
                opacity: 0,
                right: '-50%'
            });


            var replyScrollTarget = addedReply.offset().top || null;

            if (newReplyIdx === null) {
                console.log('값이 없다.');
                return false

            } else {

                $("body,html").animate({

                    scrollTop: replyScrollTarget / 1.3

                }, 1500, 'easeOutQuint', function () {

                    addedReply.animate({
                        opacity: 1,
                        right: 0
                    }, 1000, 'easeOutCirc');

                });
            }

        }//end function


    });//JQB
})();//iife