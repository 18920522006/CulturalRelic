package com.proxy;

import org.junit.Test;

import java.lang.reflect.Proxy;

public class TestForumService {

    public static void main(String[] args){
        ForumService service = new ForumServiceImpl();
        service.removeTopic(10);
        service.removeForum(1012);
    }

    /**
     * jdk 动态代理
     */
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

    /**
     *  cglib 动态代理
     */
    public void proxy_() {
        CglibProxy proxy = new CglibProxy();
        ForumServiceImpl forumService =
                (ForumServiceImpl)proxy.getProxy(ForumServiceImpl.class);
        forumService.removeForum(10);
        forumService.removeTopic(1012);
    }
}
