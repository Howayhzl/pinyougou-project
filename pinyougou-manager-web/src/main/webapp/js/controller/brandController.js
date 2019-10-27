//控制层
app.controller('brandController' ,function($scope,$controller,brandService){
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


    //分页控件配置currentPage:当前页   totalItems :总记录数  itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();
        }
    };

    //刷新列表
    $scope.reloadList=function(){
        $scope.search( $scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
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

    //删除
    $scope.selectIds=[];//用户勾选的ID集合
    //用户勾选复选框
    $scope.updateSelection=function($event,id){
        if($event.target.checked){
            $scope.selectIds.push(id);//push向集合添加元素
        }else{
            var index= $scope.selectIds.indexOf(id);//查找值的 位置
            $scope.selectIds.splice(index,1);//参数1：移除的位置 参数2：移除的个数
        }
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
