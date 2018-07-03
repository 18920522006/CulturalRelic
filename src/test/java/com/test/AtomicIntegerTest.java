package com.test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangchen
 * @date 2018/7/3 10:34
 */
public class AtomicIntegerTest implements Runnable {

    private AtomicInteger nextServerCyclicCounter;

    public AtomicIntegerTest() {
        this.nextServerCyclicCounter = new AtomicInteger(0);;
    }

    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                System.out.println(LocalDate.now() + " " + LocalTime.now() + "--" + Thread.currentThread().getName() + "-- 选择实例： " + current);
                return next;
        }
    }

    /**
     * 模拟多线程，线性选择实例
     * 摘自, Spring cloud ribbon
     * com.netflix.loadbalancer.RoundRobinRule
     * @param args
     */
    public static void main(String[] args){
        /**
         * 模拟10个连接
         */
        ExecutorService es = Executors.newFixedThreadPool(10);

        AtomicIntegerTest demo = new AtomicIntegerTest();

        for(int i = 0; i < 10; i++){
            es.execute(demo);
        }

        es.shutdown();
    }

    @Override
    public void run() {
        /**
         * 实例个数
         */
        incrementAndGetModulo(4);
    }


    /**
     * 2018-07-03 10:42:04.732 --pool-1-thread-1--  选择实例： 0
     * 2018-07-03 10:42:04.732 --pool-1-thread-2--  选择实例： 1
     * 2018-07-03 10:42:04.732 --pool-1-thread-3--  选择实例： 2
     * 2018-07-03 10:42:04.732 --pool-1-thread-4--  选择实例： 3
     * 2018-07-03 10:42:04.732 --pool-1-thread-5--  选择实例： 3
     * 2018-07-03 10:42:04.732 --pool-1-thread-6--  选择实例： 0
     * 2018-07-03 10:42:04.732 --pool-1-thread-7--  选择实例： 1
     * 2018-07-03 10:42:04.732 --pool-1-thread-8--  选择实例： 2
     * 2018-07-03 10:42:04.732 --pool-1-thread-9--  选择实例： 0
     * 2018-07-03 10:42:04.732 --pool-1-thread-10-- 选择实例： 1
     */
}
