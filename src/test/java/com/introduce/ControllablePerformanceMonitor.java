package com.introduce;

import com.proxy.performance.PerformanceMonitor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 *  DelegatingIntroductionInterceptor
 */
public class ControllablePerformanceMonitor
        extends DelegatingIntroductionInterceptor
        implements Monitorable,Say {

    private ThreadLocal<Boolean> MonitorStatusMap = new ThreadLocal<>();

    @Override
    public void setMonitorActive(boolean active) {
        MonitorStatusMap.set(active);
    }

    public void say() {
        System.out.println("say hello");
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object obj = null;

        if(MonitorStatusMap.get() != null && MonitorStatusMap.get()){
            PerformanceMonitor.begin(mi.getClass().getName()+"."+mi.getMethod().getName());
            obj = super.invoke(mi);
            PerformanceMonitor.end();
        } else {
            obj = obj = super.invoke(mi);
        }
        return obj;
    }
}
