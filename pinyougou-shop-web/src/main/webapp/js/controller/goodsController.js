 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}

	//新增
	$scope.add=function(){
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(
			function(response){
				if(response.success){
					alert("新增成功")
					$scope.entity ={};
					editor.html('');
				}else{
					alert(response.message);
				}
			}
		);
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//上传图片
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(
			function (reaponse) {
				if (reaponse.success){ //取出URL
					$scope.image_entity.url = reaponse.message;
				} else {
					alert("上传错误")
				}
			}
		)
	}

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};//定义页面实体结构
	//把当前的图片实体存入图片列表
	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}

	// 移除图片
	$scope.remove_image_entity = function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1)
	}

	// 一级分类下拉选择框
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.selectItemCat1List = response;
			}
		)
	}

	// 二级分类下拉选择框
	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.selectItemCat2List = response;
			}
		)
	});

	// 三级分类下拉选择框
	$scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.selectItemCat3List = response;
			}
		)
	})

	//显示模板ID
	$scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId = response.typeId;
			}
		)
	})

	// 获取品牌下拉列表
	$scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
		typeTemplateService.findOne(newValue).success(
			function (response) {
				$scope.typeTemplate = response; // 获取模板类型类
				$scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds) // 在模板类型类中取出关联品牌列表

				$scope.entity.goodsDesc.customAttributeItems =JSON.parse($scope.typeTemplate.customAttributeItems)
			}
		)

		//查询规格列表
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specialList = response;
			}
		)

	})

	$scope.updateSpecAttribute = function ($event,name, value) {
		var object =$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name)

		if (object!=null){
			if ($event.target.checked){
				object.attributeValue.push(value)
			}else { //取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(value),1) //移除选项
				if (object.attributeValue.length == 0){ //移除对象
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1)
				}
			}

		}else {
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
		}
	}


	//创建SKU列表
	$scope.createItemList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
		var items=  $scope.entity.goodsDesc.specificationItems;
		for(var i=0;i< items.length;i++){
			alert($scope.entity.itemList)
			$scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
		}
	}

	//添加列值
	addColumn=function(list,columnName,conlumnValues){
		var newList=[];//新的集合
		for(var i=0;i<list.length;i++){
			var oldRow= list[i];
			for(var j=0;j<conlumnValues.length;j++){
				var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
				newRow.spec[columnName]=conlumnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}

	$scope.status = ['未审核','已审核','审核未通过','驳回']  // 商品状态

	$scope.itemCatList =[]; //商品列表分类

	// 查询商品分类列表
	$scope.findItemCatList = function () {
		itemCatService.findAll().success(
			function (response) {
				for (var i=0; i<response.length;i++){
					$scope.itemCatList[response[i].id] = response[i].name;
				}
			}
		)
	}

});	
