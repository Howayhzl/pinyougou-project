package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * @param list
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> list,Long itemId,Integer num);
}
