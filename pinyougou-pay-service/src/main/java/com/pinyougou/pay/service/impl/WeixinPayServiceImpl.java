package com.pinyougou.pay.service.impl;


import java.util.HashMap;
import java.util.Map;

import com.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;


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
        //1.参数封装
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购");
        param.put("out_trade_no", out_trade_no);//交易订单号
        param.put("total_fee", total_fee);//金额（分）
        param.put("spbill_create_ip", "127.0.0.1");
        param.put("notify_url", "http://testpay.le1.com.cn/wechat/notify.htm");
        param.put("trade_type", "NATIVE");//交易类型
        param.put("sign","010B867767A9AC67CAF9AFF98BD2F546");

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求的参数："+xmlParam);

            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果
            String xmlResult = httpClient.getContent();

            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("微信返回结果"+mapResult);
            Map map=new HashMap<>();
            map.put("code_url", mapResult.get("code_url"));//生成支付二维码的链接
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);

            return map;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new HashMap();
        }
    }

    /* @Override
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
        param.put("notify_url","http://test.itcast.cn"); // 通知地址
        param.put("trade_type","NATIVE"); //交易类型

        try {
            String paramdXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数："+paramdXml);
            //2.发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramdXml);
            httpClient.post();
            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("返回结果："+mapResult);
            Map map = new HashMap();
            map.put("code_url",mapResult.get("code_url")); // 生成支付二维码连接
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_fee);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }*/
}
