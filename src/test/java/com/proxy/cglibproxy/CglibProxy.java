package com.proxy.cglibproxy;

import com.proxy.performance.PerformanceMonitor;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {

    private Enhancer enhancer = new Enhancer();

    public Object getProxy(Class clazz) {
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    /**
     *
     * @param obj child class (cglib build)
     * @param method class method
     * @param args method arguments
     * @param proxy
     * @return method proxy
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        PerformanceMonitor.begin(obj.getClass().getName()+"."+method.getName());
        Object result = proxy.invokeSuper(obj, args);
        PerformanceMonitor.end();
        return result;
    }
}
