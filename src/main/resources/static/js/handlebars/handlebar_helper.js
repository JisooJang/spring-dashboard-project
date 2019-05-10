/*if~else 헬퍼
* {{#if 파리미터값}}
*
* {{else}}
*
* {{/if}}
* */
Handlebars.registerHelper('if', function (conditional, options) {
    if (conditional) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

/*댓글 likes_check , 1이면 내가 좋아요 한 게시글 0이면 안 한 것*/
Handlebars.registerHelper('comment_likes', function (conditional, options) {
    if (conditional === 1) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

/*group_check*/
Handlebars.registerHelper('group_check', function (conditional, options) {
    if (conditional === 0) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기(가족 그룹 수락 알림)
Handlebars.registerHelper('checkMessage0', function (conditional, options) {
    if (conditional === '0') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기(가족 그룹 신청 알림)
Handlebars.registerHelper('checkMessage1', function (conditional, options) {
    if (conditional === '1') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (작성글에 신규 댓글 알림)
Handlebars.registerHelper('checkMessage2', function (conditional, options) {
    if (conditional === '2') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (갤러리에 신규 댓글 알림)
Handlebars.registerHelper('checkMessage3', function (conditional, options) {
    if (conditional === '3') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (그룹간 육아일기에 신규 글 알림)
Handlebars.registerHelper('checkMessage4', function (conditional, options) {
    if (conditional === '4') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (그룹간 육아일정에 신규 글 올림)
Handlebars.registerHelper('checkMessage5', function (conditional, options) {
    if (conditional === '5') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (앱에서 피피 데이터 insert되어 배변활동 알림)
Handlebars.registerHelper('checkMessage6', function (conditional, options) {
    if (conditional === '6') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (앱에서 보틀 데이터 insert되어 수유활동 알림)
Handlebars.registerHelper('checkMessage7', function (conditional, options) {
    if (conditional === '7') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (앱에서 템프 저체온 알림)
Handlebars.registerHelper('checkMessage8', function (conditional, options) {
    if (conditional === '8') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (앱에서 템프 고체온 알림)
Handlebars.registerHelper('checkMessage9', function (conditional, options) {
    if (conditional === '9') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (그룹 수락 셀프 알림)
Handlebars.registerHelper('checkMessageA', function (conditional, options) {
    if (conditional === 'a') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//event_type분기 (그룹 거절 셀프 알림)
Handlebars.registerHelper('checkMessageB', function (conditional, options) {
    if (conditional === 'b') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('nullCheck', function (conditional, options) {
    if (conditional != null && conditional.length > 0) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

/*length가 0, null인지 체크*/
Handlebars.registerHelper('isEmpty', function (conditional, options) {
    if (conditional === null || (conditional != null && conditional.length < 1) || conditional === undefined || conditional === '' || conditional === ' ') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

//세션값과 작성자 비교(수정, 삭제 가능)
Handlebars.registerHelper('checkWriter', function (conditional, options) {
    var session_idx = document.getElementById("session_idx").value;
    if (conditional == session_idx) {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('likesCheck', function (conditional, options) {
    if (conditional == 1) {
        return options.fn(this);
    } else if (conditional == 0) {
        return options.inverse(this);
    } else {
        return options.inverse(this);
    }
});

Handlebars.registerHelper('listSizeCheck', function (conditional, options) {
    if (conditional == 'f') {
        return options.fn(this);
    } else {
        return options.inverse(this);
    }
});



