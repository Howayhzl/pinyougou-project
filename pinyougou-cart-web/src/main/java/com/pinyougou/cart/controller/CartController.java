package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.utils.CookieUtil;
import entity.Result;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 60000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public List<Cart> findCartLIst(){

        // 当前登陆人账号，判断当前是否有人登陆
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆人："+name);
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (CommonUtils.isEmpty(cartListString)){
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if (name.equals("anonymousUser")){ // 未登录
            // 从cookie中提取购物车
            System.out.println("从cookie中提取购物车");

            return cartList_cookie;
        }else { // 已登录
            System.out.println("从redis中提取购物车");
            // 合并购物车逻辑
            // 获取redis购物车
            List<Cart> cartList_redis = cartService.findCartListFromRedis(name);
            if (cartList_cookie.size()>0){ // 判断本地购物车是否存在，存在才合并，提高性能
                List<Cart>  cartList_merge = cartService.mergeCartList(cartList_cookie, cartList_redis);
                // 将合并后的购物车存入redis
                cartService.saveCartListToRedis(name,cartList_merge);
                // 本地购物车清楚
                CookieUtil.deleteCookie(request,response,"cartList");
                return cartList_merge;
            }
            return cartList_redis;
        }
    }



    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
        // 当前登陆人账号，判断当前是否有人登陆
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆人："+name);
        // 提取购物车
        List<Cart> cartList = findCartLIst();
        // 调用服务方法操作购物车
        cartList = cartService.addGoodsToCartList(cartList, itemId, num);
        try {
            if (name.equals("anonymousUser")){ // 未登录
                System.out.println("将新的购物车存入cookie");
                // 将新的购物车存入cookie
                String cartListString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",cartListString,3600*24,"utf-8");
            }else {
                System.out.println("将新的购物车存入redis中");
                cartService.saveCartListToRedis(name,cartList);
            }
            return new Result("存入购物车成功",true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result("存入购物车失败",false);
        }
    }
}
