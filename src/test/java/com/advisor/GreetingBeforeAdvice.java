package com.advisor;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class GreetingBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(target.getClass().getName()+"."+method.getName());
        if("setSelfProxy".equals(method.getName())){
            return;
        }
        String clientName = (String) args[0];
        System.out.println("你好吗 " + clientName);
    }
}
