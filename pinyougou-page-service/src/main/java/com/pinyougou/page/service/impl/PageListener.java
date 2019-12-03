package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 监听类，用于生成详细页
 */
@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String goodsId  = textMessage.getText();
            System.out.println("接收到消息:"+goodsId);
            boolean b = itemPageService.generateHtml(Long.parseLong(goodsId));
            System.out.println("生成商品详细页结果:"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
