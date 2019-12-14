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

    // 购物库数量的加减
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if (response.success){
                    $scope.findCartList(); //刷新列表
                }else {
                    alert(response.message)//弹出错误提示
                }
            }
        )
    }
})