package com.advisor;

import org.mockito.internal.matchers.Matches;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GreetingDynamicPointcut extends DynamicMethodMatcherPointcut {

    private static List<String> specialClientList = new ArrayList<>();

    static {
        specialClientList.add("张三");
        specialClientList.add("李四");
    }

    @Override
    public ClassFilter getClassFilter() {
        return new ClassFilter() {
            @Override
            public boolean matches(Class<?> aClass) {
                return Waiter.class.isAssignableFrom(aClass);
            }
        };
    }

    public boolean matches(Method method, Class<?> targetClass) {
        System.out.println("调用matches(method, targetClass)"+targetClass.getName()+"."+method.getName()+"做静态检查");
        return "greetTo".equals(method.getName());
    }

    /**
     * 对参数内容进行过滤，只对张三、李四进行增强服务。
     * @param method
     * @param targetClass
     * @param objects
     * @return
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass, Object... objects) {
        System.out.println("调用matches(method, targetClass, objects)"+targetClass.getName()+"."+method.getName()+"做动态检查");
        return specialClientList.contains(objects[0]);
    }
}
