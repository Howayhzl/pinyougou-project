package cn.itcast.demo;

import cn.itcast.sms.IndustrySMS;

import java.util.Random;

public class SMSText {
    public static void main(String[] args) {
        Random random = new Random();
        String result="";
        for (int i=0;i<6;i++)
        {
            result += (int)random.nextInt(10);
        }

        IndustrySMS.execute("13669267422", result);
    }
}
