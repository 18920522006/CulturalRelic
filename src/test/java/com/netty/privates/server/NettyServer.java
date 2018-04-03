package com.netty.privates.server;

import com.netty.privates.NettyConstant;
import com.netty.privates.codec.marshalling.decode.NettyMessageDecoder;
import com.netty.privates.codec.marshalling.encode.NettyMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wangchen
 * @date 2018/3/22 10:07
 */
public class NettyServer {

    public void run(String host, int port, NioEventLoopGroup bossGroup, NioEventLoopGroup workGroup) throws Exception {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    /**
                     * https://www.jianshu.com/p/e6f2036621f4
                     */
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
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
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(10))
                                    /**
                                     * 握手
                                     */
                                    .addLast("loginAuthHandler", new LoginAuthRespHandler())
                                    /**
                                     * 心跳
                                     */
                                    .addLast("heartBeatHandler", new HeartBeatRespHandler())
                                    /**
                                     * 文件接收
                                     */
                                    .addLast("fileTransfer", new FileUploadRepsHandler());
                        }
                    });
            ChannelFuture future = b.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
