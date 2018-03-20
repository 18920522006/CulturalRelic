package com.netty.serializatble.marshalling.client;

import com.netty.serializatble.marshalling.codec.MarshallingCodecFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author wangchen
 * @date 2018/3/20 10:14
 */
public class SubReqClient {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new SubReqClient().connect(port);
    }

    public void connect(int port) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(MarshallingCodecFactory.buildMarshallingDecoder())
                                    .addLast(MarshallingCodecFactory.buildMarshallingEncoder())
                                    .addLast(new SubReqClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }
}
