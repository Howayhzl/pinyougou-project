package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
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
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
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
			// 从索引库中删除数据
            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));

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
		return goodsService.findPage(goods, page, rows);		
	}

	@Reference(timeout = 100000)
	private ItemSearchService itemSearchService;
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[]ids,String status){
		try {
			// *****导入到索引库
			goodsService.updateStatus(ids,status);
            List<TbItem> itemList = goodsService.findItemListByGoodsIdAndStatus(ids, status);
            //调用搜索接口实现数据批量导入
            if(itemList.size()>0){
            	// 导入到solr
                itemSearchService.importList(itemList);
            }else{
                System.out.println("没有明细数据");
            }
            // *****生成商品详情页
			for (Long goodsId : ids) {
				itemPageService.generateHtml(goodsId);
			}
            return new Result( "修改成功",true);
		}catch (Exception e){
			e.printStackTrace();
			return new Result( "修改失败",false);
		}

	}

	@Reference(timeout=40000)
	private ItemPageService itemPageService;
	/**
	 * 生成静态页（测试）
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		itemPageService.generateHtml(goodsId);
	}


}
