package com.pinyougou.sellergoods.service;

import java.util.List;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

/**
 * 品牌接口
 * @author Administrator
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();

	public PageResult findPage(int pageNum,int pageSize);

	/**
	 * 添加品牌
	 * @param brand 品牌类
	 */
	public void addBrand(TbBrand brand);


	/**
	 * 更新
	 * @param brand
	 */
	public void update(TbBrand brand);


	/**
	 * 根据id获取实体
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);

	/**
	 * 删除品牌
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页查询
	 * @param brand 品牌对象
	 * @param pageNum 当前页码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbBrand brand, int pageNum,int pageSize);
	
}
