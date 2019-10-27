//控制层
app.controller('brandController' ,function($scope,$controller,brandService){

    $controller('baseController',{$scope:$scope});

    //查询品牌列表
    $scope.findAll=function(){
        brandService.findAll().success(
            function(response){
                $scope.list=response;
            }
        );
    };

    //分页
    $scope.findPage=function(page,rows){
        brandService.findPage(page,rows).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };

    //新增
    $scope.save = function () {
        var  Object = null; //方法名称
        if ($scope.entity.id!=null){
            Object = brandService.update($scope.entity);
        }else {
            Object = brandService.add($scope.entity);
        }
        Object.success(
            function (response) {
                if (response.success){
                    $scope.reloadList(); //重新加载
                } else {
                    alert(response.message)
                }
            }
        )
    };

    //查询尸体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }

        )
    };

    //批量删除
    //删除
    $scope.delete=function(){
        if(confirm('确定要删除吗？')){
            brandService.delete($scope.selectIds).success(
                function(response){
                    if(response.success){
                        $scope.reloadList();//刷新
                        $scope.selectIds=[];
                    }else{
                        alert(response.message);
                    }
                }
            );
        }
    };

    $scope.searchEntity={};
    //条件查询
    $scope.search=function(page,rows){
        brandService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;//显示当前页数据
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

});
