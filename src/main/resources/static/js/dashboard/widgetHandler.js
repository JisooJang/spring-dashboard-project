(function () {
    $(function () {
        /*위젯의 위치 좌표를 저장하는 함수*/
        Packery.prototype.getShiftPositions = function (attrName) {
            attrName = attrName || 'id';
            var _this = this;
            return this.items.map(function (item) {
                return {
                    attr: item.element.getAttribute(attrName),
                    x: item.rect.x / _this.packer.width
                }
            });
        }; //getShiftPositions

        /*받은 positions값을 json parse하는 메서드*/
        Packery.prototype.initShiftLayout = function (positions, attr) {
            if (!positions) {
                this.layout();
                return;
            }

            // 스트링을 JSON으로 파싱
            if (typeof positions === 'string') {
                try {
                    positions = JSON.parse(positions);
                } catch (error) {
                    console.error('제이슨 파싱 에러: ' + error);
                    this.layout();
                    return;
                }
            } //end if

            attr = attr || 'id';
            this._resetLayout();

            /*가로위치와 순서 셋팅 저장된 positions에서*/
            this.items = positions.map(function (itemPosition) {
                var selector = '[' + attr + '="' + itemPosition.attr + '"]';
                var itemElem = this.element.querySelector(selector);
                var item = this.getItem(itemElem);
                item.rect.x = itemPosition.x * this.packer.width;
                return item;
            }, this);
            this.shiftLayout();

        }; //initShiftLayout

        /*팩커리 드래거블 해제*/
        Packery.prototype.unbindDraggabillyEvents = function (draggie) {
            draggie.off('dragStart', this.handleDraggabilly.dragStart);
            draggie.off('dragMove', this.handleDraggabilly.dragMove);
            draggie.off('dragEnd', this.handleDraggabilly.dragEnd);
        };

        /****************팩커리 설정************/

        var docsWidth = getCurrentWidth();
        var grid = $(".dashboard");
        var items = null;

        /*브라우저 화면 조절시 팩커리 설정*/
        $(window).on('resize', function (e) {
            docsWidth = getCurrentWidth();
            var isMobile = docsWidth <= 768;
            if (isMobile) {

            } else {

            }
        });

        /*모바일 화면에서 드래거블 해제*/
        if (docsWidth <= 640) {

            grid.packery({
                itemSelector: '.dashboard--list',
                gutter: 24,
                percentPosition: true,
                initLayout: true,
                columnWidth: 318,
            });


        } else if (docsWidth >= 2048) {
            /*레티나 해상도*/
            setTimeout(function () {

                grid.packery({
                    itemSelector: '.dashboard--list',
                    gutter: 32,
                    percentPosition: false,
                    initLayout: true,
                    columnWidth: 424,
                    transitionDuration: '0.4s'
                });
                items = grid.find('.dashboard--list').draggable();

                /*드래그 기능 할당*/
                items = grid.find('.dashboard--list').draggable();
                grid.packery('bindUIDraggableEvents', items);

                grid.on('dragItemPositioned', function (e) {
                    // (positions에 위젯 x 좌표 저장)
                    var positions = grid.packery('getShiftPositions', 'data-widget-id');

                    grid.packery('fit', e.currentTarget);

                    try {
                        /*로컬 스토리지에 저장(5mb한계)*/
                        localStorage.setItem('dragPositions', JSON.stringify(positions));

                        /*데이터 베이스 저장*/
                        if (docsWidth <= 640) {

                        } else {
                            setWidgetPosition(positions);
                        }
                    } catch (error) {
                        throw error;
                    }
                });//위치 조정 되었을 때

                var initPositions;

                var initPostionAjax = getWidgetPosition();

                /*데이터베이스에 저장된 위치 값이 없을 경우*/
                if (initPostionAjax === null) {
                    /*로컬 스트리지에 저장*/
                    initPositions = localStorage.getItem('dragPositions');
                    // /*localStorage의 저장된 값으로 위젯 위치 이동.*/
                    grid.packery('initShiftLayout', initPositions, 'data-widget-id');
                } else {
                    /*데이터베이스에 저장된 위치가 있을 경우에는 db의 값을 가져온다.*/
                    grid.packery('initShiftLayout', initPostionAjax, 'data-widget-id');
                }//end if
            }, 700)

        } else if (docsWidth <= 2047) {



            setTimeout(function () {
                grid.packery({
                    itemSelector: '.dashboard--list',
                    gutter: 24,
                    percentPosition: false,
                    initLayout: true,
                    columnWidth: 318,
                    transitionDuration: '0.4s'
                });
                items = grid.find('.dashboard--list').draggable();
                /*드래그 기능 할당*/
                items = grid.find('.dashboard--list').draggable();
                grid.packery('bindUIDraggableEvents', items);

                grid.on('dragItemPositioned', function (e) {
                    // (positions에 위젯 x 좌표 저장)
                    var positions = grid.packery('getShiftPositions', 'data-widget-id');

                    grid.packery('fit', e.currentTarget);

                    try {
                        /*로컬 스토리지에 저장(5mb한계)*/
                        localStorage.setItem('dragPositions', JSON.stringify(positions));

                        /*데이터 베이스 저장*/
                        if (docsWidth <= 640) {

                        } else {
                            setWidgetPosition(positions);
                        }
                    } catch (error) {
                        throw error;
                    }
                });//위치 조정 되었을 때

                var initPositions;

                var initPostionAjax = getWidgetPosition();

                /*데이터베이스에 저장된 위치 값이 없을 경우*/
                if (initPostionAjax === null) {
                    /*로컬 스트리지에 저장*/
                    initPositions = localStorage.getItem('dragPositions');
                    // /*localStorage의 저장된 값으로 위젯 위치 이동.*/
                    grid.packery('initShiftLayout', initPositions, 'data-widget-id');
                } else {
                    /*데이터베이스에 저장된 위치가 있을 경우에는 db의 값을 가져온다.*/
                    grid.packery('initShiftLayout', initPostionAjax, 'data-widget-id');
                }//end if
            }, 700);


        }
    });//jqb
})();//iife