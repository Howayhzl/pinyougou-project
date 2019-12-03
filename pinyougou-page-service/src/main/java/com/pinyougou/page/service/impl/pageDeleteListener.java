package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class pageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long [] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("接受到消息"+goodsIds);
            boolean b = itemPageService.deleteHtml(goodsIds);
            System.out.println("删除详细页结果："+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
