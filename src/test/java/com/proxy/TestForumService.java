package com.proxy;

import org.junit.Test;

import java.lang.reflect.Proxy;

public class TestForumService {
    public static void main(String[] args){
        ForumService service = new ForumServiceImpl();
        service.removeTopic(10);
        service.removeForum(1012);
    }

    @Test
    public void proxy() {
        ForumService target = new ForumServiceImpl();
        PerformanceHandler handler = new PerformanceHandler(target);
        ForumService proxy = (ForumService)Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                handler
        );
        proxy.removeForum(10);
        proxy.removeTopic(1012);
    }
}
