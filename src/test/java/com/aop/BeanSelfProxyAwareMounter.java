package com.aop;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import java.util.Map;


/**
 *  SystemBootAddon 标志 为转配器
 *  装配所有的BeanSelfProxyAware的实体类，回传自己的引用
 */
public class BeanSelfProxyAwareMounter implements SystemBootAddon, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onReady() {
        Map<String, BeanSelfProxyAware> beans = applicationContext.getBeansOfType(BeanSelfProxyAware.class);
        if (beans == null || beans.size() == 0)
            return;
        for (BeanSelfProxyAware beanSelfProxyAware : beans.values()) {
            beanSelfProxyAware.setSelfProxy(beanSelfProxyAware);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
