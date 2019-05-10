(function () {
    $(function () {
        /*최초 ajax통신 상태는 true(ready)로 셋팅, */
        var ajaxReady = true;

        var scheduleSaveBtn = $(".__save-schedule-button");
        var scheduleModifyBtn = $(".__modify-schedule-button");
        var schedulAddBtn = $("#schedule-handler .__register-button");
        var scheduleEditBtn = $("#schedule-handler .__modify-button");
        var scheduleDeleteBtn = $("#schedule-handler  .__delete-button");

        /*오늘 날짜 구하는 객체*/
        var todayObject = new Date();
        var currentMonth = todayObject.getMonth() + 1 < 10 ? "0" + (todayObject.getMonth() + 1) : todayObject.getMonth() + 1;
        var currentYear = todayObject.getFullYear();
        var currentDay = todayObject.getDate() < 10 ? "0" + (todayObject.getDate()) : todayObject.getDate();

        /* -로 틀 갖추기*/
        var today = [currentYear, currentMonth, currentDay].join('-');
        var eventMonth = [currentYear, currentMonth].join('-');

        /*스케쥴 객체*/
        var targetScheduleIdx = null;

        /*picker*/
        var startPicker = document.querySelector('.dashboard-babybook--container__schedule.start');
        var endPicker = document.querySelector('.dashboard-babybook--container__schedule.end');
        var back = $(".schedule-selection");


        /*일정,일기목록 카운트*/
        var schedulerCount = $("#schedule-count-component");
        var babybookCount = $("#babybook-count-component");

        var bottomScheduleCount = $(".dashboard-babybook--client--info--container__schedule-count p");

        /*윈도우 너비*/
        var windowWidth = getCurrentWidth();

        /*쓰기, 편집, 삭제할 객체의 정보*/
        var scheduleState = {
            idx: '',
            title: '',
            eventType: '',
            startDate: '',
            endDate: '',
            selectedDate: '', //yyyy-mm-dd
            status: 'hold' //edit, write, delete, hold(기본,default)
        };

        /*이벤트 날짜 객체가 들어오는 값 ex) 현월 ㅇㅇ일에 이벤트가 있다.*/
        var eventDates = [];

        /*이벤트 날짜 통신하여 달력에 표시*/
        $.ajax({
            url: "/dashboard/babybook/checkByMonth",
            type: "POST",
            data: {
                date: eventMonth	// yyyy-mm
            },
            dataType: "json",
            beforeSend: function (xhr) {
                sendCsrfToken(xhr);
            },
            success: function (data) {
                /*가져온 이벤트 날짜 삽입*/
                eventDates = data;

                /*가져온 이벤트 날짜(필요:년월일,이벤트타입)*/
                console.log('달력 이벤트 날짜');
                console.log(eventDates);

            },
            error: function (e) {
                generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
            }
        });	// 월별 다이어리 작성여부 체크
        /*오늘 날짜 구하기 끝*/

        /*스케쥴 이벤트 선택*/
        var eventIcon = $(".__event-type");

        eventIcon.on('click', function (e) {
            e.preventDefault();
            var eventType = $(this).attr('data-event-type');
            $(this).siblings().removeClass('active');
            $(this).addClass('active');
            $("input[name='event_type']").val(eventType);
        });

        $(window).on('resize', function (e) {
            windowWidth = getCurrentWidth();
        });

        /*모달창안에 달력*/
        var selectedDate = $("input[name='event_date']");

        setTimeout(function () {
            $(".date-picker").datepicker({
                inline: true,
                language: 'en',
                time: '',
                onSelect: function (formattedDate, date) {
                    if (date === '' || date === ' ') {
                        return;
                    }

                    date = new Date(date);

                    var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
                    var day = date.getDate() < 10 ? "0" + (date.getDate()) : date.getDate();
                    var year = date.getFullYear();
                    var dateVal = [year, month, day].join('-');

                    var week = ['일', '월', '화', '수', '목', '금', '토'];
                    var days = week[date.getDay()];

                    /*육아일기 event_date 설정*/
                    selectedDate.val(dateVal);

                    /*화면 날짜 변경*/
                    $(".dashboard-babybook--client--info--date").text(dateVal + ' (' + days + ')');

                    /* 달력에서 날짜를 클릭하면 해당날짜의 육아일기와 육아일정을 불러오는 ajax 통신 */
                    if (ajaxReady === true) {

                        $.ajax({
                            url: "/dashboard/babybook/list_view_byDate",
                            type: "POST",
                            data: {
                                date: dateVal
                            },
                            dataType: "text",
                            beforeSend: function (xhr) {
                                sendCsrfToken(xhr);
                                ajaxReady = false;
                                console.log('일정,일기 통신 시작..');
                            },
                            success: function (data) {
                                var formatData;
                                var diaryList;
                                var scheduleList;

                                if (!isEmpty(data)) {
                                    formatData = JSON.parse(data) || null;
                                }

                                if (formatData !== null && formatData !== undefined) {
                                    diaryList = formatData.diary_list || null;
                                    scheduleList = formatData.schedule_list || null;
                                }

                                console.log('육아일기 리스트--');
                                console.log(diaryList);
                                console.log('육아일정 리스트--');
                                console.log(scheduleList);

                                /*일단 초기화*/
                                $("#babybook-list-container").html('');
                                $("#client-schedule-list").html('');


                                /*모바일용과 데스크탑용으로 처리를 분기*/

                                if (windowWidth <= 768) {
                                    /*해상도가 768px보다 작을 경우 (모바일용)*/

                                    /*현재 일정 탭이 켜져 있을 경우*/
                                    var scheduleActive = $(".__tab-button.__schedule-list").hasClass('active');
                                    /*현재 일기 탭이 켜져 있을 경우*/
                                    var babyBookActive = $(".__tab-button.__babybook-list").hasClass('active');

                                        if (!isEmpty(scheduleList)) {
                                            for (var j in scheduleList) {
                                                if (scheduleList.hasOwnProperty(j)) {
                                                    $("#client-schedule-list").append(renderScheduleList(scheduleList[j]));
                                                    var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                                    $("#client-schedule-list").find('li').eq(j).stop().delay(timer).animate({
                                                        opacity: 1
                                                    }, 1200, 'easeOutCubic');
                                                }
                                            }//end for ~in

                                            $(".dashboard-babybook--client--info--container__schedule-count p").text(scheduleList.length);

                                            var tag = '<div>\n' +
                                                '                    <h2>일정 수</h2>\n' +
                                                '                    <span title="' + (scheduleList.length + '의 일정이 있습니다.') + '">' + scheduleList.length + '</span><em>개</em>\n' +
                                                '                    \n' +
                                                '                </div>';

                                            $(".dashboard-babybook--client--info--container__schedule-count").html('');

                                            $(".dashboard-babybook--client--info--container__schedule-count").append(tag);

                                            // $("#client-schedule-list").find('li').eq(0).click();

                                            schedulerCount.addClass('active').text('+' + scheduleList.length);


                                        } else {
                                            $("#client-schedule-list").append(noScheduleList());
                                            var notag = '<div>\n' +
                                                '                    <h2>일정 수</h2>\n' +
                                                '                    \n' +
                                                '                    <p>기록 없음</p>\n' +
                                                '                </div>';
                                            $(".dashboard-babybook--client--info--container__schedule-count").html('');
                                            $(".dashboard-babybook--client--info--container__schedule-count").append(notag);
                                            /*스케쥴 view 삭제*/
                                            $("#schedule-list-viewbox").html('');
                                            /*스케쥴 form 비활성화*/
                                            $("#dashboard-schedule-form").removeClass('active');

                                            schedulerCount.removeClass('active').text('');
                                        } //end if~else



                                        if (!isEmpty(diaryList)) {

                                            for (var j in diaryList) {
                                                if (diaryList.hasOwnProperty(j)) {
                                                    $("#babybook-list-container").append(renderBabybookList(diaryList[j]));
                                                    var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                                    $("#babybook-list-container").find('li').eq(j).stop().delay(timer).animate({
                                                        opacity: 1
                                                    }, 1200, 'easeOutCubic');
                                                }
                                            }//end for ~in

                                            var tag = '<div>\n' +
                                                '                    <h2>일기 수</h2>\n' +
                                                '                    <span title="' + (diaryList.length + '의 일기가 있습니다.') + '">' + diaryList.length + '</span><em>개</em>\n' +
                                                '                    \n' +
                                                '                </div>';

                                            $(".dashboard-babybook--client--info--container__babybook-count").html('');
                                            $(".dashboard-babybook--client--info--container__babybook-count").append(tag);
                                            // $("#babybook-list-container").find('li').eq(0).click();

                                            /*일정 수,일기 수 변경*/
                                            babybookCount.addClass('active').text('+' + diaryList.length);


                                        } else {
                                            $("#babybook-list-container").append(noBabybookList());
                                            var notag = '<div>\n' +
                                                '                    <h2>일기 수</h2>\n' +
                                                '                    \n' +
                                                '                    <p>기록 없음</p>\n' +
                                                '                </div>';
                                            $(".dashboard-babybook--client--info--container__babybook-count").html('');
                                            $(".dashboard-babybook--client--info--container__babybook-count").append(notag);

                                            /*육아일기 view active제거*/
                                            $(".dashboard-babybook-view").removeClass('active');

                                            babybookCount.removeClass('active').text('');

                                        }


                                } else {
                                    /*데스크탑 해상도*/

                                    /*육아일기 객체 배열 반복*/
                                    if (!isEmpty(diaryList)) {

                                        for (var j in diaryList) {
                                            if (diaryList.hasOwnProperty(j)) {
                                                $("#babybook-list-container").append(renderBabybookList(diaryList[j]));
                                                var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                                $("#babybook-list-container").find('li').eq(j).stop().delay(timer).animate({
                                                    opacity: 1
                                                }, 1200, 'easeOutCubic');
                                            }
                                        }//end for ~in

                                        var tag = '<div>\n' +
                                            '                    <h2>일기 수</h2>\n' +
                                            '                    <span title="' + (diaryList.length + '의 일기가 있습니다.') + '">' + diaryList.length + '</span><em>개</em>\n' +
                                            '                    \n' +
                                            '                </div>';

                                        $(".dashboard-babybook--client--info--container__babybook-count").html('');
                                        $(".dashboard-babybook--client--info--container__babybook-count").append(tag);
                                        $("#babybook-list-container").find('li').eq(0).click();

                                        /*일정 수,일기 수 변경*/
                                        babybookCount.addClass('active').text('+' + diaryList.length);


                                    } else {
                                        $("#babybook-list-container").append(noBabybookList());
                                        var notag = '<div>\n' +
                                            '                    <h2>일기 수</h2>\n' +
                                            '                    \n' +
                                            '                    <p>기록 없음</p>\n' +
                                            '                </div>';
                                        $(".dashboard-babybook--client--info--container__babybook-count").html('');
                                        $(".dashboard-babybook--client--info--container__babybook-count").append(notag);

                                        /*육아일기 view active제거*/
                                        $(".dashboard-babybook-view").removeClass('active');

                                        babybookCount.removeClass('active').text('');

                                    } //end if~else

                                    /*육아일정 객체 배열 반복*/
                                    if (!isEmpty(scheduleList)) {
                                        for (var j in scheduleList) {
                                            if (scheduleList.hasOwnProperty(j)) {
                                                $("#client-schedule-list").append(renderScheduleList(scheduleList[j]));
                                                var timer = j * 250 <= 1000 ? j * 250 : 1000;
                                                $("#client-schedule-list").find('li').eq(j).stop().delay(timer).animate({
                                                    opacity: 1
                                                }, 1200, 'easeOutCubic');
                                            }
                                        }//end for ~in
                                        $(".dashboard-babybook--client--info--container__schedule-count p").text(scheduleList.length);

                                        var tag = '<div>\n' +
                                            '                    <h2>일정 수</h2>\n' +
                                            '                    <span title="' + (scheduleList.length + '의 일정이 있습니다.') + '">' + scheduleList.length + '</span><em>개</em>\n' +
                                            '                    \n' +
                                            '                </div>';
                                        $(".dashboard-babybook--client--info--container__schedule-count").html('');
                                        $(".dashboard-babybook--client--info--container__schedule-count").append(tag);

                                        $("#client-schedule-list").find('li').eq(0).click();
                                        schedulerCount.addClass('active').text('+' + scheduleList.length);


                                    } else {
                                        $("#client-schedule-list").append(noScheduleList());
                                        var notag = '<div>\n' +
                                            '                    <h2>일정 수</h2>\n' +
                                            '                    \n' +
                                            '                    <p>기록 없음</p>\n' +
                                            '                </div>';
                                        $(".dashboard-babybook--client--info--container__schedule-count").html('');
                                        $(".dashboard-babybook--client--info--container__schedule-count").append(notag);
                                        /*스케쥴 view 삭제*/
                                        $("#schedule-list-viewbox").html('');
                                        /*스케쥴 form 비활성화*/
                                        $("#dashboard-schedule-form").removeClass('active');

                                        schedulerCount.removeClass('active').text('');

                                    } //end if~else


                                }





                            },
                            error: function (e) {
                                ajaxReady = true;
                                generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                            },
                            complete: function () {
                                ajaxReady = true;
                            }
                        });//end of ajax
                    }

                },
                onRenderCell: function (date, cellType) {

                    var today = new Date();
                    var todayYear = today.getFullYear();
                    var todayMonth = today.getMonth() + 1 < 10 ? '0' + (today.getMonth() + 1) : (today.getMonth() + 1).toString();

                    var todayValue = todayYear + '-' + todayMonth;

                    /*렌더링 되는 요소의 날짜*/
                    var currentDate = date.getDate() < 10 ? '0' + date.getDate() : date.getDate().toString();

                    /*현재년도-월*/
                    var currentMonth = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : (date.getMonth() + 1).toString();
                    currentMonth = date.getFullYear() + '-' + currentMonth;


                    if (todayValue === currentMonth) {
                        var arr = eventDates;

                        var tags = [];

                        for (var i in arr) {

                            var objectDate = arr[i].event_date_start < 10 ? '0' + arr[i].event_date_start : arr[i].event_date_start;

                            if (arr.hasOwnProperty(i) && (objectDate === currentDate)) {

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
                                        tags.push(tag)
                                    }
                                });
                            }

                        }//for


                        return {
                            html: date.getDate().toString() + '<div class="datepicker--cell-day--event-box">' + tags.map(function (key, i) {
                                return key;
                            }).join('\n') + '</div>'
                        };
                    }


                },
                onChangeMonth: function (month, year) {

                    var selectedMonth = year + '-' + ((month + 1 < 10 ? '0' + (month + 1) : month + 1));

                    $.ajax({
                        url: "/dashboard/babybook/checkByMonth",
                        type: "POST",
                        data: {
                            date: selectedMonth
                        },
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                        },
                        success: function (data) {
                            console.log('달력 이동!!! ');
                            console.log(data);
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

                        },
                        complete: function () {

                        },
                        error: function (e) {
                            generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                        }
                    });//ajax
                }
            });
        }, 450);

        /*일정목록 일정추가 클릭시*/
        schedulAddBtn.on('click', function (e) {

            /*status 변경*/

            console.log(scheduleState);

            /*상태를 쓰기로 변경후 초기화*/
            scheduleState = {
                idx: '',
                title: '',
                eventType: '0',
                startDate: '',
                endDate: '',
                selectedDate: '', //yyyy-mm-dd
                status: 'write' //edit, write, delete, hold(기본,default)
            };

            console.log(scheduleState);

            targetScheduleIdx = null;
            $("#schedule-list-viewbox").html("");
            $(".__modify-schedule-button").removeClass('active');
            $("#dashboard-schedule-form").addClass('active');
            $(".__save-schedule-button").addClass('active');
            $("#schedule-type-container").find('.__event-type').eq(0).addClass('active').siblings().removeClass('active');
            $("input[name='event_type']").val('0');

            /*폼초기화*/
            $("#dashboard-schedule-form")[0].reset();
            var week = ['일', '월', '화', '수', '목', '금', '토'];

            var startDate = new Date();
            var endDate = new Date();

            (function (startDate) {
                var year = startDate.getFullYear().toString();
                //var month = (startDate.getMonth() + 1).toString();
                var month = (startDate.getMonth() + 1) < 10 ? '0' + (startDate.getMonth() + 1).toString() : (startDate.getMonth() + 1).toString();
                var date = startDate.getDate() < 10 ? '0' + startDate.getDate() : startDate.getDate();
                var day = startDate.getDay();
                var hour = startDate.getHours() < 10 ? '0' + startDate.getHours() : startDate.getHours();
                var min = startDate.getMinutes() < 10 ? '0' + startDate.getMinutes() : startDate.getMinutes();


                var parseStartDate = year + "-" + month + "-" + date
                $(".schedule--date.start time").text(parseStartDate + " (" + week[day] + ") ");

                $(".schedule--date.start span").text(hour + ":" + Math.floor(min / 10) + "0");
                /*status에 시작날짜 추가*/
                scheduleState.startDate = parseStartDate + " " + hour + ":" + Math.floor(min / 10) + "0"

            })(startDate);

            (function (endDate) {
                var year = endDate.getFullYear().toString();
                //var month = (endDate.getMonth() + 1).toString();
                var month = (endDate.getMonth() + 1) < 10 ? '0' + (endDate.getMonth() + 1).toString() : (endDate.getMonth() + 1).toString();
                
                var date = endDate.getDate() < 10 ? '0' + endDate.getDate() : endDate.getDate();
                var day = endDate.getDay();
                var hour = endDate.getHours() + 1 < 10 ? '0' + (endDate.getHours() + 1) : endDate.getHours() + 1;
                var min = endDate.getMinutes() < 10 ? '0' + endDate.getMinutes() : endDate.getMinutes();

                var parseEndDate = year + "-" + month + "-" + date;
                $(".schedule--date.end time").text(parseEndDate + " (" + week[day] + ") ");
                $(".schedule--date.end span").text(hour + ":" + Math.floor(min / 10) + "0");

                scheduleState.endDate = parseEndDate + " " + hour + ":" + Math.floor(min / 10) + "0";

            })(endDate);

        });

        /*일정목록 일정추가 클릭시*/


        /*일정 삭제 메서드*/
        scheduleDeleteBtn.on('click', function (e) {

            scheduleState.status = 'delete';

            if (targetScheduleIdx === null || targetScheduleIdx === undefined) {
                alert('삭제할 일정을 선택해주세요.');
                return;
            } else if ('delete' !== scheduleState.status) {
                alert('잘못된 접근 방식입니다.');
                return;
            }

            generateModal('delte-schedule-modal', '삭제', '육아 일정을 삭제하시겠습니까?', true, function () {

                $.ajax({
                    type: 'get',
                    url: '/dashboard/delete_schedule/' + targetScheduleIdx + '/' + scheduleState.selectedDate,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        var msg = data.result;
                        var scheduleData = data.schedule_list;	// handlebars data

                        if (msg === 'success') {
                            var selectedMonth = scheduleState['startDate'].substring(0, scheduleState['startDate'].lastIndexOf('-'));

                            $("#client-schedule-list").html('');
                            /*view 갱신*/
                            for (var i in scheduleData) {
                                if (scheduleData.hasOwnProperty(i)) {
                                    $("#client-schedule-list").append(renderScheduleList(scheduleData[i]));
                                    var timer = i * 250 <= 1000 ? i * 250 : 1000;
                                    $("#client-schedule-list").find('li').eq(i).stop().delay(timer).animate({
                                        opacity: 1
                                    }, 1200, 'easeOutCubic');
                                }
                            }

                            if (scheduleData.length > 0) {
                                schedulerCount.text('+' + scheduleData.length);
                                $(".dashboard-babybook--client--info--container__schedule-count span").text(scheduleData.length)
                            } else {
                                schedulerCount.removeClass('active').text('');
                                $(".dashboard-babybook--client--info--container__schedule-count").html('<div><h2>일정 수</h2><p>기록 없음</p></div>');
                            }

                            $("#client-schedule-list").find('li').eq(0).click();
                            /*스케줄 스테이터스 초기화*/
                            scheduleState = {
                                idx: '',
                                title: '',
                                eventType: '',
                                startDate: '',
                                endDate: '',
                                selectedDate: '', //yyyy-mm-dd
                                status: 'hold' //edit, write, delete, hold(default)
                            };

                            targetScheduleIdx = null;

                            /*더는 일정이 없을 때*/
                            if (scheduleData.length < 1) {
                                $("#schedule-list-viewbox").html('');
                                $("#client-schedule-list").append(noScheduleList());
                            }


                            /*ajax통신안에서 다시 한 번 ajax통신 달력 점찍기 통신*/
                            $.ajax({
                                url: "/dashboard/babybook/checkByMonth",
                                type: "POST",
                                async: false,
                                data: {
                                    date: selectedMonth	// yyyy-mm (이값 넣어주세요)
                                },
                                dataType: "json",
                                beforeSend: function (xhr) {
                                    sendCsrfToken(xhr);
                                    ajaxReady = false;
                                },
                                success: function (data) {
                                    /*서버에서 넘어온 값과, 현재 년도와 달을 보낸다 yyyy-mm*/
                                    updatePicker(data, selectedMonth);
                                },
                                complete: function () {

                                },
                                error: function (e) {
                                    generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                                }
                            });//inner ajax


                        } else if (msg === 'no_authority') {

                        } else if (msg === 'no_infant') {

                        } else {

                        }
                    },
                    complete: function () {
                        ajaxReady = true;
                    },
                    error: function (e) {
                        console.log(e.status);
                    }

                }); //end of ajax

            });


        });


        /*일정 편집하기 클릭시*/

        var typeBox = $("#schedule-type-container");

        scheduleEditBtn.on('click', function (e) {

            targetScheduleIdx = null;

            scheduleState.status = 'edit';

            if (isEmpty(scheduleState)) {
                alert('수정할 일정을 선택해주세요.');
                $("#dashboard-schedule-form").removeClass('active');
                return;
            } else if ('edit' !== scheduleState.status) {
                alert('잘못된 접근 방식입니다.');
                return;
            }

            $("#schedule-list-viewbox").html("");
            $("#dashboard-schedule-form").addClass('active');
            $(".__save-schedule-button").removeClass('active');
            $(".__modify-schedule-button").addClass('active');

            /*폼 채우기*/
            $("#dashboard-schedule-title").val(scheduleState.title);

            typeBox.find('a[data-event-type]').removeClass('active');
            typeBox.find('a[data-event-type=' + scheduleState.eventType + ']').addClass('active');

            var week = ['일', '월', '화', '수', '목', '금', '토'];


            var startDate = new Date(scheduleState.startDate);
            var endDate = new Date(scheduleState.endDate);

            (function (startDate) {
                var year = startDate.getFullYear().toString();
                var month = (startDate.getMonth() + 1).toString();
                var date = startDate.getDate().toString();
                var day = startDate.getDay();
                var hour = startDate.getHours() < 10 ? '0' + startDate.getHours() : startDate.getHours();
                var min = startDate.getMinutes() < 10 ? '0' + startDate.getMinutes() : startDate.getMinutes();


                var parseStartDate = year + "-" + month + "-" + date + " (" + week[day] + ") ";
                $(".schedule--date.start time").text(parseStartDate);
                $(".schedule--date.start span").text(hour + ":" + min);

            })(startDate);

            (function (endDate) {
                var year = endDate.getFullYear().toString();
                var month = (endDate.getMonth() + 1).toString();
                var date = endDate.getDate().toString();
                var day = endDate.getDay();
                var hour = endDate.getHours() < 10 ? '0' + endDate.getHours() : endDate.getHours();
                var min = endDate.getMinutes() < 10 ? '0' + endDate.getMinutes() : endDate.getMinutes();

                var parseEndDate = year + "-" + month + "-" + date + " (" + week[day] + ") ";
                $(".schedule--date.end time").text(parseEndDate);
                $(".schedule--date.end span").text(hour + ":" + min);


            })(endDate);

            console.log(scheduleState);

        });

        /*일정 편집하기 클릭시 끝*/


        /*일정 수정 ajax*/

        scheduleModifyBtn.on('click', function (e) {
            e.preventDefault();
            var isValid = 'edit' === scheduleState.status;


            if (isValid && ajaxReady) {
                scheduleState.title = $("#dashboard-schedule-title").val();
                scheduleState.eventType = $('input[name="event_type"]').val();

                var isValidIdx = !isEmpty(scheduleState.idx);
                var isValidTitle = !isEmpty(scheduleState.title);
                var isValidStartDate = !isEmpty(scheduleState.startDate);
                var isValidEndDate = !isEmpty(scheduleState.endDate);
                var isValidEventType = !isEmpty(scheduleState.eventType);

                if (isValidTitle && isValidStartDate && isValidEndDate && isValidEventType && isValidIdx) {

                    /*수정 폼데이터 생성*/

                    var formData = new FormData();

                    formData.append('schedule_idx', scheduleState.idx);
                    formData.append('update_start_date', scheduleState.startDate);
                    formData.append('update_end_date', scheduleState.endDate);
                    formData.append('update_title', scheduleState.title);
                    formData.append('selected_date', scheduleState.selectedDate);
                    formData.append('update_event_type', scheduleState.eventType);

                    console.log(scheduleState);

                    var selectedMonth = scheduleState['startDate'].substring(0, scheduleState['startDate'].lastIndexOf('-'));

                    $.ajax({
                        type: 'POST',
                        url: '/dashboard/modify_schedule/' + scheduleState.idx,
                        data: formData,
                        processData: false,
                        contentType: false,
                        beforeSend: function (xhr) {
                            sendCsrfToken(xhr);
                            ajaxReady = false;
                        },
                        success: function (data) {
                            var tags = [];
                            var msg = data.result;
                            var scheduleData = data.schedule_list;

                            if (msg === 'success') {

                                $("#client-schedule-list").html('');
                                /*view 갱신*/
                                for (var i in scheduleData) {
                                    if (scheduleData.hasOwnProperty(i)) {
                                        $("#client-schedule-list").append(renderScheduleList(scheduleData[i]));
                                        var timer = i * 200 <= 1000 ? i * 200 : 1000;
                                        $("#client-schedule-list").find('li').eq(i).delay(timer).animate({
                                            opacity: 1
                                        }, 1200, 'easeOutCubic');
                                    }
                                }

                                $("#client-schedule-list").find('li').eq(0).click();

                                console.log('변경값');
                                console.log(scheduleData.event_date_start);
                                var month = scheduleData.event_date_start.getMonth();
                                console.log(month);
                                //숫자 변경
                                schedulerCount.text('+' + scheduleData.length);
                                bottomScheduleCount.text(scheduleData.length);


                                /*스케줄 스테이터스 초기화*/
                                scheduleState = {
                                    idx: '',
                                    title: '',
                                    eventType: '',
                                    startDate: '',
                                    endDate: '',
                                    selectedDate: '', //yyyy-mm-dd
                                    status: 'hold' //edit, write, delete, hold(default)
                                };

                                /*폼 상태도 초기화*/
                                $("#dashboard-schedule-form")[0].reset();
                                $("#schedule-type-container").find('.__event-type').eq(0).addClass('active').siblings().removeClass('active');

                                /*ajax통신안에서 다시 한 번 ajax통신 달력 점찍기 통신*/
                                $.ajax({
                                    url: "/dashboard/babybook/checkByMonth",
                                    type: "POST",
                                    async: false,
                                    data: {
                                        date: selectedMonth	// yyyy-mm (이값 넣어주세요)
                                    },
                                    dataType: "json",
                                    beforeSend: function (xhr) {
                                        sendCsrfToken(xhr);
                                        ajaxReady = false;
                                    },
                                    success: function (data) {
                                        /*서버에서 넘어온 값과, 현재 년도와 달을 보낸다 yyyy-mm*/
                                        updatePicker(data, selectedMonth);
                                    },
                                    complete: function () {

                                    },
                                    error: function (e) {
                                        generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                                    }
                                });//inner ajax


                            } else if (msg === 'no_authority') {
                                alert("수정 권한이 없습니다.");
                            } else if (msg === 'failed') {
                                alert("수정에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                            } else if (msg === 'no_infant') {
                                alert("수정 권한이 없습니다.");
                            }
                        },
                        complete: function () {
                            ajaxReady = true;
                        },
                        error: function (e) {
                            console.log(e.status);
                        }
                    })

                } else if (!isValidTitle) {
                    generateModal('file-schedule-modal', 'Oops!', '제목을 입력해주세요.');
                } else if (!isValidStartDate) {
                    generateModal('file-schedule-modal', 'Oops!', '시작 날짜와 시간을 입력해주세요.');
                } else if (!isValidEndDate) {
                    generateModal('file-schedule-modal', 'Oops!', '종료 날짜와 시간을 입력해주세요.');
                } else if (!isValidIdx) {
                    generateModal('file-schedule-modal', 'Oops!', '비정상적인 접근방법입니다.');
                } else {
                    generateModal('file-schedule-modal', '에러', '서버에러 입니다.');
                }

            }


        });

        /*일정 수정 ajax 끝*/


        /*육아일정 저장하기 클릭 메서드*/
        scheduleSaveBtn.on("click", function (e) {
            e.preventDefault();
            /*스테이터스 상태 값 확인*/
            var isValid = scheduleState.status === 'write';


            if (ajaxReady === true && isValid) {

                scheduleState.title = $("#dashboard-schedule-title").val();
                scheduleState.eventType = $("input[name='event_type']").val();

                var selectedMonth = scheduleState['startDate'].substring(0, scheduleState['startDate'].lastIndexOf('-'));

                /*form 데이터 생성*/
                var formData = new FormData();
                formData.append('title', scheduleState.title);
                formData.append('start_date', scheduleState.startDate);
                formData.append('end_date', scheduleState.endDate);
                formData.append('event_type', scheduleState.eventType);

                $.ajax({
                    url: "/dashboard/add_schedule",
                    type: "POST",
                    data: formData,
                    processData: false,
                    contentType: false,
                    beforeSend: function (xhr) {
                        sendCsrfToken(xhr);
                        ajaxReady = false;
                    },
                    success: function (data) {
                        var msg = data.result;
                        var tags = [];
                        var scheduleData = data.schedule_list;


                        if (msg === 'success') {
                            // generateModal("schedule-success-modal", '작성완료!', '육아 일정작성이 완료되었습니다.', false);

                            $("#client-schedule-list").html('');
                            /*view 갱신*/
                            for (var i in scheduleData) {
                                if (scheduleData.hasOwnProperty(i)) {
                                    $("#client-schedule-list").append(renderScheduleList(scheduleData[i]));
                                    var timer = i * 200 <= 1000 ? i * 200 : 1000;
                                    $("#client-schedule-list").find('li').eq(i).delay(timer).animate({
                                        opacity: 1
                                    }, 1200, 'easeOutCubic');
                                }
                            }

                            /*0->1로 글 쓰는 거라면*/
                            if (scheduleData.length <= 1) {
                                schedulerCount.addClass('active').text('+1');
                                $(".dashboard-babybook--client--info--container__schedule-count").html('<div><h2>일정 수</h2><span title="1개의 육아일정이 작성되었습니다.">1</span><em>개</em></div>')
                            } else {
                                schedulerCount.addClass('active').text('+' + scheduleData.length);
                                $(".dashboard-babybook--client--info--container__schedule-count span").text(scheduleData.length);
                            }


                            $("#client-schedule-list").find('li').eq(0).click();

                            // 그룹에 알림전송
                            if (typeof $("input[name='family_group_idx']").val() != "undefined") {	// 그룹이 존재할시
                                var group_member_idx = $("input[name='family_group_idx']").val();
                                var group_idx_array = new Array(group_member_idx.length);
                                var requester_idx = $("input[name='session_idx']").val();

                                var i = 0;
                                for (i = 0; i < group_member_idx.length; i++) {
                                    group_idx_array[i] = $("input[name='family_group_idx']")[i].value;
                                    if ($("#session_idx").val() === group_idx_array[i]) {
                                        continue;
                                    }
                                    group_request_added(requester_idx, group_idx_array[i]);

                                }
                            }//end if

                            /*스케줄 스테이터스 초기화*/
                            scheduleState = {
                                idx: '',
                                title: '',
                                eventType: '',
                                startDate: '',
                                endDate: '',
                                selectedDate: '', //yyyy-mm-dd
                                status: 'hold' //edit, write, delete, hold(default)
                            };

                            /*폼 상태도 초기화*/
                            $("#dashboard-schedule-form")[0].reset();
                            $("#schedule-type-container").find('.__event-type').eq(0).addClass('active').siblings().removeClass('active');

                            /*ajax통신안에서 다시 한 번 ajax통신 달력 점찍기 통신*/
                            $.ajax({
                                url: "/dashboard/babybook/checkByMonth",
                                type: "POST",
                                async: false,
                                data: {
                                    date: selectedMonth	// yyyy-mm (이값 넣어주세요)
                                },
                                dataType: "json",
                                beforeSend: function (xhr) {
                                    sendCsrfToken(xhr);
                                    ajaxReady = false;
                                },
                                success: function (data) {
                                    /*서버에서 넘어온 값과, 현재 년도와 달을 보낸다 yyyy-mm*/
                                    updatePicker(data, selectedMonth);
                                },
                                complete: function () {

                                },
                                error: function (e) {
                                    generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                                }
                            });//inner ajax


                        } else if (msg === 'db_error') {
                            generateModal("schedule-fail-modal", '실패', '서버오류', false);
                        } else if (msg === 'no_infant') {
                            generateModal("schedule-fail-modal", '실패', '아이정보를 등록해주세요.', false);
                        } else {
                            generateModal("schedule-fail-modal", '실패', '서버오류, 잠시 후 다시 시도해주세요.', false);
                        }


                    },
                    error: function (e) {
                        generateModal("schedule-fail-modal", e.status, '서버오류, 잠시 후 다시 시도해주세요.', false);
                    },
                    complete: function () {
                        ajaxReady = true;
                    }
                });//ajax
            }

        });//end method click


        /*#육아일정 list 클릭시 리스트 일정 불러오기*/
        $(document).on('click', '#client-schedule-list>.schedule--list', function (e) {
            var scheduleIdx = $(this).attr('data-schedule-idx');
            $(this).addClass('active');
            $(this).siblings().removeClass('active');
            $.ajax({
                url: "/dashboard/get_schedule/" + scheduleIdx,
                type: "GET",
                data: '',
                dataType: "text",
                beforeSend: function (xhr) {
                    ajaxReady = false;
                    sendCsrfToken(xhr);
                },
                success: function (data) {
                    targetScheduleIdx = scheduleIdx;

                    var jsonData = JSON.parse(data);

                    var selectedDate = new Date(jsonData.event_date_start);
                    var year = selectedDate.getFullYear();
                    var month = selectedDate.getMonth() + 1 < 10 ? '0' + (selectedDate.getMonth() + 1) : (selectedDate.getMonth() + 1);
                    var date = selectedDate.getDate() < 10 ? '0' + selectedDate.getDate() : selectedDate.getDate();

                    scheduleState = {
                        idx: jsonData.idx,
                        title: jsonData.title,
                        eventType: jsonData.event_type,
                        startDate: jsonData.event_date_start,
                        endDate: jsonData.event_date_end,
                        selectedDate: year + "-" + month + "-" + date,
                        status: 'hold'
                    };

                    scheduleViewRender(scheduleState);

                },
                error: function (e) {
                    generateModal('ajax-error', e.status, 'ajax 서버 통신 오류', false);
                },
                complete: function () {
                    ajaxReady = true;
                }
            })

        });

        /*picker*/


        var pickerDate = new Date();
        pickerDate = formatDate(pickerDate);

        var pickerBox = document.getElementById('picker-container');


        $(startPicker).on('click', function () {
            // back.css('background', "url('/images/dashboard/modal/diary-edit-start-area.svg') no-repeat left /contain");
            var picker = new Picker(startPicker, {
                container: '#picker-container',
                format: 'YYYY-MM-DD HH:mm',
                date: pickerDate,
                increment: {
                    year: 1,
                    month: 1,
                    day: 1,
                    hour: 1,
                    minute: 10,
                    second: 10,
                    millisecond: 100,
                },
                rows: 5,
                translate: function (type, text) {
                    var suffixes = {
                        year: '년',
                        month: '월',
                        day: '일',
                        hour: '시',
                        minute: '분'
                    };

                    return Number(text) + suffixes[type];
                },
                shown: function (e) {
                    $(".picker").addClass('active');
                },
                hide: function (e) {
                    picker.reset();
                },
                pick: function (e) {

                    var time = picker.getDate(true).indexOf(' ');
                    var timelen = picker.getDate(true).length;
                    var targetd = picker.getDate(true).toString();

                    var setDate = targetd.slice(0, time);

                    /*status변경*/
                    scheduleState.startDate = picker.getDate(true);
                    scheduleState.selectedDate = setDate;

                    var date = new Date(setDate).getDay();
                    if (date === 0) {
                        setDate = setDate + ' (일)';
                    } else if (date === 1) {
                        setDate = setDate + ' (월)';

                    } else if (date === 2) {
                        setDate = setDate + ' (화)';

                    } else if (date === 3) {
                        setDate = setDate + ' (수)';

                    } else if (date === 4) {
                        setDate = setDate + ' (목)';

                    } else if (date === 5) {
                        setDate = setDate + ' (금)';

                    } else if (date === 6) {
                        setDate = setDate + ' (토)';

                    }
                    //
                    $(".schedule--date.start").find('time').text(setDate);
                    $(".schedule--date.start").find('span').text(targetd.slice(time, timelen));

                }
            });
            picker;


        });

        $(endPicker).on('click', function () {
            back.css('background', "url('/images/dashboard/modal/diary-edit-finish-area.svg') no-repeat right /contain");
            var picker = new Picker(endPicker, {
                container: '#picker-container',
                format: 'YYYY-MM-DD HH:mm',
                date: pickerDate,
                increment: {
                    year: 1,
                    month: 1,
                    day: 1,
                    hour: 1,
                    minute: 10,
                    second: 10,
                    millisecond: 100,
                },
                rows: 5,
                translate: function (type, text) {
                    var suffixes = {
                        year: '년',
                        month: '월',
                        day: '일',
                        hour: '시',
                        minute: '분'
                    };

                    return Number(text) + suffixes[type];
                },
                shown: function (e) {
                    console.log(e);
                    $(".picker").addClass('active');
                },
                hide: function (e) {
                    picker.reset();
                },
                pick: function (e) {

                    var time = picker.getDate(true).indexOf(' ');
                    var timelen = picker.getDate(true).length;
                    var targetd = picker.getDate(true).toString();

                    var setDate = targetd.slice(0, time);

                    /*status변경*/
                    scheduleState.endDate = picker.getDate(true);
                    ;

                    var date = new Date(setDate).getDay();
                    if (date === 0) {
                        setDate = setDate + ' (일)';
                    } else if (date === 1) {
                        setDate = setDate + ' (월)';

                    } else if (date === 2) {
                        setDate = setDate + ' (화)';

                    } else if (date === 3) {
                        setDate = setDate + ' (수)';

                    } else if (date === 4) {
                        setDate = setDate + ' (목)';

                    } else if (date === 5) {
                        setDate = setDate + ' (금)';

                    } else if (date === 6) {
                        setDate = setDate + ' (토)';

                    }
                    //
                    $(".schedule--date.end").find('time').text(setDate);
                    $(".schedule--date.end").find('span').text(targetd.slice(time, timelen));

                }
            });

            picker;

        });


    });//JQB
})();//IIFE


function scheduleViewRender(scheduleState) {

    /*이벤트 분기*/
    var eventType = scheduleState.eventType;
    var eventClass;

    if (eventType === '0') {
        eventType = '병원 일정';
        eventClass = 'hospital-icon';
    } else if (eventType === '1') {
        eventType = '생일 일정';
        eventClass = 'birthday-icon';
    } else if (eventType === '2') {
        eventType = '기타 일정';
        eventClass = 'etc-icon';
    }


    var date = scheduleState.startDate;


    date = new Date(date);
    var weekList = ['일', '월', '화', '수', '목', '금', '토'];

    /*시작*/
    var startYear = date.getFullYear() + '년 ';
    var startMonth = (date.getMonth() + 1) < 10 ? '0' + (date.getMonth() + 1) + '월 ' : (date.getMonth() + 1).toString() + '월 ';
    var startDate = date.getDate() < 10 ? '0' + date.getDate() : (date.getDate() + '일 ');
    var startDay = "(" + weekList[date.getDay()] + ")";
    var startHour = date.getHours() < 10 ? ('0' + date.getHours()) : date.getHours();
    var startMinute = date.getMinutes() < 10 ? ('0' + date.getMinutes()) : date.getMinutes();
    var startTime = startHour + ":" + startMinute;
    var startDateVal = startYear + startMonth + startDate + startDay;

    /*종료*/

    var endDate = scheduleState.endDate;

    endDate = new Date(endDate);

    var endHour = endDate.getHours() < 10 ? ('0' + endDate.getHours()) : endDate.getHours();
    var endMinute = endDate.getMinutes() < 10 ? ('0' + endDate.getMinutes()) : endDate.getMinutes();
    var endTime = endHour + ":" + endMinute;


    var tag = '<div class="schedule-view-container">\n' +
        '    <div class="schedule-view-container--inner">\n' +
        '        <div class="schedule-view-container--inner--title">\n' +
        '            <div class="' + eventClass + '"></div>\n' +
        '            <h2>' + (!isEmpty(scheduleState.title) ? scheduleState.title : '제목 없음') + '</h2>\n' +
        '        </div>\n' +
        '        <div class="schedule-view-container--inner--info">\n' +
        '            <div class="__date">' + startDateVal + '</div>' +
        '            <div class="__time">' + startTime + ' ~ ' + endTime + '</div>\n' +
        '            <div class="__event-type">' + eventType + '</div>\n' +
        '        </div>\n' +
        '    </div>\n' +
        '</div>'
    var preTag = $(".schedule-view-container") || null;
    if (preTag) {
        preTag.remove();
    } else {
    }
    $("#schedule-list-viewbox").append(tag);

    $(".dashboard-babybook--main-modal .dashboard-babybook--form #dashboard-schedule-form").removeClass('active');
}


function scheduleCalendar(scheduleState) {
    var week = ['일', '월', '화', '수', '목', '금', '토'];

    var startDate = new Date(scheduleState.startDate);
    var endDate = new Date(scheduleState.endDate);

    (function (startDate) {
        var year = startDate.getFullYear().toString();
        var month = (startDate.getMonth() + 1).toString();
        var date = startDate.getDate().toString();
        var day = startDate.getDay();
        var hour = startDate.getHours() < 10 ? '0' + startDate.getHours() : startDate.getHours();
        var min = startDate.getMinutes() < 10 ? '0' + startDate.getMinutes() : startDate.getMinutes();


        var parseStartDate = year + "-" + month + "-" + date + " (" + week[day] + ") ";
        $(".schedule--date.start time").text(parseStartDate);
        $(".schedule--date.start span").text(hour + ":" + min);

    })(startDate);

    (function (endDate) {
        var year = endDate.getFullYear().toString();
        var month = (endDate.getMonth() + 1).toString();
        var date = endDate.getDate().toString();
        var day = endDate.getDay();
        var hour = endDate.getHours() < 10 ? '0' + endDate.getHours() : endDate.getHours();
        var min = endDate.getMinutes() < 10 ? '0' + endDate.getMinutes() : endDate.getMinutes();

        var parseEndDate = year + "-" + month + "-" + date + " (" + week[day] + ") ";
        $(".schedule--date.end time").text(parseEndDate);
        $(".schedule--date.end span").text(hour + ":" + min);


    })(endDate);

}


function renderScheduleList(object) {

    /*이벤트 타입*/
    var objectType = object.event_type;
    if (objectType === '0') {
        objectType = 'reserved'
    } else if (objectType === '1') {
        objectType = 'anniversary';
    } else if (objectType === '2') {
        objectType = 'appointment';
    }

    console.log(object);
    var Date1 = new Date(object.event_date_start);
    var Date2 = new Date(object.event_date_end);

    /*시작 날짜 가공*/
    var startMonth = Date1.getMonth() + 1 < 10 ? '0' + (Date1.getMonth() + 1) : (Date1.getMonth() + 1).toString();
    var startDate = Date1.getDate() < 10 ? '0' + Date1.getDate() : Date1.getDate();
    var startHour = Date1.getHours() < 10 ? '0' + Date1.getHours() : Date1.getHours();
    var startMinute = Date1.getMinutes() < 10 ? '0' + Date1.getMinutes() : Date1.getMinutes();

    /*종료 날짜 가공*/
    var endMonth = Date2.getMonth() + 1 < 10 ? '0' + (Date2.getMonth() + 1) : (Date2.getMonth() + 1).toString();
    var endDate = Date2.getDate() < 10 ? '0' + Date2.getDate() : Date2.getDate();
    var endHour = Date2.getHours() < 10 ? '0' + Date2.getHours() : Date2.getHours();
    var endMinute = Date2.getMinutes() < 10 ? '0' + Date2.getMinutes() : Date2.getMinutes();


    var startFormat = startMonth + '월 ' + startDate + '일 ' + startHour + ":" + startMinute;
    var endFormat = endMonth + '월 ' + endDate + '일 ' + endHour + ":" + endMinute;

    var isSame = (startMonth + startDate) === (endMonth + endDate);

    var tag = '<li role="listitem" class="rendered schedule--list" data-schedule-idx="' + (object.idx) + '">\n' +
        '            <a href="javascript:void(0)" role="link" style="padding: 14.5px 0;">\n' +
        '                <div class="list--info">\n' +
        '                    <div class="list--info--time">\n' +
        '                        <time>' + (startHour + ":" + startMinute) + '</time>\n' +
        '                        <time>' + (endHour + ":" + endMinute) + '</time>\n' +
        '                    </div>\n' +
        '                    <div class="list--info--status">\n' +
        '                        <span class="' + (objectType) + '"></span>\n' +
        '                    </div>\n' +
        '                    <div class="list--info--desc">\n' +
        '                        <p>' + (object.title !== "" ? object.title : '(제목 없음)') + '</p>\n' +
        '                       ' + (isSame !== true ? '<span>' + (startFormat + '~' + endFormat) + '</span>' : '') + ' \n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </a>\n' +
        '        </li>';

    return tag;
}

function noScheduleList() {
    var tag = '<li role="listitem">\n' +
        '            <a href="javascript:void(0);" class="none-schedule">\n' +
        '                +&nbsp;&nbsp;일정을 추가하세요.\n' +
        '            </a>\n' +
        '        </li>';

    return tag;
}

function noBabybookList() {
    var tag = '<li>\n' +
        '            <a class="none-schedule" href="javascript:void(0);">\n' +
        '                +&nbsp;&nbsp;일기를 추가하세요.\n' +
        '            </a>\n' +
        '        </li>';

    return tag;
}