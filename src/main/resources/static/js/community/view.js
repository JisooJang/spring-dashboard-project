(function () {
    $(function () {
        /*2018-10-22 커뮤니티 view 수정*/

        var shareFacebookBtn = $(".__share-facebook");
        var shareTwitterBtn = $(".__share-twitter");
        var clipBoardBtn = $(".__copy-clipboard");
        var boardLikeBtn = $(".gallery-view-section--count #community_likes");
        var replyActiveBtn = $(".__active-all-comment");
        var commentBtn = $(".gallery-view-section--count .__comments");
        var replyInput = $("#client-reply-component");
        var replyBtn = $(".__add-reply-btn");
        var boardLikesListBtn = $(".__like-list-interaction");
        var ajaxReady = true;


        /*게시글,수정 삭제 메뉴 클릭*/
        $(".gallery-view-section--count .__more-info").on('click', function (e) {
            $(".gallery-view-section--count--button-box").toggleClass('active');
        });

        /*게시글 수정,삭제 메뉴에서 마우스 나갈 때*/
        $(".gallery-view-section--count--button-box").on('mouseleave', function (e) {
            var isActive = $(this).hasClass('active');
            if (isActive) {
                $(this).removeClass('active');
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

        /*수정창 벗어나면 창 닫기*/
        $(document).on('mouseleave', ".__modify-reply-container.active", function (e) {
            $(".__modify-reply-container.active").removeClass('active');
        });

        /*댓글 수정 키 입력시 버튼 활성화*/
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


        /*갤러리_댓글 수정 ajax통신하기 (POST)*/
        $(document).on('click', '.__gallery-contents-edit-button', function (e) {
            e.preventDefault();
            var boardIdx = $("#board_idx").val();
            var commentIdx = $(this).attr("data-comment-idx");
            var contents = $(this).prev().children().first().val();
            //입력칸 삭제 ajax 통신 성공하면 success:function으로 옮길것
            $.ajax({
                url: "/community/commentModify/" + commentIdx + "/" + boardIdx,
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
                    $(".__client-contents p[data-comment-idx=" + commentIdx + "]").text(contents);

                },
                error: function (e) {
                    generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                }
            });//END ajax
        });//end click


        /*댓글 수정 닫기버튼 클릭시 수정창 삭제*/
        $(document).on('click', '.__close-edit-button', function () {
            var commentIdx = $(this).attr('data-comment-idx');
            $(".__client-reply-list--contents-edit[data-comment-idx=" + commentIdx + "]").html('');
        });

        /*댓글 삭제 통신*/
        $(document).on("click", ".__modify-reply-container.active .__remove-gallery-reply", function (e) {
            var commentIdx = $(this).attr('data-comment-idx');
            var boardIdx = $("#board_idx").val();
            var result = confirm('정말 삭제하시겠습니까?');
            if (result) {

                $.ajax({
                    url: "/community/commentDelete/" + commentIdx + "/" + boardIdx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        var data = JSON.parse(data);

                        if (data.result === 1) {

                            generateModal("로그인이 필요합니다.");
                            $(location).attr("href", url[0] + "/login");

                        } else if (data.result === 2) {

                            generateModal("삭제 권한이 없습니다.");

                        } else if (data.result === 3) {

                            $("#comment_area").html("");
                            var object = {};
                            object.comments = data.comments;
                                console.log(object.comments.length);

                            if (object.comments.length === 0) {
                                $('.client-reply-list').removeClass('active');
                                $(".__active-all-comment").text('');
                            }

                            $(".gallery-view-section--count .__comments").text(object.comments.length);


                            var template = Handlebars.compile($("#community_comment_template").html());
                            var html = template(object);	// data는 댓글 배열
                            $(".client-reply-list").html(html);

                        } else {
                            generateModal('wrong-connect-error', '에러', '통신 에러가 발생했습니다.', false);
                        }

                    }, error: function () {
                        generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                    }

                });
            }
        });


        /*댓글 좋아요 클릭시 1 늘려주는 통신*/
        $(document).on('click', '.__add-likes-btn', function (e) {
            e.preventDefault();
            var commentIdx = $(this).attr('data-comment-idx');
            if (ajaxReady === true) {

                $.ajax({
                    url: '/community/commentLikes/' + commentIdx,
                    type: "GET",
                    dataType: "text",
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        var type = data.split(':')[0].trim();
                        var value = data.split(':')[1].trim();
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
                    },
                    error: function (e) {
                        generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                    }
                });//end of ajax
            }//end if

        });//click



        $(document).on('click', '.__info-close-button',function(){
            $('div[data-modal-name="client-like-modal"]').remove();
        });


        /*게시글의 좋아요를 누른 사람 리스트 보기*/
        boardLikesListBtn.on("click", function (e) {

            $(".client-like-info-section").remove();

            var communityIdx = $(this).attr("data-idx");
            $.ajax({
                url: "/gallery/" + communityIdx + "/likes_view",
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


                },
                error: function () {
                    generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                }
            });


        });


        /*VIEW페이지 내에서 COMMENT 클릭할 경우 댓글란으로 이동*/
        commentBtn.on('click', function (e) {
            var isActive = $(".client-reply-list").hasClass('active') || false;

            if (!isActive) {
                $(".client-reply-list").addClass('active');
                $(".__active-all-comment").text("댓글 숨기기");
            }

            var scroll_top = $(".client-reply-section").offset().top;
            var scrollHeight = $(".client-reply-section").height();
            $("body,html").animate({
                scrollTop: scroll_top - scrollHeight
            }, 500, "easeOutSine", function () {
                $("#client-reply-component").focus();
            });
        });

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


        /*페이스북 게시물 공유하기 클릭*/
        shareFacebookBtn.on('click', function (e) {
            var url = window.location.href;
            var title = $("#board_subject").text();
            var description = $("#qna_contents").val();
            var parsing_contents = $("#parsing_contents").val();
            var share_object = {
                'og:url': url,
                'og:title': title,
                'og:description': parsing_contents,
            };

            if (description.indexOf("<img src=") >= 0) {

                var img_src = $("#board_first_file").val();
                share_object['og:image'] = img_src;
            }
            // 이미지가 첨부되었으면 share_object에 og:image 속성 추가

            console.log(share_object);

            FB.ui({
                method: 'share_open_graph',
                action_type: 'og.likes',
                action_properties: JSON.stringify({
                    object: share_object
                })
            }, function (response) {
            });


        });
        /*페이스북 게시물 공유하기 클릭*/


        /*트위터 공유하기 클릭*/
        shareTwitterBtn.on('click', function (e) {
            var popUpTwitter = function () {
                window.open("https://twitter.com/intent/tweet"
                    + "&text=" + encodeURIComponent($(".gallery-view-section--desc h1").text()) // Title in this html document
                    + "&url=" + encodeURIComponent(window.location.href), "_blank"
                );
            };
            popUpTwitter();
        });
        /*트위터 공유하기 클릭*/

        /*url클릭시 링크 복사*/
        clipBoardBtn.on('click', function (e) {
            var target = document.getElementById('clip-board');
            target.innerHTML = window.location.href;
            target.select();
            document.execCommand('copy');
            generateModal('clip-board-modal', "COPY!", '클립보드에 복사되었습니다.', false);
        });
        /*url클릭시 링크 복사*/


        /*게시물 좋아요 클릭 이벤트*/
        boardLikeBtn.on("click", function (e) {
            e.preventDefault();
            var category = $(this).attr('data-category');
            var boardIdx = $(this).attr('data-board-idx');
            $.ajax({
                url: "/community/" + boardIdx + "/likes",
                type: "GET",
                dataType: "text",
                async: false,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    var countLikes = parseInt($('#community_likes').text());


                    if (data != null) {
                        var check = data.split(':');

                        if (check[0] === 'plus') {
                            /*좋아요 count 1 증가*/
                            $('#community_likes').text(++countLikes);
                            $(".__like-list-interaction").text('좋아요 '+ (countLikes)+'개');
                            // 하트색 채우기
                            $('#community_likes').attr('class', '__my-likes');
                        } else if (check[0] === 'minus') {
                            /*좋아요 count 1 감소*/
                            $('#community_likes').text(--countLikes);
                            $(".__like-list-interaction").text('좋아요 '+ (countLikes)+'개');

                            // 하트색 지우기
                            $('#community_likes').attr('class', '__likes');

                        } else {
                            generateModal('data-connect-error', "통신에러", '데이터가 없습니다.', false);
                        }
                    } else {
                        generateModal('data-connect-error', "통신에러", '데이터가 없습니다.', false);

                    }
                },
                error: function (e) {
                    generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                }
            });
        });

        /*댓글 보이기 숨기기 버튼 클릭 이벤트*/
        replyActiveBtn.on('click', function (e) {

            var clientList = $(".client-reply-list");

            if (clientList.hasClass('active')) {
                $(this).text('댓글 모두 보기');
            } else {
                $(this).text('댓글 숨기기');
            }
            clientList.toggleClass('active');
        });


        /* 게시글 삭제 */

        var deleteBtn = $(".__delete-board-info-button");
        deleteBtn.on("click", function (e) {
            e.preventDefault();
            var category = $(this).attr('data-board-category');
            var boardIdx = $(this).attr('data-board-idx');
            var result = confirm("정말 삭제하시겠습니까?");
            if (result) {
                $.ajax({
                    url: "/community/" + category + "/delete/" + boardIdx,
                    type: "GET",
                    dataType: "text",
                    async: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === 'success') {
                            generateModal('complete-delete-modal', "성공", '게시물을 삭제하였습니다.', false);
                            var url = window.location.href.split("/community/" + category + "/");
                            $(location).attr("href", url[0] + "/community/" + category + "/page/1");
                        } else if (data === '0') {
                            generateModal('login-warning-modal', "실패", '로그인을 하여 주시기 바랍니다.', false);
                            return false;
                        } else if (data === '1') {
                            generateModal('error-warning-modal', "에러", '잘못된 접근입니다.', false);
                            return false;
                        } else if (data === '2') {
                            generateModal('modify-warning-modal', "권한", '수정할 권한이 없습니다.', false);
                            return false;
                        }
                    },
                    error: function (e) {
                        generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                    }
                });//ajax
            }
        });

        /*커뮤니티 댓글 작성 버튼 클릭 이벤트*/
        replyBtn.on('click', function (e) {
            var isActive = $(this).hasClass('active') || false;
            /*댓글의 value를 가져온다.*/
            var contents = $("#client-reply-component").val();
            /*댓글의 idx번호*/
            var boardIdx = $("#board_idx").val();
            var sessionIdx = $(this).attr('data-session-idx');
            var writerIdx = $(this).attr('data-writer-idx');

            if (contents.length < 1 || isActive === false) {
                generateModal('reply-warning-modal', "Oops!", '댓글을 입력해주세요.', false);
                return;
            }

            if (ajaxReady === true && contents.length > 0 && isActive === true) {

                $.ajax({
                    url: "/community/comment/" + boardIdx,
                    type: "POST",
                    async: false,
                    data: {
                        reply_contents: contents
                    },
                    beforeSend: function (xhr) {
                        ajaxReady = false;
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        var object = {};
                        object.comments = data.comments;

                        if (object.comments.length > 0) {
							$('.client-reply-list').addClass('active');
							$(".__active-all-comment").text('댓글 숨기기');
							$(".gallery-view-section--count .__comments").text(object.comments.length);
                        }
                        var template = Handlebars.compile($("#community_comment_template").html());
                        var html = template(object);	// data는 댓글 배열
                        $(".client-reply-list").html(html);
                        /*댓글입력칸 초기화*/
                        $("input[name='reply']").val('');

                        /*웹 소켓에 알림 추가*/
                        if (sessionIdx !== writerIdx) {
                            group_request_added(sessionIdx, writerIdx);
                        }


                    },
                    error: function (e) {
                        generateModal('ajax-connect-error', e.status, 'AJAX통신 에러가 발생했습니다.', false);
                    },
                    complete: function () {
                        ajaxReady = true;
                    }
                });//end ajax

            } // end if
        });


    });//JQB
})();//iife




