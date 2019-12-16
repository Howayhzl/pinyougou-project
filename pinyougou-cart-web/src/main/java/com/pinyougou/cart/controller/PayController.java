package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.utils.IdWorker;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/createNative")
    public Map createNative(){
        IdWorker idWorker = new IdWorker();
        Map nativeMap = weixinPayService.createNative(String.valueOf(idWorker.nextId()), "1");
        return nativeMap;
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        while (true){
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);// 调用查询
            if (map == null){
                result = new Result("支付发生错误",false);
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")){
                result = new Result("支付成功",true);
                break;
            }
        }
        return result;
    }
}
