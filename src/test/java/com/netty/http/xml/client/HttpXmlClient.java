package com.netty.http.xml.client;

import com.netty.http.xml.codec.request.HttpXmlRequestEncoder;
import com.netty.http.xml.codec.response.HttpXmlResponseDecoder;
import com.netty.http.xml.pojo.Order;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

/**
 * @author wangchen
 * @date 2018/3/14 11:41
 */
public class HttpXmlClient  {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new HttpXmlClient().connect(port);
    }

    public void connect(int port) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /**
                                     * http 解码
                                     */
                                    .addLast("http-decoder",new HttpResponseDecoder())
                                    /**
                                     * 将多个消息转换为单一的 FullHttpRequest 或者 FullHttpResponse
                                     */
                                    .addLast("http-aggregator", new HttpObjectAggregator(65536))
                                    /**
                                     * ByteBuf --> XML --> Object
                                     */
                                    .addLast("xml-decoder", new HttpXmlResponseDecoder(Order.class, true))
                                    /**
                                     * http 编码
                                     */
                                    .addLast("http-encoder", new HttpRequestEncoder())
                                    /**
                                     * 创建 http 并且把对象转换 xml 放入 http 消息体
                                     */
                                    .addLast("xml-encoder", new HttpXmlRequestEncoder())
                                    /**
                                     * 回调执行函数
                                     */
                                    .addLast("handler", new HttpXmlClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(new InetSocketAddress(port)).sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
