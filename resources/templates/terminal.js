var logoutButton = document.getElementById('logout');
logoutButton.addEventListener('click', function () {
    console.log('???');
    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://' + window.location.host + '/logout', true);
    xhr.onload = function () {
        console.log(xhr.responseURL);
        window.location.href = xhr.responseURL;
    }
    xhr.send();
})
$.ajax({
    url: "tsconfig.json",//json文件位置
            type: "GET",//请求方式为get
                dataType: "json", //返回数据格式为json
                success: function(data) {//请求成功完成后要执行的方法
                //each循环 使用$.each方法遍历返回的数据date
                $.each(data.first, function(i, item) {
                    var str = '<li><lo><a href="channel-main">' + item.name + 'Channel'+  '</a></lo></li>';
                    $("#channel_list").append(str);
        })
    }
})
