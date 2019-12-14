// 购物车控制层
app.controller('cartController',function ($scope, cartService) {

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (resposne) {
                $scope.cartList=resposne;
            }
        )
    }
})