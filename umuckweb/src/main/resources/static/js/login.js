
//一加载就执行填充信息  用于记住登录信息
$(document).ready(function(){
    if($.cookie("password") != ''){
        $("#password").val($.cookie("password"));
    }
    if($.cookie("account") != ''){
        $("#account").val($.cookie("account"));
    }


})


//不加这个复选框样式不会改变
layui.use('form', function(){
    var form = layui.form; //只有执行了这一步，部分表单元素才会自动修饰成功
});


//回车登录
function keyLogin(){
    if (event.keyCode==13){ //回车键的键值为13
        //document.getElementById("login").click(); //调用登录按钮的登录事件
        //或者使用jquery实现
        $("#login").click();
    }
}

//用于验证码
var code;//声明一个变量用于存储生成的验证码
document.getElementById("code").onclick=changeImg;
function changeImg(){
    //alert("换图片");
    var arrays=new Array(
        '1','2','3','4','5','6','7','8','9','0',
        'a','b','c','d','e','f','g','h','i','j',
        'k','l','m','n','o','p','q','r','s','t',
        'u','v','w','x','y','z',
        'A','B','C','D','E','F','G','H','I','J',
        'K','L','M','N','O','P','Q','R','S','T',
        'U','V','W','X','Y','Z'
    );
    code='';//重新初始化验证码
    //alert(arrays.length);
    //随机从数组中获取四个元素组成验证码
    for(var i=0;i<4;i++){
        //随机获取一个数组的下标
        var r=parseInt(Math.random()*arrays.length);
        code+=arrays[r];
        //alert(arrays[r]);
    }
    //alert(code);
    document.getElementById('code').innerHTML=code;//将验证码写入指定区域
}
var ContentType = "application/json; charset=utf-8";
var AcceptType = "application/json; charset=utf-8";
//点击登录事件
$("#login").click(function(){
    var account = $("#account").val();
    var password = $("#password").val();
    var verify = $("#verify").val();
    if( account=="" || password=="" ){
        var obj = document.getElementById("errorMsg");
        $("#tipMsg").html("请填写完整的登录信息！");
        obj.setAttribute("class", "tip-box visiblility-show");
        return ;
    }
    //验证码不对
    /*	if( code.toLowerCase()!=verify){
            //提示错误信息
            var obj = document.getElementById("errorMsg");
            //修改提示文字
            $("#tipMsg").html("请填写正确的验证码！");
            obj.setAttribute("class", "tip-box visiblility-show");
            return ;
        }*/
    //判断是否勾选记住登录信息
    if ($("#remember").prop("checked") == true) {
        //记录信息
        var account = $("#account").val();
        var password = $("#password").val();
        //alert(passWord);
        $.cookie("account", account);
        $.cookie("password", password,{ expires: 7 }); // 存储一个带7天期限的 cookie 如果{ expires: 7 }
    }else{
        $.cookie("account", "");
        $.cookie("password", "");
    }
    jQuery.ajax({
        url: 'user/login.do',
        type:"POST",
        cache:false,
        data: JSON.stringify({
            username: account,
            password:password
        }),
        contentType:ContentType,
        headers: {
            Accept: AcceptType
        },
        dataType:"json",
        success:function(data){
            if(!data.success){
                $("#password").val("");
                $.cookie("password", "");
                $("#verify").val("")
                //changeImg();
                alert("密码错误");
            }else{ //登录成功跳转页面
                window.location.href = 'main.html';
            }
        }
    });

});

//点击错误信息关闭按钮
$("#closeErrorMsg").click(function(){
    var obj = document.getElementById("errorMsg");
    obj.setAttribute("class", "tip-box visiblility-hidden");
});

//关闭错误提示
function closeErrorMsg(){
    var obj = document.getElementById("errorMsg");
    obj.setAttribute("class", "tip-box visiblility-hidden");
};

//验证码提示
$('.imgcode').hover(function(){
    layer.tips("点击更换验证码", '#code', {
        time: 2000,
        tips: [2, "#3c3c3c"]
    });
});
