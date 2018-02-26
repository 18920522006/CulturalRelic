package com.netty.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  @author wangchen
 *  @date 2018/2/26 10:25
 *  同步阻塞式 I/O 的 TimeServer
 *  服务端
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        /**
         *  ServerSocket 负责绑定IP地址和启动监听端口
         */
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            System.out.println("当前 Time Server 的启动监听端口为 ：" + port);
            /**
             * Socket 负责发起连接操作
             */
            Socket socket = null;

            /**
             * 创建线程池
             */
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);

            while (true) {
                /**
                 * 随机挑选一个请求，如果没有阻塞
                 */
                socket = serverSocket.accept();

                //new Thread(new TimeServerHandler(socket)).start();

                /**
                 * 使用线程池里代替单个线程的创建和销毁
                 */
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("关闭 ServerSocket");
        }
    }
}
