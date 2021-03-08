var exec = require('cordova/exec');
var webHttp = function () { };

// arg1：成功回调
// arg2：失败回调
// arg3：将要调用类配置的标识
// arg4：调用的原生方法名
// arg5：参数，json格式

webHttp.prototype.get = function (url, query, params, success, error) {
    exec(success, error, "HttpWeb", "get", [url, query, params]);
};
webHttp.prototype.post = function (url, query, params, success, error) {
    exec(success, error, "HttpWeb", "post", [url, query, params]);
};
webHttp.prototype.getImage = function (url, query, params, success, error) {
    exec(success, error, "HttpWeb", "getImage", [url, query, params]);
};
webHttp.prototype.getDate = function (success, error) {
    exec(success, error, "HttpWeb", "getDate", ['', '', {}]);
};
var http = new webHttp();
module.exports = http;