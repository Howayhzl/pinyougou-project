package com.pinyougou.user.controller;
import java.util.List;

import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.utils.PhoneFormatCheckUtils;
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
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,	String smsCode){
		// 校验验证码是否正确
		boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smsCode);
		if (!checkSmsCode){
			return new Result("验证码不正确",false);
		}
		try {
			userService.add(user);
			return new Result( "增加成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("增加失败",false);
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result( "删除成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result("删除失败",false);
		}
	}
	
		/**
	 * 查询+分页
	 * @param user
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}

	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		// 对手机号码最验证
		if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
			return new Result("手机格式不正确",false);
		}
		try {
			userService.createSmsCode(phone);
			return new Result( "发送验证码成功",true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result( "发送验证码失败",false);
		}
	}
}
