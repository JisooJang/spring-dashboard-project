(function () {

    $(function () {

        var limitSize = 3000000;

        /*이미지 변경*/
        $("#upload_image").on('change', function (e) {
            var files = e.target.files[0];
            /*사이즈 허용 여부를true or false로 반환 */

            console.log(e.target.files[0]);
            if(!files){
                generateModal('사진을 선택해주세요.');
                return
            }
            var isSizeOk = files.size <= limitSize;
            if (/\.(jpe?g|png)$/i.test(files.name) && isSizeOk) {
                    var fileReader = new FileReader();
                    fileReader.readAsDataURL(files);
                    fileReader.onload = function (e) {
                        $(".thumb_img").attr('src', e.target.result);
                    }
            } else if (/\.exe$/i.test(files.name)) {
                generateModal('EXE파일은 업로드가 불가합니다. JPG, PNG, JPEG만 올려주세요.');
            } else if(!isSizeOk){
                generateModal('3MB이하의 용량만 가능합니다.');
            }
            else {
                generateModal('적합한 파일명을 올려주세요');
            }
        });


    });//JQB

})();//IIFE