package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.awt.print.Pageable;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        // 1.参数封装
        Map param = new HashMap();
        param.put("appid",appid); //公众账号ID
        param.put("mch_id",partner); // 商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr()); // 随机字符串
        param.put("body","品优购"); //商品描述
        param.put("out_trade_no",out_trade_no); //商户订单号
        param.put("total_fee",total_fee); //标价金额
        param.put("spbill_create_ip","127.0.0.1"); // 终端IP
        param.put("notify_url","http://www.itcast.cn"); // 通知地址
        param.put("trade_type","NATIVE"); //交易类型
        //2.发送请求

        //3.获取结果

        return null;
    }
}
