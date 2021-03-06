package com.netty.aio;

/**
 * @author wangchen
 * @date 2018/2/28 10:45
 */
public class TimeClient {
    public static void main(String[] args){
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
