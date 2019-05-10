/*autosize*/
(function () {

    $(function () {
        var isAlarm = $("#alert_comment_idx").length;
        if(isAlarm > 0){
            setTimeout(function () {
                alramReplyScrollEvent();
            }, 800);
        }
        /*알람창을 통해 게시글 이동시 해당 댓글로 스크롤 하는 이벤트*/
    });//JQB


    /* 기능 : 알람창을 통해서 댓글확인하러 왓을 경우 해당댓글로 스크롤 하는 함수*/
    function alramReplyScrollEvent() {
        var newReplyIdx = $("#alert_comment_idx").val() || null;

        $('.client-reply-list--container[data-reply-idx=' + newReplyIdx + ']').css({
            opacity: 0,
            right: '-50%'
        });


        if (newReplyIdx === null) {

            return false

        } else {
            var replyScrollTarget = $('.client-reply-list--container[data-reply-idx=' + newReplyIdx + ']').offset().top || null;

            $(".__active-all-comment").text('댓글 숨기기');

            $(".client-reply-list").addClass('active');

            $("body,html").animate({

                scrollTop: replyScrollTarget / 1.3

            }, 1500, 'easeOutQuint', function () {

                $('.client-reply-list--container[data-reply-idx=' + newReplyIdx + ']').animate({
                    opacity: 1,
                    right: 0
                }, 1150, 'easeOutCirc');

            });
        }

    }


})();

