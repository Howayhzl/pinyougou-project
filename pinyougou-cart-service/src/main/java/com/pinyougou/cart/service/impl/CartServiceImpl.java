package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据skuID查询商品明细的SKU对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){
            throw  new RuntimeException("该商品不存在");
        }
        if (!item.getStatus().equals("1")){
            throw  new RuntimeException("该商品状态不合法");
        }
        // 2.根据SKU对象查找到商家ID
        String sellerId = item.getSellerId();
        // 3.根据商家ID在购物车列表中查询购物车对象
        Cart cart = searchCartBySellerId(cartList, sellerId);
        // 4.如果购物车列表中不存在该商家的购物车
        if (cart == null){
            // 4.1创建一个新的购物车
           cart = new Cart();
           cart.setSellerId(sellerId); // 商家ID
            cart.setSellerName(item.getSeller()); //商家名称
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(num, item);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            // 4.2 将新的购物车对象添加到购物车列表中
            cartList.add(cart);
        }else {
            // 5.如果购物车列表中存在该商家的购物车
            TbOrderItem orderItem = searchOrderItemByITemId(cart.getOrderItemList(), itemId);
            // 判断该商品是否在该购物车明细列表中存在
            if (orderItem == null){
                // 5.1 如果不存在，创建新的购物车明细对象，并添加到该购物车的明细列表中
                 orderItem= createOrderItem(num, item);
                 cart.getOrderItemList().add(orderItem);
            }else {
                // 5.2 如果存在，在原有的数量上添加数量，并且更新金额
                orderItem.setNum(orderItem.getNum()+num); //更改数量
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                // 当明细的数量小于等于0，移除此明细
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                // 当购物的明细数量为0时，在购物车列表中移除该购物车
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }

        }
        return cartList;
    }

    /**
     * 根据skuID在购物车明细列表中查询购物车明细对象
     * @param orderItemList
     * @param itemId
     * @return
     */
    public TbOrderItem searchOrderItemByITemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     * @param num
     * @param item
     * @return
     */
    private TbOrderItem createOrderItem(Integer num, TbItem item) {
        // 创建新的明细购物车
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    /**
     * 根据商家ID在购物车中查找购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
