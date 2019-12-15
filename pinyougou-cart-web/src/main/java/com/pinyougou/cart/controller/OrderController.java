package com.pinyougou.cart.controller;
import java.util.List;

import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){
		return orderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return orderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){
		// 获取当前登录用户名
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		order.setUserId(userName);
		order.setSourceType("2"); // 订单来源PC
		try {
			orderService.add(order);
			return new Result( "增加成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("增加失败",false);
		}
	}
	
	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
			return new Result("修改成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result( "修改失败",false);
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long id){
		return orderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			orderService.delete(ids);
			return new Result("删除成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("删除失败",false);
		}
	}
	
		/**
	 * 查询+分页
	 * @param order
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);		
	}
	
}
