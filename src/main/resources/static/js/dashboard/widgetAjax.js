/*ajax 통신은 여기서 작성해서 dashboard.js에서 쓰기*/


/*위젯 위치 통신하는 함수*/

// 사용자에게 저장된 위젯 위치 가져오기
function getWidgetPosition() {
    var session_idx = $("#session_idx").val();
    var position = null;

    $.ajax({
        method: 'GET',
        url: "/dashboard/getDashboardWidget/" + session_idx,
        dataType: 'JSON',
        async: false,
        beforeSend: function (xhr) {
            sendCsrfToken(xhr);
        },
        success: function (data) {
            var target = data;
            console.log('서버에서 가져온 위치 ㄱ밧입니다.');
            console.log(data);

            if (target !== null) {

                target.forEach(function (ele, idx) {

                    if (ele == null || ele == undefined || ele == '' || ele == ' ' || ele == false) {
                        console.log('서버에서 가져온 값에 null값이 있어서, 위젯의 위치를 기본셋팅값으로 변경합니다.');
                        /*기본값 좌표*/
                        target = [{"attr":"3","x":0},{"attr":"4","x":0.24633431085043989},{"attr":"6","x":0.49266862170087977},{"attr":"5","x":0.7390029325513197},{"attr":"2","x":0},{"attr":"1","x":0.24633431085043989}];
                    }
                });

                // 위젯위치 값이 데이터에 저장되어 있는 경우
                position = JSON.stringify(target);
            } else {
                /*위젯 위치 값이 없을 경우*/
                position = null;
            }
            //없는 경우 기본 위젯 위치
        }
    });//AJAX

    return position;
}

// 위젯 위치 옮기는 이벤트가 감지되었을때 실행되며, 옮긴 위치의 좌표값을 db에 업데이트.
function setWidgetPosition(positions) {
    /*위젯 '저장되었습니다'메세지 태그*/
    $.ajax({
        method: 'POST',
        contentType: "application/json",
        url: "/dashboard/saveDashboardWidget",
        data: JSON.stringify(positions),
        beforeSend: function (xhr) {
            sendCsrfToken(xhr);
        },
        success: function (data) {
            console.log('위치 저장 성공 ');
            console.log(JSON.stringify(positions));
            console.log(data);
            if (data !== "success") {
                // 위젯 위치 변경 이력이 있는 경우
                alert("위젯 위치 저장에 실패하였습니다. 잠시 후에 다시 시도해주세요.");
            } else {

            }


        },
        complete: function () {

        }

    });//AJAX
}

/*아기 선택시 아이 변경 ajax통신*/
function getBabyAjax() {
    $.ajax({
        method: '',
        url: "",
        data: '',
        dataType: '',
        beforeSend: function (xhr) {
            sendCsrfToken(xhr);
        },
        success: function () {

        },
        complete: function () {

        }

    });//AJAX
}