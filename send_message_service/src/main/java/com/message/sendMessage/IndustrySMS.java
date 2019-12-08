package com.message.sendMessage;


import com.message.config.Config;
import com.message.util.HttpUtil;

import java.net.URLEncoder;

/**
 * 验证码通知短信接口
 *
 * @ClassName: IndustrySMS
 * @Description: 验证码通知短信接口
 */
public class IndustrySMS {
    private static String operation = "/industrySMS/sendSMS";

    private static String accountSid = Config.ACCOUNT_SID;

    /**
     * 验证码通知短信
     */
    public static void execute(String to, String random) {
        String templateid = "【Howay科技】您的手机注册验证码是："+random+"，如非本人操作，请忽略本信息！";
        String tmpSmsContent = null;
        try {
            tmpSmsContent = URLEncoder.encode(templateid, "UTF-8");
            String url = Config.BASE_URL + operation;
            String body = "accountSid=" + accountSid + "&to=" + to + "&smsContent=" + tmpSmsContent
                    + HttpUtil.createCommonParam();

            // 提交请求
            String result = HttpUtil.post(url, body);
            System.out.println("result:" + System.lineSeparator() + result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
