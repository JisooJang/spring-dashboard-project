var googleUser = {};
var startApp = function() {
	gapi.load('auth2', function(){
		// Retrieve the singleton for the GoogleAuth library and set up the client.
		auth2 = gapi.auth2.init({
			client_id: '...',
			cookiepolicy: 'single_host_origin',
			// Request scopes in addition to 'profile' and 'email'
			//scope: 'additional_scope'
		});
		
		if(document.getElementById('bg_google') != null) {	
			attachSignin(document.getElementById('bg_google'));	
		} else if(document.getElementById('bg_google_invite') != null) {	
			attachSignin2(document.getElementById('bg_google_invite'));
		}

	});
};

/* 일반 가입 */
function attachSignin(element) {
	auth2.attachClickHandler(element, {},
			function(googleUser) {
		var id_token = googleUser.getAuthResponse().id_token;
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var xhr = new XMLHttpRequest();
		xhr.open('POST', '/googletokensignin');
		xhr.setRequestHeader(header, token);
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
		xhr.send('idtoken=' + id_token);
		xhr.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200)
			{	var response = xhr.responseText;
				if(response == 'social_join') {
					location.href = "./auth/google_login";
				} else if(response == 'social_login') {
					location.href = "./";
				} else if(response == 'email_dup') {
					alert("이미 일반 회원에 해당 구글 계정으로 가입된 이력이 있습니다. 일반 로그인을 시도해주세요.");
				} else {
					alert("잘못된 접근입니다.");
					var url = window.location.href.split('/login');
					location.href = "./";
				}
			}
		}
	}, function(error) {
		alert(JSON.stringify(error, undefined, 2));
	});

}


/* 가족 초대 가입 */
function attachSignin2(element) {
	auth2.attachClickHandler(element, {},
			function(googleUser) {
		var id_token = googleUser.getAuthResponse().id_token;
		var inviter_idx = $("#inviter_idx").val();
		
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		var xhr = new XMLHttpRequest();
		xhr.open('POST', '/googletokensignin/invite_family');
		xhr.setRequestHeader(header, token);
		xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
		xhr.send('idtoken=' + id_token + "&inviter_idx=" + inviter_idx);
		
		xhr.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				var response = xhr.responseText;
				if(response == 'social_join') {
					location.href = "/auth/google_login";
				} else if(response == 'social_login') {
					location.href = "/";
				} else if(response == 'email_dup') {
					alert("이미 일반 회원에 해당 구글 계정으로 가입된 이력이 있습니다. 일반 로그인을 시도해주세요.");
				} else { 
					alert("잘못된 접근입니다.");
					location.href = "/";
				}
			}
		}
	}, function(error) {
		alert(JSON.stringify(error, undefined, 2));
	});

}