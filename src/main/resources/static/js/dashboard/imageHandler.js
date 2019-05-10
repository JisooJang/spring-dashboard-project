(function () {
    $(function () {

        /******* 이미지 업로드 부분 ***************/

        /*기본 빈 이미지*/
        var defaultImage = 'data:image/png;base64,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';

        /*fileList가 담길 배열*/
        var fileList = [];

        var fileInput = $("#upload-box--hidden");
        /*파일 변경시*/
        fileInput.on("change", function (e) {
            var file = this.files[0];//multipart타입 filelist객체
            var fileSize = file.size;
            var fileType = file.type;
            var name = file.name;

            var isValidSize = fileSize <= 3000000;
            var isValidType = fileType.match(/^image\//);
            var isLimitedArr = fileList.length < 4;

            /*분기*/
            if (isValidType && isValidSize && isLimitedArr) {
                var fileReader = new FileReader();
                var imgAll = $("img[data-order]");

                fileReader.onload = function (e) {

                    file.src = e.target.result;

                    fileList.unshift(file);

                    for (var i = 0; i < fileList.length; i++) {
                        var isValid = fileList[i] != null;
                        if (isValid) {
                            imgAll.eq(i).attr('src', fileList[i].src);
                            imgAll.eq(i).parent().addClass('active');
                            $(".upload-box__upload").val('현재 파일명 : ' + name);
                        }
                    }

                };

                fileReader.readAsDataURL(file);
                var mainImg = document.getElementsByClassName('current-image')[0].classList;
                mainImg.add('active');
                console.log(fileList);

            } else if (!isValidType) {
                alert('사진만 올릴 수 있습니다.');
            } else if (!isValidSize) {
                alert('3MB이하의 사진만 올릴 수 있습니다.');
            } else if (!isLimitedArr) {
                alert('이미지는 4개까지 첨부할 수 있습니다.');
            } else {
                alert("적합하지 않습니다.");
            }

            /*같은 이미지 파일도 올릴 수 있게 마지막에 초기화*/
            e.target.value = null;

        });

        /*이미지 클릭시 메인이미지로 swap*/
        var imageBox = $(".__image-handler");
        imageBox.on('click', function () {
            var isActive = $(this).hasClass('active');
            if (fileList.length === 0 || !isActive) {
                return;
            }

            $(".current-image").addClass('active');
            var order = $(this).attr('data-order');
            fileList.swap(0, order);
            for (var i = 0; i < fileList.length; i++) {
                $(".thumb").eq(i).attr('src', fileList[i].src);
            }
            $(".upload-box__upload").val('현재 파일명 : ' + fileList[0].name);

        });

        /*업로드 한 사진 삭제*/
        $(".__remove-image-button").on('click', function (e) {
            /*배열의 갯수가 4개 이상 일때,*/
            var isFileArr = fileList.length > 0;
            /*부모 객체가 활성화 되어 있을 때*/
            var isActive = $(this).parent().hasClass('active');

            if (isActive && isFileArr) {
                /*맨 첫 번째 파일리스트 객체 삭제*/
                fileList.shift();

                $(".__image-handler[data-order=" + fileList.length + "]").removeClass('active');

                var imgAll = $("img[data-order]");
                for (var i = 0; i < imgAll.length; i++) {
                    var isValid = fileList[i] != null || fileList[i] !== undefined;
                    if (isValid) {
                        imgAll.eq(i).attr('src', fileList[i].src);
                    } else {
                        imgAll.eq(i).attr('src', defaultImage);
                    }
                }


                var currentName;
                if (fileList[0] !== undefined) {
                    currentName = fileList[0].name
                } else {
                    currentName = undefined;
                }
                if (typeof currentName !== 'undefined') {
                    $(".upload-box__upload").val('현재 파일명 :' + currentName);
                } else {
                    $(".upload-box__upload").val('현재 업로드된 파일이 없습니다.');
                    $(".current-image").removeClass('active');
                    shareCheckbox.removeClass('active');
                    $("#share-babybook").val(false);
                }
            } else {
                $(".upload-box__upload").val('현재 업로드된 파일이 없습니다.');
            }
        });

        var shareCheckbox = $('.__shared-checked-box,label[for="share-babybook"]');
        /*대표 이미지를 갤러리에 공유*/
        shareCheckbox.on('click', function (e) {

            var imageSelcted = $(".current-image").hasClass('active');


            if (fileList.length < 1) {
                alert('이미지를 업로드 해주세요.');
                return;
            }
            if (!imageSelcted) {
                alert("이미지를 선택해주세요.")
                return
            }
            var hasActive;
            shareCheckbox.toggleClass('active');
            hasActive = shareCheckbox.hasClass('active');
            if (hasActive) {

                /*공유 여부를 true로 변경*/
                $("#share-babybook").val(1);

            }
            if (!hasActive) {
                /*공유 여부를 false로 변경*/
                $("#share-babybook").val(0);
            }
        });//click

        /*********** 이미지 업로드 부분 ************/
    });//JQB
})();
