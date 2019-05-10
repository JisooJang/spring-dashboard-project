(function () {
    $(function () {
        /*물방울 찰랑이는 이펙트, TimeLneMax에 의존성 갖음*/
        // var tl = new TimelineMax({paused: true});
        //
        // tl.set("#turbwave", {attr: {"baseFrequency": 0.00034}});
        // tl.to("#turbwave", 2, {
        //     attr: {"baseFrequency": 0.002},
        //     repeat: -1,
        //     yoyo: true
        // });
        //
        // TweenLite.set("#turbwave", {
        //     attr: {"baseFrequency": 0}
        // });
        //
        // tl.play();

        /*문의하기 모달 닫기*/
        $(document).on("click", ".inquiry-modal-section .__close-modal-form", function () {
            $(".modal-form").remove();
            $(".screen_block").remove();
        });


        $(window).on('resize', function () {
            $(".modal-form").innerCenter();
        });

        /*문의*/
        $(".seventeenth-section__email").on('click', function (e) {
            e.preventDefault();
            var tag = '<div class="inquiry-email-modal modal-form" data-modal-name="inquiry-email-modal">\n' +
                '    <div class="inquiry-email-modal--buttons">\n' +
                '        <a href="javascript:void(0)" class="__close-modal-form"></a>\n' +
                '    </div>\n' +
                '    <div class="inquiry-email-modal--logo">리틀원의 이미지 로고입니다.</div>\n' +
                '    <div class="inquiry-email-modal--vertical-bar"></div>\n' +
                '    <h1>이메일 문의</h1>\n' +
                '    <form method="post" accept-charset="UTF-8" id="client-inquiry-form" class="inquiry-email-modal--form">\n' +
                '        <fieldset form="client-inquiry-form">\n' +
                '            <legend>이메일로 리틀원에 제품 및 서비스에 대해 문의할 수 있습니다.</legend>\n' +
                '            <div class="inquiry-email-modal--form--container">\n' +
                '                <label for="">이름</label>\n' +
                '                <input type="text" name="name" value="" placeholder="이름을 입력하세요." id="" required="required"/>\n' +
                '                <div data-name="name"></div>' +
                '            </div>\n' +
                '            <div class="inquiry-email-modal--form--container">\n' +
                '                <label for="">이메일</label>\n' +
                '                <input type="email" name="email" value="" placeholder="이메일 주소를 입력하세요." id="" required="required"/>\n' +
                '                <div data-name="email"></div>' +
                '            </div>\n' +
                '            <div class="inquiry-email-modal--form--container">\n' +
                '                <label for="">제목</label>\n' +
                '                <input type="text" name="subject" value="" placeholder="제목을 입력하세요." id="" required="required"/>\n' +
                '                <div data-name="subject"></div>' +
                '            </div>\n' +
                '            <div class="inquiry-email-modal--form--container">\n' +
                '                <label for="">이름</label>\n' +
                '                <textarea name="contents" placeholder="내용을 입력하세요." required="required"></textarea>\n' +
                '            </div>\n' +
                '            <div class="inquiry-email-modal--form--button-container">\n' +
                '                <button type="reset" class="__cancel-button">취소</button>\n' +
                '                <button type="submit" class="__submit-inquiry-button">보내기</button>\n' +
                '            </div>\n' +
                '        </fieldset>\n' +
                '    </form>\n' +
                '</div>';

            $('body').append(tag);

            defaultScreenBlock('inquiry-email-modal');
            $(".modal-form").innerCenter();

            $(".inquiry-email-modal--form--container input").on("keyup", function (e) {

                var that = $(this);
                var len = that.val().length;
                var button = that.next('div');
                var isActive = button.hasClass('active');
                if (len > 0) {
                    if (isActive) {
                        return;
                    }
                    that.next('div').addClass('active');
                } else {
                    that.next('div').removeClass('active');
                }

            }).on("keydown", function (e) {
                var that = $(this);
                var len = that.val().length;
                var button = that.next('div');
                var isActive = button.hasClass('active');
                if (len > 0) {
                    if (isActive) {
                        return;
                    }
                    that.next('div').addClass('active');
                } else {
                    that.next('div').removeClass('active');
                }
            });

            $(".inquiry-email-modal--form--container div").on('click', function (e) {

                if ($(this).hasClass('active')) {
                    $(this).prev('input').val('');
                    $(this).prev('input').focus();
                    $(this).removeClass('active');
                } else {
                    return;
                }
            });

            /*취소버튼 클릭시*/
            $(".__cancel-button").on('click', function(e){
                $('div[data-modal-name="inquiry-email-modal"]').remove();
            });


            $('.__submit-inquiry-button').on('click', function (e) {
                e.preventDefault();

                var formData = new FormData();

                formData.append('name', $("#client-inquiry-form input[name='name']").val());
                formData.append('email', $("#client-inquiry-form input[name='email']").val());
                formData.append('subject', $("#client-inquiry-form input[name='subject']").val());
                formData.append('contents', $("#client-inquiry-form textarea[name='contents']").val());

                /*문의 보내기*/
                $.ajax({
                    url: '/inquireByEmail',
                    type: "POST",
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                    }, success: function (data) {
                        if (data === 'success') {
                         $("div[data-modal-name='inquiry-email-modal']").remove();
                            generateModalInner('success-modal', '성공', "문의된 내용이 메일로 접수되었습니다.<br/> 빠른 시일내에 답변을 도와드리겠습니다.", false);
                        }
                    }, complete: function () {

                    }, error: function (e) {
                        alert(e.status);
                    }
                });
            });

        });//end click


        var greenPath = {
            one: 'M541.317 169.948c-33.91-41.177-73.476-70.409-132.648-105.33C363.035 37.685 288.5 11.05 233.439 2.792c-55.06-8.258-90.923 2.32-131.886 24.816C60.59 50.104 25.256 95.316 13.738 142.903S-2.127 236.1.968 291.38c3.62 64.656 24.306 143.548 44.33 185.018 20.022 41.47 54.599 71.94 92.447 87.217 46.042 18.583 97.844 17.098 143.101 0 45.258-17.099 76.856-32.659 127.823-66.586 50.966-33.927 87.887-67.74 116.62-96.828 28.735-29.088 45.82-64.792 50.73-107.9 4.909-43.108-8.895-90.105-34.702-122.352z',
            two: 'M540.376 181.915c-22.144-30.913-65.039-77.32-122.321-112.775-57.283-35.454-99.616-51.501-137.539-61.58-37.923-10.08-90.832-10.08-131.017 0-40.185 10.079-89.026 61.58-107.16 107.404C24.208 160.787 4.083 239.77 1.572 298.744c-2.71 63.64-3.805 108.854 13.03 155.804 16.833 46.95 46.785 83.388 94.487 105.897 47.702 22.51 100.545 19.41 141.003 6.442 44.83-14.37 87.108-29.814 140.465-63.367 53.358-33.554 84.792-55.511 121.065-93.377 36.273-37.866 60.767-73.28 64.808-118.71 4.04-45.432-13.91-78.604-36.053-109.518z',
        };

        var purplePath = {
            one: 'M530.752 362.109c9.752-68.076-39.012-188.737-98.21-263.415C373.344 24.016 330.504 3.206 270.555.225c-59.95-2.98-107.77 23.79-149.24 71.931-41.47 48.14-61.623 80.755-80.072 120.52C22.793 232.443-3.587 300.937.405 354.26c3.991 53.323 42.612 120.037 125.305 142.859 82.694 22.822 184.502 18.325 264.564 3.6C470.337 485.994 521 430.186 530.752 362.11z',
            two: 'M532.637 308.593c0-78.448-99.36-185.834-150.655-233.457-51.294-47.622-106.429-86.208-170.456-72.24C147.5 16.863 120.53 41.48 96.702 75.136 72.875 108.791 60.6 157.364 36.99 223.606 13.379 289.846-5.114 338.628 2.292 388.387c7.407 49.76 29.504 90.975 114.292 113.586 84.788 22.61 194.367 9.114 283.05-28.318 88.682-37.433 133.003-86.615 133.003-165.063z',
        };

        var violetPath = {
            one: 'M528.805 144.532C521.3 97.183 488.097 48.514 436.778 26.95 385.46 5.383 294.643-5.13 230.926 2.45 167.208 10.033 98.08 23.381 55.647 64.234 13.214 105.086-14.909 166.159 8.443 240.668c23.352 74.51 66.843 148.604 139.515 216.06 72.673 67.455 152.487 70.83 217.185 28.381 64.697-42.45 110.55-135.469 136.267-198.44 25.717-62.971 34.06-100.093 27.395-142.137z',
            two: "M529.748 162.118c-7.595-47.526-37.546-78.624-78.51-104.533-40.965-25.909-89.928-43.92-160.186-53.33C220.794-5.156 112.59-.656 67.279 35.11c-45.312 35.766-80.115 100.185-62.713 178.4 17.401 78.217 50.848 166.437 124.375 234.144s156.799 76.734 207.094 54.487c50.296-22.246 126.115-128.88 152.555-187.967 26.44-59.087 47.902-109.857 41.158-152.057z",
        };


        var violetFrame = Snap.select('#violet-one');
        var greenFrame = Snap.select('#green-one');
        var purpleFrame = Snap.select('#purple-one');

        var purpleAction = function () {
            purpleFrame.animate({
                d: purplePath.one
            }, 5500, mina.easeinout, function () {
                purpleFrame.animate({
                    d: purplePath.two
                }, 5500, mina.easeinout, purpleAction)
            });
        };
        var violetAction = function () {
            violetFrame.animate({
                d: violetPath.one
            }, 2900, mina.easeinout, function () {
                violetFrame.animate({
                    d: violetPath.two
                }, 2750, mina.easeinout, violetAction)
            })
        };

        var greenAction = function () {
            greenFrame.animate({
                d: greenPath.one
            }, 3150, mina.easeinout, function () {
                greenFrame.animate({
                    d: greenPath.two
                }, 3230, mina.easeinout, greenAction)
            });
        };

        purpleAction();
        violetAction();
        greenAction();


    });//JQB
})();//IIFE


