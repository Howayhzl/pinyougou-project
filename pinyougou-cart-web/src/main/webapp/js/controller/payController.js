app.controller('payController' ,function($scope ,payService){
    //本地生成二维码
    $scope.createNative=function(){
        payService.createNative().success(
            function(response){
                $scope.money=  (response.total_fee/100).toFixed(2) ;	//金额
                $scope.out_trade_no= response.out_trade_no;//订单号
                //二维码
                var qr = new QRious(
                    {
                        element:document.getElementById('qrious'),
                        size:250,
                        value: response.code_url,
                        level:'H'
                    }
                );
                // 查询支付状态
                queryPayStatus();
            }
        );
    }

    queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (respose) {
                if (respose.success){
                    location.href="paysuccess.html"
                } else {
                    location.href="payfail.html"
                }
            }
        )
    }
});
