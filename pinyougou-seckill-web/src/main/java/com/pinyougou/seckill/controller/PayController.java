package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Autowired
    private SeckillOrderService seckillOrderService;


    @RequestMapping("/createNative")
    public Map createNative(){
        // 1.获取当前登录用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.提取秒杀订单(从缓存)
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        if (seckillOrder != null){
            //3.调用微信支付接口
            Map nativeMap = weixinPayService.createNative(seckillOrder.getId()+"", (long)(seckillOrder.getMoney().doubleValue()*100)+"");
            return nativeMap;
        }else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        int x = 0;
        while (true){
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);// 调用查询
            if (map == null){
                result = new Result("支付发生错误",false);
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")){
                result = new Result("支付成功",true);
                // 支付成功后更新订单状态
               // orderService.updateOrderStatus(out_trade_no,map.get("transaction_id")); // 修改订单状态
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x>=4){
                result = new Result("二维码超时",false);
                break;
            }
        }
        return result;
    }
}
