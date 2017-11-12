package com.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class MailSendListener implements ApplicationListener<MailSendEvent> {

    @Override
    public void onApplicationEvent(MailSendEvent event) {
        System.out.println("MailSendListener :" + event.getTo() + "发送邮件！");
    }
}
