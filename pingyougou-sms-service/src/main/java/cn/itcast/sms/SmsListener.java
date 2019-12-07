package cn.itcast.sms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @JmsListener(destination = "sms")
    public void sendSms(Map map) {
        IndustrySMS.execute((String) map.get("phone"), (String) map.get("code"));
        System.out.println("手机号：" + (String) map.get("phone"));
        System.out.println("验证码：" + (int) map.get("code"));
    }
}
