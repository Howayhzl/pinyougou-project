//服务层
app.service('userService',function($http){
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../user/findPage.do?page='+page+'&rows='+rows);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../user/add.do',entity );
	}
});
