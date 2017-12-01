package com.proxy.jdkproxy;

import com.proxy.performance.PerformanceMonitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PerformanceHandler implements InvocationHandler {

    private Object target;

    /**
     *  传入的实现类 必须为接口的实现类
     * @param target
     */
    public PerformanceHandler(Object target) {
        this.target = target;
    }

    /**
     * @param proxy 代理类(由Proxy派生) @{@link java.lang.reflect.Proxy}
     * @param method 接口中的方法
     * @param args 方法参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        PerformanceMonitor.begin(target.getClass().getName()+"."+method.getName());

        Object obj = method.invoke(target, args);

        PerformanceMonitor.end();

        return obj;
    }
}
