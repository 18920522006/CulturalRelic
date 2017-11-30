package com.beanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.beans.PropertyDescriptor;

public class MyInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter{

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if("user".equals(beanName)){
            System.out.println(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        if ("user".equals(beanName)) {
            System.out.println(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName());
        }
        return super.postProcessPropertyValues(pvs, pds, bean, beanName);
    }
}
