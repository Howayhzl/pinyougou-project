package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> list, Long itemId, Integer num) {
        //1.根据skuID查询商品明细的SKU对象

        // 2.根据SKU对象查找到商家ID

        // 3.根据商家ID在购物车列表中查询购物车对象

        // 4.如果购物车列表中不存在该商家的购物车
            // 4.1创建一个新的购物车
            // 4.2 将新的购物车对象添加到购物车列表中

        // 5.如果购物车列表中存在该商家的购物车
            // 判断该商品是否在该购物车明细列表中存在
            // 5.1 如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中
            // 5.2 如果存在，在原有的数量上添加数量，并且更新金额
        return null;
    }
}
