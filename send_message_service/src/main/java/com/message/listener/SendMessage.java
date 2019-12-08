package com.message.listener;

import com.message.sendMessage.IndustrySMS;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SendMessage {

    @JmsListener(destination = "sms")
    public void sendSMS(Map<String, String> map) {
        System.out.println("消息开始发送："+map);
        IndustrySMS.execute(map.get("phone"), map.get("verifyCode"));
        System.out.println("消息发送完成:"+map);
    }
}
