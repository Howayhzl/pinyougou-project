package com.pinyougou.manager.controller;

import java.util.List;



import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brand")
public class BrandController {

	@Reference
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();		
	}

	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows){
		return brandService.findPage(page,rows);
	}

	@RequestMapping("/add")
	public Result addBrand(@RequestBody TbBrand brand){
		try {
			brandService.addBrand(brand);
			return new Result("添加成功",true);
		}catch (Exception e){
			return new Result("添加失败",false);
		}
	}

	@RequestMapping("/update")
	public Result updateBrand(@RequestBody TbBrand brand){
		try {
			brandService.update(brand);
			return new Result("更新成功",true);
		}catch (Exception e){
			return new Result("更新失败",false);
		}
	}

	@RequestMapping("/findOne")
	public TbBrand findOneBrand(Long id){
		return 	brandService.findOne(id);
	}

}
