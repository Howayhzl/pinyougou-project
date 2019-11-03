//控制层
app.controller('indexController',function ($scope,loginService) {
    /*显示当前用户名*/
    $scope.showLoginName = function () {
        loginService.loginName().service(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }
});