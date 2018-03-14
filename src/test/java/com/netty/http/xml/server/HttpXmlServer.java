package com.netty.http.xml.server;

import com.netty.http.xml.codec.request.HttpXmlRequestDecoder;
import com.netty.http.xml.codec.request.HttpXmlRequestEncoder;
import com.netty.http.xml.codec.response.HttpXmlResponseDecoder;
import com.netty.http.xml.codec.response.HttpXmlResponseEncoder;
import com.netty.http.xml.pojo.Order;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;

/**
 * @author wangchen
 * @date 2018/3/14 14:10
 */
public class HttpXmlServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new HttpXmlServer().run(port);
    }

    public void run(int port) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /**
                                     * 解码 client 发来的 http
                                     */
                                    .addLast("request-http", new HttpRequestDecoder())
                                    /**
                                     * 将多个消息转换为单一的 FullHttpRequest 或者 FullHttpResponse
                                     */
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    /**
                                     * 转换 http --> xml --> object
                                     */
                                    .addLast("request-http-xml", new HttpXmlRequestDecoder(Order.class, true))
                                    /**
                                     * 编码 http 发送 client
                                     */
                                    .addLast("response-http", new HttpResponseEncoder())
                                    /**
                                     * 编码 object --> xml --> http
                                     */
                                    .addLast("response-http-xml", new HttpXmlResponseEncoder())
                                    /**
                                     * handler 函数
                                     */
                                    .addLast("handler", new HttpXmlServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(new InetSocketAddress(port)).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
