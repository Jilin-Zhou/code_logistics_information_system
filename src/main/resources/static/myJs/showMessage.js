
/**
 * 弹出消息提示框，采用浏览器布局，位于整个页面中央，默认显示3秒
 * 后面的消息会覆盖原来的消息
 * @param message：待显示的消息
 * @param type：消息类型，0：错误消息，1：成功消息
 */
function showMessage(message, type) {
    let messageJQ= $("<div class='showMessage'>" + message + "</div>");
    if (type == 0) {
        messageJQ.addClass("showMessageError");
    } else if (type == 1) {
        messageJQ.addClass("showMessageSuccess");
    }
    // 先将原始隐藏，然后添加到页面，最后以400毫秒的速度下拉显示出来
    messageJQ.hide().appendTo("body").slideDown(400);
    // 4秒之后自动删除生成的元素
    window.setTimeout(function() {
        messageJQ.show().slideUp(400, function() {
            messageJQ.remove();
        })
    }, 4000);
}

/**
 *  在某页面上打开一个新页面，
 * @param url
 * @param param
 */
function open_page(url, param) {
    var form = '<form action="' + url + '"  target="_blank"  id="windowOpen" style="display:none">';
    for(var key in param) {
        form += '<input name="' + key + '" value="' + param[key] + '"/>';
    }
    form += '</form>';
    $('body').append(form);
    $('#windowOpen').submit();
    $('#windowOpen').remove();
};


var host = "localhost:8888";
var httphost = "http://localhost:8888";

// var wshost = "ws:localhost:8888";
// var wshost = "ws:192.168.200.53:8888";

