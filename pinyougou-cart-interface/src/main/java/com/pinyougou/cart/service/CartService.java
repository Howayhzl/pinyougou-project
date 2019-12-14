package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * @param itemId
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从缓存中查询购物车
     * @param userName
     * @return
     */
    public  List<Cart> findCartListFromRedis(String userName);

    /**
     * 向redis中存购物车
     * @param userName
     * @param cartList
     */
    public void saveCartListToRedis(String userName,List<Cart> cartList);
}
