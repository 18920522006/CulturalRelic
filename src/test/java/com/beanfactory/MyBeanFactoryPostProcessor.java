package com.beanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition user = beanFactory.getBeanDefinition("user");
        user.getPropertyValues().addPropertyValue("username","zhangsan");
        System.out.println("调用 BeanFactoryPostProcessor.postProcessBeanFactory");
    }
}
