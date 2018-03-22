package com.netty.privates.client;

import com.netty.privates.NettyConstant;
import com.netty.privates.codec.decode.NettyMessageDecoder;
import com.netty.privates.codec.encode.NettyMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchen
 * @date 2018/3/22 9:33
 */
public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void connect(int port,String host) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    /**
                     * TCP/IP协议中针对TCP默认开启了Nagle算法。Nagle算法通过减少需要传输的数据包，来优化网络。
                     * 启动TCP_NODELAY，就意味着禁用了Nagle算法，允许小包的发送。对于延时敏感型，同时数据传输量比较小的应用，
                     * 开启TCP_NODELAY选项无疑是一个正确的选择。比如，对于SSH会话，
                     * 用户在远程敲击键盘发出指令的速度相对于网络带宽能力来说，绝对不是在一个量级上的，所以数据传输非常少；
                     * 而又要求用户的输入能够及时获得返回，有较低的延时。如果开启了Nagle算法，就很可能出现频繁的延时，导致用户体验极差。
                     */
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /**
                                     * 解码
                                     */
                                    .addLast(new NettyMessageDecoder(1024 * 1024, 4,4))
                                    /**
                                     * 编码
                                     */
                                    .addLast("MessageEncoder", new NettyMessageEncoder())
                                    /**
                                     * 50秒内没有读取到对方任何信息，需要主动关闭链路
                                     */
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                    /**
                                     * 握手
                                     */
                                    .addLast("loginAuthHandler", new LoginAuthReqHandler())
                                    /**
                                     * 心跳
                                     */
                                    .addLast("heartBeatHandler", new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture future = b.connect(host,port).sync();
            future.channel().closeFuture().sync();
        } finally {
            /**
             * 释放资源后，清空资源，再次发起重连操作
             */
            executor.execute(new Runnable() {
                @Override
                public void run() {

                    System.out.println("链路连接中断，开始重新连接！");

                    try {
                        /**
                         * 每5秒钟重连一次
                         */
                        TimeUnit.SECONDS.sleep(5);
                        /**
                         * 更换地址
                         */
                        connect(NettyConstant.LOCAL_PORT, NettyConstant.REMOTE_IP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.PORT, NettyConstant.LOCAL_IP);
    }
}
