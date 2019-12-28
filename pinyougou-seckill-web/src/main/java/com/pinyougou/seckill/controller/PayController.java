package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
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

    @Reference
    private SeckillOrderService seckillOrderService;


    @RequestMapping("/createNative")
    public Map createNative(){
        // 1.获取当前登录用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.根据当前登录用户到redis查询秒杀订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        // 判断秒杀订单是否存在
        if (seckillOrder!=null){
            long fen=  (long)(seckillOrder.getMoney().doubleValue()*100);//金额（分）
            return weixinPayService.createNative(seckillOrder.getId()+"",+fen+"");
        } else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        // 1.获取当前登录用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

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
                // 保存订单到数据库
                seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no),map.get("transaction_id"));
                break;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x>=100){
                result = new Result("二维码超时",false);

                Map<String,String> payResult = weixinPayService.closePay(out_trade_no);
                if (payResult!=null && payResult.get("return_code").equals("FAIL")){
                    if (payResult.get("err_code").equals("ORDERPAID")){
                        // 支付成功
                        result = new Result("支付成功",true);
                        // 保存订单
                        seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no),map.get("transaction_id"));
                    }
                }
                if (result.isSuccess() == false){
                    // 删除订单
                    seckillOrderService.deleteOrderFromRedis(userId,Long.valueOf(out_trade_no));
                }

                break;
            }
        }
        return result;
    }
}
