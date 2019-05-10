(function () {
    $(function () {

        /*코멘트 눌러서 왔을 경우*/
        var query = window.location.search.substring(1);
        var shareFacebookBtn = $(".__share-facebook");

        var isFocus = parseQueryString(query);

        console.log(isFocus);

        if (isFocus.focus === 'on') {
            var scroll_top = $(".client-reply-section").offset().top;
            var elementHeight = $(".gallery-view-section--desc").innerHeight();
            var elementHeight2 = $(".gallery-view-section--count").innerHeight();
            $("body,html").animate({
                scrollTop: scroll_top - elementHeight - elementHeight2 - 300
            }, function () {
                $("#client-reply-component").focus();
            });
        }

        /*코멘트 눌러서 왔을 경우*/


        /*VIEW페이지 내에서 COMMENT 클릭할 경우 댓글란으로 이동*/
        $(".gallery-view-section--count .__comments").on('click', function (e) {

            var isActive =  $(".client-reply-list").hasClass('active') || false;

            if(!isActive){
                $(".client-reply-list").addClass('active');
                $(".__active-all-comment").text("댓글 숨기기");
            }

            var scroll_top = $(".client-reply-section").offset().top;
            var scrollHeight = $(".client-reply-section").height();
            $("body,html").animate({
                scrollTop: scroll_top-scrollHeight
            },500,"easeOutSine", function () {
                $("#client-reply-component").focus();
            });
        });


        $(document).on('click', '.__info-close-button', function (e) {
            $(".screen_block").remove();
            $(".client-like-info-section").remove();
        });


        var board_idx = $("#board_idx").val();

        /*이미 좋아요를 클릭한 경우 하트색 채워주기*/
        if ($("#board_likes_check").val() === "true") {
            // 하트색 채워주기
            $('#board_likes').css('background', 'url(/images/svg/icon_like_fill.svg) no-repeat left center/20px auto');
        }

        // 게시글 삭제 버튼 클릭
        $(".btn_gallery_delete").on("click", function (e) {
            e.preventDefault();
            var result = confirm("정말 삭제하시겠습니까?");
            if (result) {
                $.ajax({
                    url: "/gallery/delete/" + board_idx,
                    type: "GET",
                    dataType: "text",
                    async: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {
                        if (data === '1') {
                            generateModal("존재하지 않는 글입니다.");
                        } else if (data === '2') {
                            generateModal("삭제 권한이 없습니다.");
                        } else if (data === '3') {
                            var url = document.location.href.split("/gallery/view/");
                            $(location).attr("href", url[0] + "/gallery");
                        } else {
                            generateModal("잘못된 통신입니다.");
                        }
                    }, error: function (e) {
                        generateModal("ajax-connect-error",e.status,'통신에러가 발생했습니다', false);
                    }
                });
            }
        });

        //좋아요 갯수

        // 갤러리 좋아요 버튼 클릭
        $("#gallery_likes").on("click", function () {
            $.ajax({
                url: "/gallery/" + board_idx + "/likes",
                type: "GET",
                dataType: "text",
                async: false,
                beforeSend: function (xhr) {
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    var countLikes = parseInt($('#gallery_likes').text());

                    if (data === 'error2') {
                        generateModal("reply-connect-error",'알림','존재하지 않는 게시물입니다.', false);
                        return;
                    }
                    /*: 버리기*/
                    var check = data.split(':');

                    /*콜백 값이 plus라면 증가와 하트의 색 채우기*/
                    if (check[0] === 'plus') {
                        /*좋아요 count 1 증가*/
                        $('#gallery_likes').text(++countLikes);
                        $(".__like-list-interaction").text("좋아요 " + countLikes + "개");
                        // 하트색 채우기
                        $('#gallery_likes').attr('class', '__my-likes');
                    } else if (check[0] === 'minus') {
                        /*좋아요 count 1 감소*/
                        $('#gallery_likes').text(--countLikes);
                        $(".__like-list-interaction").text("좋아요 " + countLikes + "개");
                        // 하트색 지우기
                        $('#gallery_likes').attr('class', '__likes');

                    }
                },
                error: function (e) {
                    generateModal("ajax-connect-error",e.status,'통신에러가 발생했습니다', false);
                }
            });
        });


        $(".__active-all-comment").on('click', function (e) {

            if ($(".client-reply-list").hasClass('active')) {
                $(this).text('댓글 모두 보기');
            } else {
                $(this).text('댓글 숨기기');
            }
            $(".client-reply-list").toggleClass('active');
        });
        
        
        /* 페이스북 공유 이벤트 */
        /*페이스북 게시물 공유하기 클릭*/
		shareFacebookBtn.on('click', function (e) {
			var url = window.location.href;
			var title = $("#gallery_subject").text();
			var description = $("#gallery_contents").text();
			var img_src = $(".gallery-view-section--image > img").attr("src");
			
			var share_object = {
				'og:url': url,
				'og:title': title,
				'og:description': description,
				'og:image': img_src
			};
			
			console.log(share_object);
			
			FB.ui({
				method: 'share_open_graph',
				action_type: 'og.likes',
				action_properties: JSON.stringify({
					object: share_object
				})
			}, function(response){});
			
			
		});
		/*페이스북 게시물 공유하기 클릭*/
    });//JQB
})();//iife