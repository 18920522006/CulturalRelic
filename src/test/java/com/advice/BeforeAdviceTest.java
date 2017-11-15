package com.advice;

import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

public class BeforeAdviceTest {
    @Test
    public void before() {
        NaiveWaiter target = new NaiveWaiter();
        GreetingBeforeAdvice advice = new GreetingBeforeAdvice();

        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.addAdvice(advice);

        //使用 setInterfaces ,jdk 动态代理
        factory.setInterfaces(target.getClass().getInterfaces());
        //使用 setOptimize(true) cglib 动态代理 (优先)
        factory.setOptimize(true);

        Waiter proxy = (Waiter)factory.getProxy();
        proxy.greetTo("wangchen");
        proxy.serveTo("wangchen");
    }
}
