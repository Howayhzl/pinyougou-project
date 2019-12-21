app.controller('seckillGoodsController' ,function($scope,$location,seckillGoodsService){
    //读取列表数据绑定到表单中
    $scope.findList=function(){
        seckillGoodsService.findList().success(
            function(response){
                $scope.list=response;
            }
        );
    }


    //查询商品
    $scope.findOne=function(){
        // 接受参数ID
        var id= $location.search()['id'];
        alert(id)
        seckillGoodsService.findOne(id).success(
            function(response){
                $scope.entity=response;
            }
        );
    }

});
