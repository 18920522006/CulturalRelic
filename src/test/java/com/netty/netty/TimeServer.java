package com.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author wangchen
 * @date 2018/2/28 14:07
 */
public class TimeServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new TimeServer().bind(port);
    }

    public void bind(int port) throws Exception {
        /**
         * 配置服务端的 NIO 线程组
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * 启动辅助类
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new childChannelHandler());

            System.out.println("当前 Time Server 的启动监听端口为 ：" + port);

            /**
             * 绑定端口，同步等待成功
             */
            ChannelFuture future = b.bind(port).sync();
            /**
             * 等待服务端监听端口关闭
             */
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class childChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            /**
             *  LineBasedFrameDecoder 遍历接收到的 ByteBuf中字节， 若果有换行，以此结束。
             *  如果没有继续查找，单行匹配的最大长度为 1024.
             *  如果扔没有匹配到，抛出异常，同事忽略之前的异常码流。
             */
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            /**
             *  StringDecoder 把接收到的对象，转换成字符串。继续向下传播。
             */
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }
}


