package com.netty.aio;

/**
 * @author wangchen
 * @date 2018/2/28 9:35
 */
public class TimeServer {
    public static void main(String[] args){
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(port);

        new Thread(asyncTimeServerHandler, "AIO-AsyncTimeServerHandler-001").start();
    }
}
