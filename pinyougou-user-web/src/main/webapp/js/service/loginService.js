//服务层
app.service('loginService',function ($http) {

    //读取登录用户名列表数据绑定到表单中
    this.showName=function () {
        return $http.get('../login/name.do');
    }
})