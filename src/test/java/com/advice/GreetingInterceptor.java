package com.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class GreetingInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();//目标入参方法
        System.out.println("欢迎光临");
        Object proceed = invocation.proceed();
        System.out.println("谢谢光临");
        return proceed;
    }
}
