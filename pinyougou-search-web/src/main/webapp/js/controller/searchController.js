app.controller('searchController',function ($scope, searchService) {

    // 定义搜索对象结构 category：商品分类
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':20,'sort':'','sortField':''};//搜索对象

    //搜索
    $scope.search = function () {
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo) //转换为数字
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
               // $scope.searchMap.pageNo=1; //查询后显示第一页
                buildPageLable()// 构建分页栏
            }
        );
    }

    buildPageLable = function(){
        // 构建分页栏
        $scope.pageLable=[];
        var firstPage =1; //开始页码
        var  lastPage = $scope.resultMap.totalPages;

        $scope.firstDot=true; //前面有点
        $scope.endtDot=true; //后面有点

        if ($scope.resultMap.totalPages>5){ //如果页码数量大于5
            if ($scope.searchMap.pageNo <= 3){
                lastPage=5;
                $scope.firstDot=false; //前面没点
            } else if ($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2 ) {//显示后5页
                firstPage=$scope.resultMap.totalPages-4;
            }else {//显示以当前页为中心的5页
                firstPage = $scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
                $scope.endtDot=false; //后边没点
            }


        }else {
            $scope.firstDot=false; //前面无点
            $scope.endtDot=false; //后面无点
        }

        // 构建页码
        for (var i=firstPage;i<=lastPage;i++){
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

    // 分页查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search(); //查询
    }

    // 判断当前页是否第一页
    $scope.isTopPage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        }else {
            return false;
        }
    }

    // 判断当前页是否最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true
        }else {
            return false;
        }
    }

    // 排序查询
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;

        $scope.search(); //查询
    }

    // 判断输入的关键字是否品牌
      $scope.keywordsIsBrand = function () {
        for (var i=0; i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){ //如果输入的字包含品牌
                return true
            }

        }
        return  false;
     }

})