package com.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;

public class MailSendMulticaster implements ApplicationContextAware {

    private  ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void sendMail(String to) {
        System.out.println("MailSendMulticaster: 模拟发送邮件...");
        MailSendEvent mailSendEvent = new MailSendEvent(applicationContext);
        mailSendEvent.setTo(to);
        applicationContext.publishEvent(mailSendEvent);
    }
}
