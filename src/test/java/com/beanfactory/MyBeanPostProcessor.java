package com.beanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        return bean;
    }
}
