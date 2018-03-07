package com.netty.netty.separator;

import com.netty.serializatble.msgpack.MsgpackDecoder;
import com.netty.serializatble.msgpack.MsgpackEncoder;
import com.netty.serializatble.protobuf.SubscribeReqProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author wangchen
 * @date 2018/3/2 10:37
 */
public class EchoServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            port = Integer.valueOf(args[0]);
        }
        new EchoServer().bind(port);
    }

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    /**
                                     * 自定义分隔符 “$_”
                                     */
                                    //.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("$_".getBytes())))
                                    /**
                                     * 定长分隔符
                                     */
                                    //.addLast(new FixedLengthFrameDecoder(20))
                                    /**
                                     * 自动转为字符串
                                     */
                                    //.addLast(new StringDecoder())
                                    /**
                                     * 半包处理器
                                     * protobuf 方式
                                     */
                                     //.addLast(new ProtobufVarint32FrameDecoder())
                                     //.addLast(new ProtobufDecoder(SubscribeReqProto.SubscribeReq.getDefaultInstance()))
                                     //.addLast(new ProtobufEncoder())

                                     .addLast("frameDecode", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                     .addLast("frameEncoder", new LengthFieldPrepender(2))
                                     .addLast("msgpack decoder", new MsgpackDecoder())
                                     .addLast("msgpack encoder", new MsgpackEncoder())
                                    /*
                                     * 处理类
                                     */
                                     .addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
