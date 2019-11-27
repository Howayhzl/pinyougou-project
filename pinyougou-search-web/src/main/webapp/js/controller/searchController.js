app.controller('searchController',function ($scope, searchService) {

    // 定义搜索对象结构 category：商品分类
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20};//搜索对象

    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;

                buildPageLable()// 构建分页栏
            }
        );
    }

    buildPageLable = function(){
        // 构建分页栏
        $scope.pageLable=[];
        var firstPage =1; //开始页码
        var  lastPage = $scope.resultMap.totalPages;

        if ($scope.resultMap.totalPages>5){ //如果页码数量大于5
            if ($scope.searchMap.pageNo <= 3){
                lastPage=5;
            } else if ($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2 ) {//显示后5页
                firstPage=$scope.resultMap.totalPages-4;
            }else {//显示以当前页为中心的5页
                firstPage = $scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }


        }

        // 构建页码
        for (var i=firstPage;i<lastPage;i++){
            $scope.pageLable.push(i);
        }
    }

    //添加搜索项
    $scope.addSearchItem=function(key,value){
        if(key=='category' || key=='brand' || key=='price'){//如果点击的是分类或者是品牌
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        $scope.search();
    }

    //移除复合搜索条件
    $scope.removeSearchItem=function(key){
        if(key=="category" ||  key=="brand" || key=='price'){//如果是分类或品牌
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();
    }


})