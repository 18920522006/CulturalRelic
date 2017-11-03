package com.config;

import com.beanfactory.MyBeanFactoryPostProcessor;
import com.beanfactory.MyBeanPostProcessor;
import com.beanfactory.MyInstantiationAwareBeanPostProcessor;
import com.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean(name="user",initMethod = "myInit",destroyMethod = "myDestroy")
    public User buildUser(){
        User user = new User();
        user.setUsername("wangchen");
        return user;
    }

    @Bean
    public MyBeanPostProcessor buildBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }

    @Bean
    public MyBeanFactoryPostProcessor buildBeanFactoryPostProcessor() {
        return new MyBeanFactoryPostProcessor();
    }

    @Bean
    public MyInstantiationAwareBeanPostProcessor buildInstantiationAwareBeanPostProcessor() {
        return new MyInstantiationAwareBeanPostProcessor();
    }
}
