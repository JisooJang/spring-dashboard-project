(function () {
    $(function () {
        var slideBtn = $('.__slide-top');

        slideBtn.on('click', function (e) {
            e.preventDefault();
            $('html,body').stop().animate({
                scrollTop: 0
            }, 1000, "easeOutQuart");


        });//click

        /*검색클릭*/
        $(".search-component-bar").on('click', function () {
            $(this).addClass('active');
        })
        $(".search-component").on('mouseleave', function () {
            $('.search-component-bar').removeClass('active');
        })


    });//jqb
})();//iife

