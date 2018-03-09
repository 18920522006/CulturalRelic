package com.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author wangchen
 * @date 2018/3/7 15:30
 */
public class HttpFileServer {
    private static final String DEFAULT_URL = "/src/test/java/com/netty/";

    public void run(final int port, final String url) throws  Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /**
                                     * Http 解码器
                                     */
                                    .addLast("http-decoder", new HttpRequestDecoder())
                                    /**
                                     * 将多个消息转换为单一的 FullHttpRequest 或者 FullHttpResponse
                                     */
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    /**
                                     * Http 编码器
                                     */
                                    .addLast("http-encoder", new HttpResponseEncoder())
                                    /**
                                     * 大文件传输优化，防止内存溢出
                                     */
                                    .addLast("http-chunked", new ChunkedWriteHandler())
                                    /**
                                     * 处理业务逻辑
                                     */
                                    .addLast("fileServerHandler", new HttpFileServerHandler(url));
                        }
                    });
            ChannelFuture future = bootstrap.bind("192.168.31.228", port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址是 ：" + "http://192.168.31.228:" + port + url);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        String url = DEFAULT_URL;
        new HttpFileServer().run(port, url);
    }
}
