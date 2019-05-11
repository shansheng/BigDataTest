            var ap_name = navigator.appName;    
            var ap_vinfo = navigator.appVersion;    
            var ap_ver = parseFloat(ap_vinfo.substring(0,ap_vinfo.indexOf('(')));// 获取版本号  
            var time_start = new Date();    
            var clock_start = time_start.getTime();    
            var dl_ok=false;
            var is_confirm = true;
            
            $(window).bind('mouseover mouseleave', function(event){
                is_confirm = event.type == 'mouseleave';
            });
            
            function init (){    
            if(ap_name=="netscape" && ap_ver>=3.0)    
                dl_ok=true;     
                return true;    
            }    
            //已经打开页面多少秒  
            function get_time_spent (){     
                var time_now = new Date();    
                return((time_now.getTime() - clock_start)/1000);     
            }    
            function show_secs (){  // show the time user spent on the side             
                var i_total_secs = Math.round(get_time_spent());     
                var i_secs_spent = i_total_secs % 60;    
                var i_mins_spent = Math.round((i_total_secs-30)/60);//四舍五入，超60s，大于0.5，四舍五入就是1min     
                var s_secs_spent = "" + ((i_secs_spent>9) ? i_secs_spent : "0" + i_secs_spent);//改显示格式：个位数-> 0+个位数，如7->07  
                var s_mins_spent ="" + ((i_mins_spent>9) ? i_mins_spent : "0" + i_mins_spent);    
                document.fm0.time_spent.value = s_mins_spent + ":" + s_secs_spent;  //把值放入form中name为time_spent的input中  
                window.setTimeout('show_secs()',1000);   //每隔1s刷新一次input里的值  
            }    
            //参考：http://blog.csdn.net/davislien/article/details/47685831  
  
            //----------------------beforeunload使用方法一：（需要去掉body标签里的beforeunload="myFunction()"）------------------------------------  
            //在关闭页面时弹出确认提示窗口  
			//$(window).bind('beforeunload', function(){      
			//  //alert(09999);//凡是alert在此函数里被阻止，Blocked alert('09999') during beforeunload.  
			//  s1(2000);  
			//    
			//  console.log("您在网站"+ document.URL+"停留时间（分：秒）："+document.fm0.time_spent.value);  
			//  
			//     window.event.returnValue="1.确定要退出本页吗？";    
			//     
			//});  
      
		    //----------------------beforeunload使用方法二：（不去掉body标签里的beforeunload="myFunction()"也可以，还是执行此方法二，而不是方法三）------------------------------------  
		    //测试：谷歌浏览器：刷新时只执行最后一行代码，关闭页面时三行代码都执行  
            window.onbeforeunload = function(event){      
            	//alert(09999);//凡是alert在此函数里被阻止，Blocked alert('09999') during beforeunload.  
            	//s1(2000);  
            	//var arr = [0, 1, 2, 3];  
            	//localStorage.setItem("num", arr);//存储数据  
            	//console.log(localStorage.getItem("num")); 
            	var url = document.URL;
            	var time = Math.round(get_time_spent());
            	//var time = document.fm0.time_spent.value;
            	//console.log("您在网站"+ document.URL+"停留时间（分：秒）："+document.fm0.time_spent.value); 
            	saveST(url,time);
            	//if(window.is_confirm != false){  
            	//	window.event.returnValue="2.确定要退出本页吗？";  //刷新页面时只执行此行   
            	//}  
            };  
  
		    //----------------------beforeunload使用方法三：（需要在body标签里的加上beforeunload="myFunction()"）------------------------------------  
		    //测试：谷歌浏览器：刷新时只执行最后一行代码，关闭页面时三行代码都执行  
		        	//function myFunction(){      
            		//alert(09999);//凡是alert在此函数里被阻止，Blocked alert('09999') during beforeunload.  
            		//s1(2000);  
            		//console.log("您在网站"+ document.URL+"停留时间（分：秒）："+document.fm0.time_spent.value);  
            		//window.event.returnValue="3.确定要退出本页吗？";  //刷新页面时只执行此行  
		         //};    
  
  
			//js中的暂停方法一  
			    function sleep(numberMillis) {   
					var now = new Date();   
					var exitTime = now.getTime() + numberMillis;   
					while (true) {   
					now = new Date();   
					if (now.getTime() > exitTime)   
					return;   
					}   
			    }  

			    function s(){  
			    	sleep(2000);  
			    	alert("您在网站"+ document.URL+"停留时间（分：秒）："+document.fm0.time_spent.value);  
			    }  

			    //js中的暂停方法二  
				function s1(sec){  
					setTimeout(function(){  
						alert("您在网站"+ document.URL+"停留时间（分：秒）："+document.fm0.time_spent.value); 
						},sec);  
				}  
