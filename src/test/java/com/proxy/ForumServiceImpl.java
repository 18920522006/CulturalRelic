package com.proxy;

public class ForumServiceImpl implements ForumService {

    @Override
    public void removeTopic(int topicId) {
        //PerformanceMonitor.begin("com.proxy.ForumServiceImpl.removeTopic");
        System.out.println("模拟删除Topic记录:" + topicId);
        try {
            Thread.currentThread().sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //PerformanceMonitor.end();
    }

    @Override
    public void removeForum(int forumId) {
        //PerformanceMonitor.begin("com.proxy.ForumServiceImpl.removeForum");
        System.out.println("模拟删除Forum记录:" + forumId);
        try {
            Thread.currentThread().sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //PerformanceMonitor.end();
    }
}
