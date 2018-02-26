package com.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author wangchen
 * @date 2018/2/26 13:35
 * 同步阻塞式 I/O 的 TimeClient
 * 客户端
 */
public class TimeClient {
    public static void main(String[] args){
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        try (
            Socket socket = new Socket("127.0.0.1", port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ){
            /**
             * 查询系统时间
             */
            out.println("QUERY TIME ORDER");

            System.out.println("Send order 2 server succeed.");

            /**
             * 服务器返回的结果
             * 阻塞
             */
            String resp = in.readLine();

            System.out.println("现在时间为 ：" + resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
