package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import com.utils.CookieUtil;
import entity.Result;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public List<Cart> findCartLIst(){
        // 从cookie中提取购物车
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (CommonUtils.isEmpty(cartListString)){
            cartListString = "[]";
        }
        List<Cart> cartList = JSON.parseArray(cartListString, Cart.class);
        return cartList;
    }



    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
        // 从cookie中提取购物车
        try {
            List<Cart> cartList = findCartLIst();
            // 调用服务方法操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            // 将新的购物车存入cookie
            String cartListString = JSON.toJSONString(cartList);
            CookieUtil.setCookie(request,response,"cartList",cartListString,3600*24,"utf-8");
            return new Result("存入购物车成功",true);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result("存入购物车失败",false);
        }
    }
}
