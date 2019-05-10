(function () {
    $(function () {
        var diaryList = $(".__diary-list");
        var diaryForm = $("#dashboard-babybook-form");
        var registerDiaryBtn = $(".babybook-list .__register-button");
        var modifyBtn = $("#babybook-handler .__modify-button");

        /*view컴포넌트*/
        var viewBox = $(".dashboard-babybook-view");
        var viewTitle = $(".dashboard-babybook-view--title>h3");
        var viewTime = $(".dashboard-babybook-view--title>time");
        var viewContents = $(".dashboard-babybook-view--contents>p");
        var viewHashtag = $(".dashboard-babybook-view--tags");

        var saveBabyBtn = $(".__save-babybook-button.active");
        var modifyBabyBtn = $(".__modify-babybook-button");
        var babyBookTitle = $('#dashboard-babybook--container--title');
        var babyBookContents = $('#dashboard-babybook--container--contents');
        var babyBookHash = $("input[name='hashtag']");
        var deleteBtn = $("#babybook-handler .__delete-button");


        /*카운트 컴포넌트*/
        var babyCount = $("#babybook-count-component")
        var babyBottomCount = $('.dashboard-babybook--client--info--container__babybook-count span');

        /*기본 빈 이미지*/
        var defaultImage = 'data:image/png;base64,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';

        /*편집/새글*/
        var updateMode = false;

        /* 다이어리 수정시 업로드 파일 관련 수정, 삭제됐을때의 체크값 */
        var uploadModifyChk = false;

        /*편집/삭제할 육아일기의 정보를 담을 객체*/
        var babyEditStatus = {
            idx: null,
            subject: null,
            contents: null,
            date: null,
            tag: null,
            images: null,
            share: null,
            selectedDate: null,
            status: 'hold' //[hold[default], write, edit, delete]
        };
        /*ajax통신*/
        var ajaxReady = true;

        var deletedFileIdx = [];

        /*오늘 날짜 기본 셋팅*/
        var today = new Date();
        $("input[name='event_date']").val(today.toYYYYMMDD());


        /*육아일기 보여주는 슬라이더 view*/
        var imageSlider = $(".slider");
        var bx = imageSlider.bxSlider({
            touchEnabled: false,
            responsive: true,
            pager: true,
            infiniteLoop: true,
            onSliderLoad: function () {
                var currentImage = imageSlider.getCurrentSlide();
                var totalImage = imageSlider.getSlideCount();

                $(".__image-index span").text((currentImage + 1) + '/' + totalImage)
            },
            onSlideNext: function (e) {
                if ($(".bx-wrapper img").is(':animated')) {
                    return;
                }
                var currentImage = imageSlider.getCurrentSlide();
                var totalImage = imageSlider.getSlideCount();

                $(".__image-index span").text((currentImage + 1) + '/' + totalImage)
            },
            onSlidePrev: function (e) {
                if ($(".bx-wrapper img").is(':animated')) {
                    return;
                }
                var currentImage = imageSlider.getCurrentSlide();
                var totalImage = imageSlider.getSlideCount();

                $(".__image-index span").text((currentImage + 1) + '/' + totalImage)
            }
        });


        /*일기목록 클릭시 ajax통신으로 게시물 data 가져오기*/
        $(document).on('click', '.__diary-list', function (e) {
            diaryForm.removeClass('active');
            viewBox.addClass('active');
            var diaryIdx = $(this).attr('data-diary-idx');

            $(this).addClass('active');
            $(this).siblings().removeClass('active');


            if (ajaxReady === true) {

                $.ajax({
                    url: "/dashboard/get_diary/" + diaryIdx,
                    type: "GET",
                    data: '',
                    dataType: "text",
                    beforeSend: function (xhr) {
                        ajaxReady = false;
                        sendCsrfToken(xhr);
                    },
                    success: function (data) {

                        var jsonData = JSON.parse(data);

                        console.log(jsonData);
                        /*선택한 객체의 정보를 객체에 담기*/
                        babyEditStatus.idx = jsonData.idx;
                        babyEditStatus.subject = jsonData.subject;
                        babyEditStatus.contents = jsonData.contents;
                        babyEditStatus.date = jsonData.date_created;
                        var temp = jsonData.hashtag || null;
                        babyEditStatus.tag = temp;
                        babyEditStatus.images = jsonData['id_files'];
                        babyEditStatus.share = jsonData.share;
                        babyEditStatus.selectedDate = jsonData.event_date;
                        babyEditStatus.status = 'hold';

                        viewTitle.text(jsonData.subject);
                        viewContents.text(jsonData.contents);
                        viewTime.text(jsonData.date_created);

                        var arr;
                        viewHashtag.html('');

                        if (temp != null) {
                            arr = Array.from(temp.split(','));
                            arr.forEach(function (ele, idx) {
                                viewHashtag.append('<a class="__link-tag">#' + ele + '</a>');
                            });
                        } else {
                            arr = [];
                        }

                        /*img tag 다 날리기*/
                        imageSlider.find('img').remove();

                        var imgFile = jsonData['id_files'];

                        console.log('현재 파일 명');


                        /*프론트 쪽*/
                        var test = jsonData;
                        if (imgFile.length !== 0 && imgFile !== null && imgFile !== undefined) {
                            imgFile.forEach(function (ele, idx) {
                                var fileUrl = ele !== null && ele !== undefined ? ele.file_url : defaultImage;
                                imageSlider.append('<img/>').find('img').eq(idx).attr('src', fileUrl);
                            });
                        } else {
                            imageSlider.append('<img/>').find('img').eq(0).attr('src', defaultImage);
                        }


                        if (babyEditStatus.share === '0') {
                            $(".dashboard-babybook-view--title>label").text('비공개');
                            $("#share-babybook").val("0");
                        } else if (babyEditStatus.share === '1') {
                            $(".dashboard-babybook-view--title>label").text('전체공개');
                            $("#share-babybook").val("1");
                        }

                        /*bx슬라이더 리로드*/
                        bx.reloadSlider();

                        $(".__modify-babybook-button").attr("diary-idx", babyEditStatus.idx);
                    },
                    error: function (e) {
                        generateModal(e.status + " : ajax 통신 에러");
                    },
                    complete: function () {
                        ajaxReady = true;
                    }
                });//end ajax

            }//end if

        });//click


        /*새글쓰기 클릭*/
        registerDiaryBtn.on('click', function (e) {
            /*파일 리스트 초기화*/
            fileList = [];

            /*해쉬태그 초기화*/
            $(".tag-box--list .__hash-tag").remove();

            updateMode = false;
            diaryForm.addClass('active');
            viewBox.removeClass('active');

            $(".__save-babybook-button").addClass('active');
            $(".__modify-babybook-button").removeClass('active');

            /*fileList객체 초기화*/
            /*filelst객체로 수정까지 커버하자.*/
            fileList = [];
            $(".__image-handler.active").find('img').attr('src', defaultImage);
            $(".__image-handler").removeClass('active');
            $(".current-image").removeClass('active');
            /*share해제*/
            $(".__shared-checked-box ").removeClass('active');
            $("label[for='share-babybook']").removeClass('active');
            /*share 0으로 설정*/
            $("#share-babybook").val(0);
            /*인풋 초기화*/
            $("#dashboard-babybook--container--title").val('');
            $("#dashboard-babybook--container--contents").val('');

            /*스테이터스 초기화*/
            if ('write' !== babyEditStatus.status) {
                babyEditStatus.status = 'write';
            }

            babyEditStatus.subject = null;
            babyEditStatus.contents = null;
            babyEditStatus.date = null;
            babyEditStatus.tag = null;
            babyEditStatus.images = null;
            babyEditStatus.share = null;
            babyEditStatus.idx = null;
            babyEditStatus.selectedDate = null;

            console.log(babyEditStatus);

        });

        /*편집하기 클릭*/
        modifyBtn.on('click', function (e) {

            /*status변경*/
            if ('edit' !== babyEditStatus.status) {
                babyEditStatus.status = 'edit';
            }

            var isValid = babyEditStatus.idx !== null;
            var isValidStatus = 'edit' === babyEditStatus.status;

            if (!isValid || !isValidStatus) {
                return;
            }

            console.log(babyEditStatus);


            /*fileList 배열 초기화*/
            fileList = [];
            updateMode = true;
            diaryForm.addClass('active');
            viewBox.removeClass('active');
            $(".__modify-babybook-button").addClass('active');
            $(".__save-babybook-button").removeClass('active');

            babyBookTitle.val(babyEditStatus.subject);
            babyBookContents.val(babyEditStatus.contents);
            babyBookHash.val(babyEditStatus.tag);


            /*이미지 보여주기*/
            var imageArr = $("img[data-order]");

            /*수정 파일 리스트 배열에 푸쉬*/
            for (var i = 0; i < imageArr.length; i++) {
                var isValid = babyEditStatus.images[i] !== null && babyEditStatus.images[i] !== undefined;
                if (isValid) {
                    /*idx값 추가해서 배열에 추가*/
                    fileList.push({
                        file_url: babyEditStatus.images[i].file_url,
                        idx: babyEditStatus.images[i].idx
                    });

                }
            }

            /*썸네일 보여주기*/
            $.each(imageArr, function (idx, ele) {
                var isValid = babyEditStatus.images[idx] !== null && babyEditStatus.images[idx] !== undefined;
                if (isValid) {
                    $("img[data-order]").eq(idx).attr('src', babyEditStatus.images[idx].file_url);
                    /*부모 박스 활성화 (swap)되게*/
                    $(".__image-handler").eq(idx).addClass('active');
                } else {
                    $("img[data-order]").eq(idx).attr('src', defaultImage);
                    $(".__image-handler").eq(idx).removeClass('active');
                }
            });

            $(".current-image").addClass('active');

            /*해쉬태그*/
            var editArr = babyEditStatus.tag;
            if (!isEmpty(editArr)) {
                editArr = Array.from(editArr.split(','));
            }
            $(".tag-box--list .__hash-tag").remove();
            var hashPosition = $("#hash-station");

            var isHashtag = editArr !== null && editArr !== undefined;
            if (isHashtag) {
                $.each(editArr, function (idx, ele) {
                    var tag = '<a href="javascript:void(0)" class="__hash-tag"><div>#' + ele + '<input type="hidden" name="hashtag" value="' + ele + '"></div><span class="__remove-tag"></span></a>';
                    $(tag).insertBefore(hashPosition);
                });
            }

            /*날짜*/
            $("input[name='event_date']").val(babyEditStatus.date);

            /*공유버튼 */
            var isShare = babyEditStatus.share === '1';
            if (isShare) {
                $(".__shared-checked-box").addClass('active');
                $("label[for='share-babybook']").addClass('active');
                $("#share-babybook").val('1');
            } else {
                $(".__shared-checked-box").removeClass('active');
                $("label[for='share-babybook']").removeClass('active');
                $("#share-babybook").val('0');
            }


        });


        /******* 이미지 업로드 부분 ***************/


        /*fileList가 담길 배열*/
        var fileList = [];

        var fileInput = $("#upload-box--hidden");

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

                if (updateMode === true) {
                    fileReader.onload = function (e) {

                        file.src = e.target.result;

                        var imageForm = new FormData();
                        imageForm.append('image', file);
                        $.ajax({
                            type: 'POST',
                            url: '/dashboard/babyBook/modify_upload',
                            data: imageForm,
                            processData: false,
                            contentType: false,
                            beforeSend: function (xhr) {
                                sendCsrfToken(xhr);
                            },
                            success: function (data) {

                                var object = {
                                    file_url: data,
                                    idx: -1
                                };

                                fileList.unshift(object);


                                for (var i = 0; i < fileList.length; i++) {
                                    var isValid = fileList[i] !== null && fileList[i] !== undefined;
                                    if (isValid) {
                                        if (fileList[i].src !== null && fileList[i].src !== undefined) {
                                            imgAll.eq(i).attr('src', fileList[i].file_url);
                                        } else {
                                            imgAll.eq(i).attr('src', fileList[i].file_url);
                                        }
                                        imgAll.eq(i).parent().addClass('active');
                                        $(".upload-box__upload").val('현재 파일명 : ' + name);
                                    } else {
                                        alert('유효한 파일이 아닙니다.');
                                    }
                                }
                            },
                            error: function (e) {
                                console.log(e.status);
                            },
                            complete: function () {
                            }
                        });


                    }
                } else {
                    fileReader.onload = function (e) {

                        file.src = e.target.result;

                        fileList.unshift(file);

                        for (var i = 0; i < fileList.length; i++) {
                            var isValid = fileList[i] !== null && fileList[i] !== undefined;
                            if (isValid) {
                                if (fileList[i].src !== null && fileList[i].src !== undefined) {
                                    imgAll.eq(i).attr('src', fileList[i].src);
                                } else {
                                    imgAll.eq(i).attr('src', fileList[i].file_url);
                                }
                                imgAll.eq(i).parent().addClass('active');
                                $(".upload-box__upload").val('현재 파일명 : ' + name);
                            } else {
                                alert('유효한 파일이 아닙니다.');
                            }
                        }

                    };
                }


                fileReader.readAsDataURL(file);
                var mainImg = document.getElementsByClassName('current-image')[0].classList;
                mainImg.add('active');
                uploadModifyChk = true;
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

            /*다음에 같은 이미지 파일 올릴 수 있게 마지막에 초기화*/
            e.target.value = null;

        });


        /*이미지 클릭시 메인이미지로 swap*/
        var imageBox = $(".__image-handler");
        imageBox.on('click', function () {
            /*썸네일이 할당되어 active상태일 때만 클릭이 되게 조정*/
            var isActive = $(this).hasClass('active');
            if (fileList.length === 0 || !isActive) {
                return;
            }
            /*메인 썸네일 활성화*/
            $(".current-image").addClass('active');
            var order = $(this).attr('data-order');
            /*순서 바꿈*/
            fileList.swap(0, order);
            /*썸네일 재정렬*/
            for (var i = 0; i < fileList.length; i++) {
                if (!isEmpty(fileList[i].src)) {
                    $("img[data-order]").eq(i).attr('src', fileList[i].src);
                } else {
                    $("img[data-order]").eq(i).attr('src', fileList[i].file_url);
                }
            }
            /*현재 파일명*/
            if (!isEmpty(fileList[0].name)) {
                $(".upload-box__upload").val('현재 파일명 : ' + fileList[0].name);
            } else {
                var s3Name = fileList[0].file_url.substr(fileList[0].file_url.lastIndexOf('/') + 1, fileList[0].file_url.lastIndexOf('.')) || '제목없음';
                $(".upload-box__upload").val('현재 파일명 : ' + s3Name);
            }
            console.log('이미지 교체후 배열');
            console.log(fileList);

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
                /*수정 객체*/
                console.log("삭제 후 배열 ");
                console.log(fileList);
                uploadModifyChk = true;

                /*swap안되게 active제거*/
                $(".__image-handler[data-order=" + fileList.length + "]").removeClass('active');

                var imgAll = $("img[data-order]");
                for (var i = 0; i < imgAll.length; i++) {
                    var isValid = fileList[i] != null || fileList[i] !== undefined;
                    if (isValid) {
                        if (fileList[i].src !== null && fileList[i].src !== undefined) {
                            imgAll.eq(i).attr('src', fileList[i].src);
                        } else {
                            imgAll.eq(i).attr('src', fileList[i].file_url);
                        }
                    } else {
                        imgAll.eq(i).attr('src', defaultImage);
                    }
                }


                var currentName;
                if (!isEmpty(fileList[0])) {

                    if (!isEmpty(fileList[0].name)) {
                        currentName = fileList[0].name || undefined
                        $(".upload-box__upload").val('현재 파일명 :' + currentName);


                    } else {
                        var s3Name = fileList[0].file_url.substr(fileList[0].file_url.lastIndexOf('/') + 1, fileList[0].file_url.lastIndexOf('.')) || '제목없음';
                        $(".upload-box__upload").val('현재 파일명 : ' + s3Name);
                    }

                } else {
                    $(".upload-box__upload").val('현재 업로드된 파일이 없습니다.');
                    $(".current-image").removeClass('active');
                }

                deletedFileIdx.push($(this).attr("file-idx"));	// 삭제파일배열에 idx값 추가
                /*공유 해제*/
                shareCheckbox.removeClass('active');
                $("#share-babybook").val('0');

            } //end if
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
                $("#share-babybook").val('1');

            }
            if (!hasActive) {
                /*공유 여부를 false로 변경*/
                $("#share-babybook").val('0');
            }
        });//click


        /*********** 이미지 업로드 부분 ************/


        /*육아일기 저장하기 버튼 클릭 이벤트*/
        saveBabyBtn.on('click', function (e) {
            e.preventDefault();

            var isValidStatus = 'write' === babyEditStatus.status;

            babyEditStatus.subject = $("#dashboard-babybook--container--title").val();
            babyEditStatus.contents = $("#dashboard-babybook--container--contents").val();
            babyEditStatus.hashtag = $("input[name='hashtag']").val();
            babyEditStatus.date = $("input[name='event_date']").val();
            babyEditStatus.share = $("input[name='share']").val();

            /*모든 정규식 검사를 통과후에 formData 생성*/
            if (ajaxReady && isValidStatus) {

                /*폼데이터 생성*/
                var formData = new FormData();
                formData.append('title', babyEditStatus.subject);
                formData.append('update_contents', babyEditStatus.contents);
                formData.append('share', babyEditStatus.share);
                formData.append('event_date', babyEditStatus.date);

                /*이미지 추가*/
                for (var i = 0; i < fileList.length; i++) {
                    formData.append('images', fileList[i]);
                }

                /*해쉬태그 추가*/
                for (var i = 0; i < $("input[name='hashtag']").length; i++) {
                    formData.append('hashtag', $("input[name='hashtag']").eq(i).val());
                }

                /*yyyy-mm 형식*/
                var currentMonth = babyEditStatus['date'].substring(0, babyEditStatus['date'].lastIndexOf('-'));

                /*통신*/
                $.ajax({
                    url: "/dashboard/babyBook/write",
                    type: "POST",
                    enctype: "multipart/form-data",
                    async: false,
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        var msg = data.result;
                        if (msg === 'db') {
                            alert("데이터 입력 오류입니다. 잠시 후 다시 시도해주세요.");
                        } else if (msg === 'success') {
                            $(".babybook--modal").remove();
                            // 세션유저가 그룹에 속해있으면 소켓 listen
                            // 아래 소켓 연결에서 recipient_idx는 유저가 그룹에 속했으면, 해당 그룹에 속한 개개인 idx로 다 뿌려줘야함.
                            if (typeof $("input[name='family_group_idx']").val() !== "undefined") {
                                var group_member_idx = $("input[name='family_group_idx']").val();	// 그룹 멤버 idx값을 전부 가져옴
                                var group_idx_array = new Array(group_member_idx.length);	// 배열로 변환
                                var requester_idx = $("input[name='session_idx']").val();

                                for (i = 0; i < group_idx_array.length; i++) {
                                    if (requester_idx === group_idx_array[i]) {
                                        continue;
                                    }
                                    group_request_added(requester_idx, group_idx_array[i]);
                                }
                            }

                            /*성공한 통신결과를 배열로 담는다.*/
                            var renderArr = data.diary_list;

                            /*갱신 전list 초기화*/
                            $("#babybook-list-container").html('');

                            for (var j in renderArr) {
                                if (renderArr.hasOwnProperty(j)) {
                                    $("#babybook-list-container").append(renderBabybookList(renderArr[j]));
                                    var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                    $("#babybook-list-container").find('li').eq(j).stop().delay(timer).animate({
                                        opacity: 1
                                    }, 1200, 'easeOutCubic');
                                }
                            }//end for ~in


                            /*숫자 갱신*/
                            babyCount.addClass('active').text('+' + renderArr.length);

                            /*0->1로 글 쓰는 거라면*/
                            if (renderArr.length === 1) {
                                $(".dashboard-babybook--client--info--container__babybook-count").html('<div><h2>일기 수</h2><span title="1개의 육아일기가 작성되었습니다.">1</span><em>개</em></div>');
                                babyBottomCount.text(renderArr.length);
                            } else {
                                babyCount.text('+' + renderArr.length);
                                $(".dashboard-babybook--client--info--container__babybook-count span").text(renderArr.length);
                            }


                            /*status 초기화*/
                            babyEditStatus.idx = null;
                            babyEditStatus.subject = null;
                            babyEditStatus.contents = null;
                            babyEditStatus.date = null;
                            babyEditStatus.tag = null;
                            babyEditStatus.images = null;
                            babyEditStatus.share = null;
                            babyEditStatus.selectedDate = null;
                            babyEditStatus.status = 'hold';

                            setTimeout(function () {
                                $("#babybook-list-container").find('li').eq(0).click();
                            }, 500);


                            /*ajax통신안에서 다시 한 번 ajax통신 달력 점찍기 통신*/
                            $.ajax({
                                url: "/dashboard/babybook/checkByMonth",
                                type: "POST",
                                async: false,
                                data: {
                                    date: currentMonth	// yyyy-mm (이값 넣어주세요)
                                },
                                dataType: "json",
                                beforeSend: function (xhr) {
                                    sendCsrfToken(xhr);
                                    ajaxReady = false;
                                },
                                success: function (data) {
                                    /*서버에서 넘어온 값과, 현재 년도와 달을 보낸다 yyyy-mm*/
                                    updatePicker(data, currentMonth);
                                },
                                complete: function () {

                                },
                                error: function (e) {
                                    generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                                }
                            });//inner ajax

                        } else if (msg === 'title_required') {
                            alert("제목을 입력해주세요.");
                        } else if (msg === 'contents_required') {
                            alert("내용을 입력해주세요.");
                        } else if (msg === 'no_infant') {
                            alert("등록된 아이가 없습니다. 아이정보를 등록해주세요.");
                        } else {
                            alert("잘못된 통신입니다.");
                        }  //end else if

                    },
                    error: function (e) {
                        generateModal('ajax-error-modal', e.status, 'ajax 서버 통신 에러입니다.', false);
                    },
                    complete: function () {
                        ajaxReady = true;
                    }
                });//ajax
            } else if (!isValidStatus) {
                alert('비정상적인 접근 방법입니다.');
            }
        });

        /*육아일기 수정하기 버튼 클릭 이벤트*/
        modifyBabyBtn.on('click', function (e) {
            e.preventDefault();

            var isValidStatus = 'edit' === babyEditStatus.status;
            var isActive = $(this).hasClass('active');
            if (isActive && isValidStatus) {

                /* # 수정 ajax 통신*/
                babyEditStatus.subject = $("#dashboard-babybook--container--title").val();
                babyEditStatus.contents = $("#dashboard-babybook--container--contents").val();
                babyEditStatus.share = $("input[name='share']").val();
                var diaryIdx = babyEditStatus.idx;	//일기목록 li 태그 data-diary-idx값 필요

                var formData = new FormData();
                formData.append('update_title', babyEditStatus.subject);
                formData.append('update_contents', babyEditStatus.contents);
                formData.append('update_share', babyEditStatus.share);
                for (var i = 0; i < $("input[name='hashtag']").length; i++) {
                    formData.append('update_hashtag', $("input[name='hashtag']").eq(i).val());
                }
                formData.append('uploadModifyChk', uploadModifyChk);

                /*둘중에 뭘로 들어가야함?*/
                //formData.append('update_date', babyEditStatus.date);	// 수정한 날짜
                formData.append('selected_date', babyEditStatus.selectedDate); //여기에 event_date들어있음.


                /*수정은 작성과 다르게 s3 url과 파일리스트가 섞이게 됨*/
                for (var i = 0; i < fileList.length; i++) {
                    if (fileList[i].file_url != null) {
                        //formData.append('update_file_url', JSON.stringify(fileList[i]));
                        formData.append('update_file', JSON.stringify(fileList[i]));
                    } else {
                        formData.append('update_file', fileList[i]);
                    }
                }

                /*yyyy-mm 형식*/
                var currentMonth = babyEditStatus['date'].substring(0, babyEditStatus['date'].lastIndexOf('-'));


                /*수정 ajax통신*/
                $.ajax({
                    url: "/dashboard/modify_diary/" + diaryIdx,
                    type: "POST",
                    enctype: "multipart/form-data",
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        var msg = data.result;

                        if (msg === 'db') {
                            alert("데이터 입력 오류입니다. 잠시 후 다시 시도해주세요.");
                        } else if (msg === 'success') {
                            /*성공한 통신결과를 배열로 담는다.*/
                            var renderArr = null;

                            /*값이 있다면*/
                            if (data['diary_list'].length > 0) {

                                renderArr = data.diary_list;
                                /*갱신 전list 초기화*/
                                $("#babybook-list-container").html('');

                                for (var j in renderArr) {
                                    if (renderArr.hasOwnProperty(j)) {
                                        $("#babybook-list-container").append(renderBabybookList(renderArr[j]));
                                        var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                        $("#babybook-list-container").find('li').eq(j).stop().delay(timer).animate({
                                            opacity: 1
                                        }, 1200, 'easeOutCubic');
                                    }
                                }//end for ~in


                                setTimeout(function () {
                                    $("#babybook-list-container").find('li').eq(0).click();
                                }, 500);


                                /*ajax통신안에서 다시 한 번 ajax통신 달력 점찍기 통신*/
                                $.ajax({
                                    url: "/dashboard/babybook/checkByMonth",
                                    type: "POST",
                                    async: false,
                                    data: {
                                        date: currentMonth	// yyyy-mm (이값 넣어주세요)
                                    },
                                    dataType: "json",
                                    beforeSend: function (xhr) {
                                        sendCsrfToken(xhr);
                                        ajaxReady = false;
                                    },
                                    success: function (data) {
                                        /*서버에서 넘어온 값과, 현재 년도와 달을 보낸다 yyyy-mm*/
                                        updatePicker(data, currentMonth);
                                    },
                                    complete: function () {

                                    },
                                    error: function (e) {
                                        generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                                    }
                                });//inner ajax


                            } else {
                                /*화면 active요소들 전부 해제하기*/
                                $("#dashboard-babybook-form").removeClass('active');
                                $(".dashboard-babybook-view").removeClass('active');
                                $("#babybook-list-container").html('');
                                $("#babybook-list-container").append(noBabybookList());
                            }


                            /*사용한 객체 초기화*/
                            babyEditStatus = {
                                idx: null,
                                subject: null,
                                contents: null,
                                date: null,
                                tag: null,
                                images: null,
                                share: null,
                                selectedData: null,
                                status: 'hold'
                            };


                        } else if (msg === 'no_authority') {
                            alert("수정 권한이 없습니다.");
                        } else if (msg === 'no_infant') {
                            alert("수정 권한이 없습니다.");
                        } // end else if

                    },
                    error: function (e) {
                        generateModal('ajax-error-modal', e.status, 'ajax 서버 통신 에러입니다.', false);
                    },
                    complete: function () {
                        ajaxReady = true;
                    }
                })


            }//end if
        });

        /*육아일기 삭제하기*/
        deleteBtn.on('click', function () {

            /*status변경*/
            babyEditStatus.status = 'delete';

            var isValid = !isEmpty(babyEditStatus.idx);
            var isValidStatus = 'delete' === babyEditStatus.status;

            if (ajaxReady === true && isValid === true && isValidStatus) {
                generateModal('delete-diary-modal', '삭제', '삭제하시겠습니까?', true, function () {
                    $.ajax({
                        type: 'GET',
                        url: '/dashboard/delete_diary/' + babyEditStatus.idx + '/' + babyEditStatus.selectedDate,
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                            ajaxReady = false;
                        },
                        success: function (data) {
                            var msg = data.result;
                            var currentMonth = babyEditStatus['date'].substring(0, babyEditStatus['date'].lastIndexOf('-'));

                            if (msg === 'success') {
                                generateModal('delete-success-modal', '성공', '육아일기의 삭제가 완료되었습니다.', false);

                                /*성공한 통신결과를 배열로 담는다.*/
                                var renderArr = null;
                                if (data.diary_list !== null) {
                                    renderArr = data.diary_list;
                                }


                                /*갱신 전list 초기화*/
                                $("#babybook-list-container").html('');

                                for (var j in renderArr) {
                                    if (renderArr.hasOwnProperty(j)) {
                                        $("#babybook-list-container").append(renderBabybookList(renderArr[j]));
                                        var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                        $("#babybook-list-container").find('li').eq(j).stop().delay(timer).animate({
                                            opacity: 1
                                        }, 1200, 'easeOutCubic');
                                    }
                                }//end for ~in

                                if (renderArr === null) {
                                    $("#babybook-list-container").append(noBabybookList());
                                }

                                /*카운터 갱신*/
                                if (renderArr != null) {
                                    babyCount.text('+' + renderArr.length);
                                    $(".dashboard-babybook--client--info--container__babybook-count span").text(renderArr.length)
                                } else {
                                    babyCount.removeClass('active').text('');
                                    $(".dashboard-babybook--client--info--container__babybook-count").html('<div><h2>일기 수</h2><p>기록 없음</p></div>');
                                }


                                /*사용한 객체 초기화*/
                                babyEditStatus = {
                                    idx: null,
                                    subject: null,
                                    contents: null,
                                    date: null,
                                    tag: null,
                                    images: null,
                                    share: null,
                                    selectedData: null,
                                    status: 'hold'
                                };

                                console.log(babyEditStatus);

                                setTimeout(function () {
                                    $("#babybook-list-container").find('li').eq(0).click();
                                    if ($("#babybook-list-container li").length < 1) {
                                        $(".dashboard-babybook-view").removeClass('active');
                                    }
                                }, 300);

                                /*ajax통신안에서 다시 한 번 ajax통신 달력 점찍기 통신*/
                                $.ajax({
                                    url: "/dashboard/babybook/checkByMonth",
                                    type: "POST",
                                    async: false,
                                    data: {
                                        date: currentMonth	// yyyy-mm (이값 넣어주세요)
                                    },
                                    dataType: "json",
                                    beforeSend: function (xhr) {
                                        sendCsrfToken(xhr);
                                        ajaxReady = false;
                                    },
                                    success: function (data) {
                                        /*서버에서 넘어온 값과, 현재 년도와 달을 보낸다 yyyy-mm*/
                                        updatePicker(data, currentMonth);
                                    },
                                    complete: function () {

                                    },
                                    error: function (e) {
                                        generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                                    }
                                });//inner ajax
                            } else if (msg === 'no_infant') {
                                generateModal('delete-success-modal', '실패', '잘못된 접근입니다.', false);
                            } else if (msg === 'no_authoritiy_infant') {
                                generateModal('delete-success-modal', '실패', '삭제 권한이 없습니다.', false);
                            }
                        },
                        error: function (e) {
                            console.log(e.status);
                        },
                        complete: function () {
                            ajaxReady = true;
                        }
                    });//end ajax

                });
            }//end if
        });//end click

        /*육아일기 제목 타이핑 할 때*/
        babyBookTitle.on('keyup', function (e) {
            $(this).siblings('.__validation-text').removeClass('active');
        }).on('focus', function () {
            $(this).siblings('.__validation-text').removeClass('active');
        });

        /*육아일기 내용 타이핑 할 때*/
        babyBookContents.on('keyup', function (e) {
            $(this).siblings('.__validation-text').removeClass('active');
        }).on('focus', function () {
            $(this).siblings('.__validation-text').removeClass('active');
        });

        /*제목 클릭시 포커스 주기*/
        $(".title-input").on('click', function (e) {
            $(this).find('input').focus();
        });
        /*내용 클릭시 포커스 주기*/
        $(".contents-input").on('click', function (e) {
            $(this).find('input').focus();
        });
        $(".__validation-text").on('click', function (e) {
            $(this).siblings('label').focus();
        });


        /*육아일기&육아일정 모달 창 닫기*/
        var closeBabybookBtn = $(".dashboard-babybook--box .__close-button");
        var babyBookModalEle = $(".dashboard-babybook--main-modal");

        /*모달창 닫기*/
        closeBabybookBtn.on('click', function (e) {
            babyBookModalEle.toggleClass('active');
            $(".babybook-blocker").remove();
            $('.babybook-blocker').off('scroll touchmove mousewheel');

        });
        /*육아일기&육아일정 모달 창 닫기*/





        /*이미지 다운로드 아이콘 클릭*/
        $(".__download-image").on('click', function (e) {
            e.preventDefault();

            var x = new XMLHttpRequest();
            x.open("GET", "https://s3.ap-northeast-2.amazonaws.com/littleone/dashboard/diary/214/181122121749_86e5563f73.jpg", true);
            x.responseType = 'blob';
            x.onload = function (e) {
                download(x.response, "test.jpg", "image/jpeg");
            };
            x.send();

        });

        /*더보기 버튼 클릭시*/
        $(".dashboard-babybook-view--contents .__more-button").on('click', function (e) {
            e.preventDefault();

            var switches = $(this).prev().hasClass('active');
            if (switches) {
                $(this).text('더보기');
            } else {
                $(this).text('감추기');
            }

            $(this).prev().toggleClass('active');
        });


    });//jqb
})();//iife

var noneImage = 'data:image/png;base64,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';

function renderBabybookList(object) {

    var tag = '   <li role="listitem" class="__diary-list rendered" data-diary-idx="' + (object.idx) + '" data-diary-time="' + (object.date_created) + '" data-diary-subject="' + (object.subject) + '" data-diary-contents="' + (object.contents) + '">\n' +
        '            <a href="javascript:void(0)" role="link">\n' +
        '                <div class="list--info">\n' +
        '                    <div class="list--info--time">\n' +
        '                        <time>' + (object.date_created) + '</time>\n' +
        '                    </div>\n' +
        '                    <div class="list--info--status">\n' +
        '                        <span class="appointment"></span>\n' +
        '                    </div>\n' +
        '                    <div class="list--info--desc">\n' +
        '                        <p>' + (object.subject) + '</p>\n' +
        '                    </div>\n' +
        '                    <div class="list--info--image">\n' +
        //'                        <img src="' + (object.id_files[0] !== null && object.id_files[0] !== undefined ? object.id_files[0].file_url : noneImage) + '">\n' +
        '                        <img src="' + (object.id_files !== undefined && object.id_files.length > 0 ? object.id_files[0].file_url : noneImage) + '">\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </a>\n' +
        '        </li>'
    return tag;
}


function updatePicker(data, selectedMonth) {
    /*데이트 픽커 업데이트*/
    var updatePicker = $(".date-picker").datepicker().data('datepicker');

    updatePicker.update({
        onRenderCell: function (date, cellType) {
            var currentMonth = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1;
            currentMonth = date.getFullYear() + '-' + currentMonth;
            var currentDate = date.getDate() < 10 ? '0' + date.getDate() : date.getDate().toString();
            if (currentMonth === selectedMonth) {
                var arr = data;
                var tags = [];
                for (var i in arr) {
                    var objectDate = arr[i].event_date_start < 10 ? '0' + (arr[i].event_date_start) : arr[i].event_date_start;
                    if (arr.hasOwnProperty(i) && (objectDate === currentDate) && cellType === 'day') {
                        var temp = arr[i].event_type.split(',');

                        temp.map(function (key, i) {

                            var eventType = null;
                            var tag = null;

                            if (key === '2') {
                                eventType = 'reserved-color';
                                tag = '<span class="' + eventType + '"></span>\n';
                                tags.push(tag);

                            } else if (key === '1') {
                                eventType = 'babybook-color';
                                tag = '<span class="' + eventType + '"></span>\n';
                                tags.push(tag);
                            }
                        });//end map
                    } //end if
                }//end for

                return {
                    html: date.getDate().toString() + '<div class="datepicker--cell-day--event-box">' + tags.map(function (key, i) {
                        return key;
                    }).join('\n') + '</div>'
                }//end return
            }
        }//onrender
    });//update picker
}