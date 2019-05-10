(function(){
    $(function(){
    	var infant_idx = $("#infant_idx").val();
    	var infant = {
    			name: $("#infant_name").text(),
    			birth: $("#infant_birth").text(),
    			gender: $("#infant_gender").text(),
    			weight: $("#infant_weight").text(),
    			height: $("#infant_height").text(),
    			blood_type: $("#infant_blood").text()
    	};
    	
        var $grid = $('.grid').packery({
            itemSelector: '.grid-item',
            gutter:20
        });

        var draggies = [];

        /*모바일에서 드래거블 막기*/
        var isMobile = $('body').width() <= 768;

        if(isMobile){
            draggies.forEach( function( draggie ) {
                draggie['disable']();
            });
        } else{
            $grid.find('.grid-item').each( function( i, gridItem ) {
                var draggie = new Draggabilly( gridItem );
                draggies.push( draggie );
                // bind drag events to Packery
                $grid.packery( 'bindDraggabillyEvents', draggie );
            });
        }

        /*브라우저 화면 조절시 팩커리 설정*/
        $(window).on('resize', function(e){
            var isMobile = $('body').width() <= 768;
            if(isMobile){
                draggies.forEach( function( draggie ) {
                    draggie['disable']();
                });
            } else{
                draggies.forEach( function( draggie ) {
                    draggie['enable']();
                });
            }
        });



        /*아기 썸네일 클릭시*/
        $(".dashboard-baby").on('click',function(e){
            $(this).find('.dashboard-baby--thumb>img').toggleClass('active');
        });

        var babyMenuSwitch = 0;

        /*아기 정보 설정 버튼 클릭시*/
        $(".dashboard-baby__settings").on('click',function(e){
            var settings = $("     <div class='__settingBox'>\n" +
                "                            <ul>\n" +
                "                                <a href='#' class='modify_baby_info'><li><div>정보수정</div><span>아이의 정보를 수정하실 수 있습니다.</span></li></a>\n" +
                "                                <a href='#' class='delete_baby_info'><li><div>삭제</div><span>아이의 정보를 삭제합니다.</span></li></a>\n" +
                "                            </ul>\n" +
                "                        </div>");
            if(babyMenuSwitch !== 1){
                $(e.target).append(settings);
            }
            babyMenuSwitch=1;
        });
        
        /*수정버튼 클릭 후 나오는 메뉴 창에서 벗어 날 경우*/
        $(document).on("mouseleave",".__settingBox",function(e){
            $(this).remove();
            babyMenuSwitch=0;

        });

        /*정보설정버튼에서 뜨는 메뉴의 정보수정 메뉴를 클릭했을 경우*/
        $(document).on("click",".modify_baby_info",function(e){
            e.preventDefault();
            modifyBabyInfoModalView();
            $("input[name='name']").val(infant.name);
            $("input[name='birth']").val(infant.birth.split("생")[0]);
            $("input[name='weight']").val(infant.weight.split("kg")[0]);
            $("input[name='height']").val(infant.height.split("cm")[0]);
            
            if(infant.gender == '남자아이') {
            	$("input[id='baby_boy']").attr("checked", true);
            } else if(infant.gender == '여자아이') {
            	$("input[id='baby_girl']").attr("checked", true);
            }
            
            if(infant.blood_type.split("형")[0] == 'a') {
            	$("input[id='type_a']").attr("checked", true);
            } else if(infant.blood_type.split("형")[0] == 'b') {
            	$("input[id='type_b']").attr("checked", true);
            } else if(infant.blood_type.split("형")[0] == 'o') {
            	$("input[id='type_o']").attr("checked", true);
            } else if(infant.blood_type.split("형")[0] == 'ab') {
            	$("input[id='type_ab']").attr("checked", true);
            }
            /*$.ajax({
            	url: "/dashboard/infant_info/" + infant_idx,
            	type: "GET",
            	dataType: "json",
            	success: function(data) {
            		//data의 필드값들을 모달창의 value값들로 채움
            		
            	}, error: function(e) {
            		showPopup(e.status+"에러 /아기 정보수정");
            	}
            });*/
            
        });
        
        /* 아기정보 삭제버튼 클릭시*/
        $(document).on("click", ".delete_baby_info", function(e) {
        	e.preventDefault();
        	var result = confirm("정말 삭제하시겠습니까? 아기 정보를 삭제하시면 연관된 생체 데이터도 전부 삭제됩니다.");
        	if(result) {
        		$(location).attr("href", "/dashboard/delete_infant/" + infant_idx);
        	}
        });

        /*정보설정버튼에서 뜨는 메뉴의 정보 삭제 메뉴를 클릭했을 경우*/
        $(document).on("click",".delete_baby_info",function(e){
            e.preventDefault();
            showPopup('현재 기능 구현 중입니다.');
        });
        
        /* 아기정보 수정 모달창에서 수정 submit 버튼 클릭시 */
        $(document).on("click", "#btn_modify_info", function(e) {
        	e.preventDefault();
        	var newWeight = $("#baby_weight").val();
        	var newHeight = $("#baby_height").val();
        	$.ajax({
        		url: "dashboard/modify_infant_info/" + infant_idx,
        		type: "POST",
        		data: {
        			weight: newWeight,
        			height: newHeight
        		},
        		dataType: "text",
        		beforeSend: function(xhr) {
        			sendCsrfToken(xhr);
        		},
        		success: function(data) {
        			if(data == 0) {
        				alert("수정이 완료되었습니다.");
        				// 모달창 닫기
        				// 대쉬보드 아기정보창에서 변경된 내용으로 수정
        				$("#infant_height").text(newHeight + "cm");
        				$("#infant_weight").text(newWeight + "kg");
        			}
        		},
        		error: function() {
        			alert("아기정보 수정 ajax 에러");
        		}
        	});
        });

        /*아기 추가 입력정보*/
        var babyBirthdateOk = 0;
        var babyNameOk = 0;
        var babyWeightOk = 0;
        var babyHeightOk = 0;
        /*아기 추가 버튼 클릭시*/
        $(".btn_add_baby").on("click touchstart",function(){
            openModalView();
        });

        //아기 생년월일 정보 검사
        $(document).on("keyup","#baby_birthdate",function(){
            if(checkNumberValidation("#baby_birthdate")){
                babyBirthdateOk = 1;
            } else{
                babyBirthdateOk = 0;
            }
        });

        /*아기정보 입력 validation 검사*/
        $(document).on("blur","#baby_name",function(){
            if(validationTextOnly("#baby_name")){
                babyNameOk = 1;
            } else{
                babyNameOk = 0;
            }
        });

        $(document).on('keyup','#baby_weight',function(){
            if(checkNumberValidation("#baby_weight")){
                babyWeightOk = 1;
            } else{
                babyWeightOk = 0;
            }
        });
        $(document).on('keyup','#baby_height',function(){
            if(checkNumberValidation("#baby_height")){
                babyHeightOk = 1;
            } else{
                babyHeightOk = 0;
            }
        });

        //아기 정보 입력 버튼 클릭시 이벤트
        $(document).on('click',"#btn_info_baby",function(e){
            e.preventDefault();
            if(babyHeightOk===1 && babyWeightOk ===1 && babyNameOk ===1 && babyBirthdateOk===1){
                var params = $("#infant_form").serialize();
                $.ajax({
                    type: "POST",
                    url: "/dashboard/register_infant",
                    data: params,
                    dataType: "text",
                    beforeSend: function(xhr) {
            			sendCsrfToken(xhr);
            		},
                    success: function(data) {
                        if(data === 'success') {
                            alert("아기 정보 등록을 성공적으로 완료하였습니다.");
                            location.reload();
                        } else if(data == 'file_size_error') {
                        	alert("크기가 2MB 미만의 파일만 첨부 가능합니다.");
                        } else {
                            alert("정보등록에 실패하였습니다. 잠시 후 다시 시도해주세요.");
                        }
                    },
                    error: function() {
                        alert("ajax 통신 에러");
                    }
                });

                try{
                    submitBabyInfo(".section_modal");

                } catch(err){
                    throw err;
                } finally{
                    setTimeout(function(){
                        $(".section_modal").remove();
                        $(".complete_process").remove();
                        $(".screen_blocking").remove();
                    },4000);

                }//finally
            }//end if

            if(babyHeightOk===0){
                alert('아기의 키를 입력해주세요.');
            }
            if(babyWeightOk===0){
                alert('아기의 몸무게를 입력해주세요.');
            }
            if(babyNameOk===0){
                alert('아기의 이름을 입력해주세요.');
            }
            if(babyBirthdateOk===0){
                alert('아기의 생년월일을 입력해주세요.');
            }

        });
        
        /*기기연동 아이콘 클릭했을 경우 메뉴 보여주기*/
        var linkSwitch = 0;
        $(".btn_device_links").on("click",function(){
            if(linkSwitch === 0) {
                showDeviceLinks(0,100);
            } else {
                hideDeviceLinks(0,100);
            }
        });

        function showDeviceLinks(index,interval){
            var targetLen = $(".list-devices>div").length;
            if(index < targetLen){
                if($(".list-devices>div").eq(index).is(":animated")){
                    return;
                }
                $(".list-devices>div").eq(index++).stop().animate({right:'0'},500);
                setTimeout(function(){
                   showDeviceLinks(index,interval);
                },interval*index);
            }
            linkSwitch=1;
        }

        function hideDeviceLinks(index, interval) {
            var targetLen = $(".list-devices>div").length;
            if (index < targetLen) {
                if($(".list-devices>div").eq(index).is(":animated")){
                    return;
                }
                $(".list-devices>div").eq(index++).stop().animate({right: '-300px'}, 500);
                setTimeout(function () {
                    hideDeviceLinks(index, interval);
                }, interval*index);
            }
            linkSwitch=0;

        }

    });//JQB
})();