app.controller('searchController',function ($scope, searchService) {

    // 定义搜索对象结构 category：商品分类
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{}};

    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
            }
        );
    }

})