package com.advisor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.ControlFlowPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class GreetingComposablePointcut {
    public Pointcut getIntersectionPointcut() {
        ComposablePointcut pointcut = new ComposablePointcut();
        Pointcut pointcut1 = new ControlFlowPointcut(WaiterDelegate.class, "service");
        NameMatchMethodPointcut pointcut2 = new NameMatchMethodPointcut();
        pointcut2.addMethodName("greetTo");
        return pointcut.intersection(pointcut1).intersection((Pointcut)pointcut2);
    }
}
