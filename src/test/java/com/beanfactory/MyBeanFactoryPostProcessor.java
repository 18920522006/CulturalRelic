package com.beanfactory;

import com.entity.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition user = beanFactory.getBeanDefinition("user");
        user.getPropertyValues().addPropertyValue("username","zhangsan");
        System.out.println(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());

        /**
         * 动态注册BeanDefinition
         */
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory)beanFactory;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("username","lisi");
        factory.registerBeanDefinition("user2",beanDefinitionBuilder.getRawBeanDefinition());

        /**
         * 直接注册一个Bean
         */
        User user3 = new User();
        user3.setUsername("wangwu");
        factory.registerSingleton("user3",user3);
    }
}
