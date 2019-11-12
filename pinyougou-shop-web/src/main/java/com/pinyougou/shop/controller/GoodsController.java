package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		//获取登录名
		String sellerId= SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getGoods().setSellerId(sellerId);//设置商家ID
		try {
			goodsService.add(goods);
			return new Result("增加成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("增加失败",false);
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		// 当前商家ID
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		// 首先要判断商品是否是该商家的商品
		Goods goods2 = goodsService.findOne(goods.getGoods().getId());
		if (!goods2.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId)){
			return new Result("非法操作",false);
		}

		try {
			goodsService.update(goods);
			return new Result("修改成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("修改失败",false);
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result( "删除成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("删除失败",false);
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		// 获取商家ID
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		// 添加查询条件
		goods.setSellerId(name);
		return goodsService.findPage(goods, page, rows);		
	}


	/**
	 * 设置商品上下架状态
	 * @param ids
	 * @param marketable
	 */
	@RequestMapping("/setSaleStatus")
	public Result setSaleStatus(Long[] ids, String marketable) {
		try {
			goodsService.setSaleStatus(ids,marketable);
			return new Result( "设置成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("设置失败",false);
		}
	}
	
}
