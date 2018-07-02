/*
 * jQuery slide Plugin 1.5.1 http://blog.geekli.cn/ Copyright 2011, ksc last
 * update: 2014.04.28
 */
jQuery.extend({
			st : function(s1, s2, op) {
				var sl = {};// 暴漏给外面的对象
				var set = {
					current : 0,// 当前显示元素的索引,取值范围0-(len-1)
					enumber : 0,// 元素个数
					time : 2.5,// 切换时间间隔
					on : 'on',// 控制层 css类名
					auto : 1,// 是否自动切换
					mode : 'normal',// 切换模式 normal:普通;fade:淡入淡出;slide:滑动;
					switchMode : 'mouseover',// 鼠标切换方式，mouseover鼠标经过时切换，click单击切换
					Cmouseover : 'none'// 当鼠标经过内容区{s2}时，none:无动作，wait：等待

				};

				// 切换模式 normal:普通;fade:淡入淡出;slide:滑动;
				sl = $.extend({
							'last' : 0
						}, set, op);

				var $e1 = $(s1);// 控制端
				var $e2 = $(s2);// 内容 在slide模式下 $e2为“内容容器层”

				var len = (sl.mode == 'slide')
						? $e2.find('li').length
						: $e2.length;// 元素个数
				var height = $e2.find('li').height();// 展现元素的高度，在切换模式为slide时要用到
				var timer;

				// 自动切换
				var start = function() {
					timer = setInterval(function() {
								toggle();
							}, sl.time * 1000);
				};
				// 停止自动切换
				var clear = function() {
					clearInterval(timer);
				};
				// 切换到指定位置，可以是 索引、'next'、'prev'
				var toggle = function(to) {
					sl.last = sl.current;
					if (to == null || to == 'next') {
						sl.current++;
					} else if (to == 'prev') {
						sl.current--;
					} else {// 如果to不为空，则切换到指定位置
						sl.current = to;
					}

					if (sl.current >= len) {// 如果current越界了，则重置为0
						sl.current = 0;
					}
					if (sl.current < 0) {
						sl.current = len - 1;
					}
					if (sl.last == sl.current) {
						return;
					}
					// console.log(sl.current);
					$e1.removeClass(sl.on);
					$e1.eq(sl.current).addClass(sl.on);
					switch (sl.mode) {
						case 'fade' :
							$e2.hide().stop(false, true);
							$e2.eq(sl.current).css({
										"z-index" : "4"
									});
							$e2.eq(sl.last).css({
										"z-index" : "2"
									});
							$e2.eq(sl.current).fadeIn(1500);
							$e2.eq(sl.last).show().fadeOut(1500);
							break;
						case 'slide' :
							$e2.stop();
							// 在图片未加载之前，chrome浏览器下图片的高宽为0，若img外部的li有又没有设置高度的话，此时li的高也是0。
							height = !height ? $e2.find('li').height() : height;// 若给img外面的li设上了高，则可以删去此行
							$e2.animate({
										top : -sl.current * (height)
									}, {
										'duration' : "slow"
									});
							break;
						case 'slide2' :
							// $e2.stop();
							var z_index = 1;
							height = !height ? $e2.height() : height;// 若给img外面的li设上了高，则可以删去此行
							$e2.css({
										'z-index' : z_index
									});
							$e2.eq(sl.last).css({
										'z-index' : z_index + 1
									});
							$e2.eq(sl.current).css({
										'top' : -height,
										'z-index' : z_index + 2
									}).animate({
										top : 0
									}, {
										'duration' : "slow"
									});
							break;
						default :
							$e2.hide();
							$e2.eq(sl.current).show();
					}
				};

				// 设置控制层切换控制方式
				$e1.bind(sl.switchMode, function() {
							clear();
							var to = $(this).index();
							toggle(to);
						});
				if (sl.auto) {// 如果自动切换
					$e1.mouseout(function() {
								clear();
								start()
							});
					start();
				}
				if (sl.Cmouseover == 'wait') {
					$e2.bind({
								'mouseover' : function() {
									clear();
								},
								'mouseout' : function() {
									clear();
									start();
								}
							});
				}

				sobj = {
					'slide_to' : function(to) {
						clear();
						toggle(to);
						if (sl.auto) {
							start();
						}
					},
					'stop' : function() {// 停止轮播
						clear();
						sl.auto = 0;
					},
					'start' : function() {
						clear();
						sl.auto = 1;
						start();
					}
				}
				return sobj;
			}// function end

		});
