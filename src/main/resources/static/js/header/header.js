(function () {
    $(function () {
        /*마우스 스크롤 다운시 메뉴 사라짐*/
        var lastPosY = 0;
        var headerEle = $(".header") || null;
        if (GetIEVersion() === 0 && headerEle !== null) {
            $(window).on('scroll touchmove mousewheel', function () {
                var scrollPosY = window.pageYOffset || document.documentElement.scrollTop;
                var isAnimated = headerEle.is(":animated");
                if (scrollPosY === null || scrollPosY === undefined || typeof scrollPosY === 'undefined') {
                    return
                }
                if (scrollPosY < 300) {
                    headerEle.removeClass('active');
                } else {
                    headerEle.addClass('active');
                }
                if (scrollPosY > lastPosY && (scrollPosY > 300)) {
                    if (!isAnimated) {
                        headerEle.addClass('scrolling');
                    }
                } else {
                    if (!isAnimated) {
                        headerEle.removeClass('scrolling');
                    }
                }

                console.log(lastPosY, scrollPosY);
                lastPosY = scrollPosY;
            });
        }
    });//jqb
})();