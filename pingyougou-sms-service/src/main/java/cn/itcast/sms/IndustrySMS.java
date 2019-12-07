package cn.itcast.sms;

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
    //private static String to = "13279919393";

    /**
     * 验证码通知短信
     */
    public static void execute(String to, String random) {
        String templateid = "【记忆科技深圳有限公司】尊敬的用户，您的验证码为" + random;
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
