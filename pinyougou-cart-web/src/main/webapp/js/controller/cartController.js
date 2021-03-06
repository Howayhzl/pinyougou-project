// 购物车控制层
app.controller('cartController',function ($scope, cartService) {

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (resposne) {
                $scope.cartList=resposne;
                $scope.totalValue=cartService.sum($scope.cartList);//求合计数
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

    // 求合计
   /* sum = function () {
        $scope.totalNum = 0; //总数量
        $scope.totalMoney = 0; // 总金额

        for (var i=0; i<$scope.cartList.length;i++){
            var cart = $scope.cartList[i]; //购物车对象
            for (var j=0; j<cart.orderItemList.length; j++){
                var orderItem = cart.orderItemList[j]; // 购物车明细
                $scope.totalNum += orderItem.num; // 累加数量
                $scope.totalMoney += orderItem.totalFee; // 累加金额

            }
        }
    }*/

   // 获取当前用户地址列表
   $scope.findAddressList = function () {
       cartService.findAddressList().success(
           function (response) {
               $scope.findAddressList = response;
               //设置默认地址
               for (var i=0; i<$scope.findAddressList.length;i++){
                   if ($scope.findAddressList[i].isDefault=='1'){
                       $scope.adress = $scope.findAddressList[i];
                       break;
                   }
               }
           }
       )
   }

   // 选择地址
   $scope.selectAddress = function (address) {
       $scope.adress = address;
   }

    //判断是否是当前选中的地址
    $scope.isSelectedAddress = function (address) {
        if (address == $scope.adress){
            return true;
        } else {
            return false;
        }
    }

    $scope.order = {paymentType:'1'};

   // 选择支付类型
   $scope.selectPayType = function (type) {
       $scope.order.paymentType = type;
   }

   // 保存订单
   $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.adress.address; // 地址
        $scope.order.receiverMobile = $scope.adress.mobile; // 手机
        $scope.order.receiver = $scope.adress.contact; // 联系人
        cartService.submitOrder($scope.order).success(
            function (resposne) {
                if(resposne.success){
                  //页面跳转
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message);	//也可以跳转到提示页面
                }
            }
       )
   }
})