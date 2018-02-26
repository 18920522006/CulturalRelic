package com.netty.bio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchen
 * @date 2018/2/26 14:19
 *
 *  伪异步 I/O
 *  控制了创建线程的数量，但无法解决根本，互相等待的问题。
 */
public class TimeServerHandlerExecutePool {

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        executor = new ThreadPoolExecutor(
                //获取cpu核心数
                Runtime.getRuntime().availableProcessors(),
                //线程池容量
                maxPoolSize,
                //超时时间
                120L,
                //计数单位
                TimeUnit.SECONDS,
                //排队的请求放入队列中
                new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }
}
