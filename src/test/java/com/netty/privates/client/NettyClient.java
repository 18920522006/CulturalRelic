package com.netty.privates.client;

import com.netty.privates.NettyConstant;
import com.netty.privates.codec.marshalling.decode.NettyMessageDecoder;
import com.netty.privates.codec.marshalling.encode.NettyMessageEncoder;
import com.netty.privates.util.ObjectConvertUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchen
 * @date 2018/3/22 9:33
 */
public class NettyClient {

    public static void main(String[] args) throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.setHost(NettyConstant.REMOTE_IP);
        nettyClient.setPort(NettyConstant.LOCAL_PORT);
        nettyClient.setFile(new File("C:\\Users\\wangchen\\Downloads\\cs1_6_Setup.exe"));
        nettyClient.connect();
    }

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private int port;

    private String host;

    private File file;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void connect() throws Exception {
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
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(10))
                                    /**
                                     * 握手
                                     */
                                    .addLast("loginAuthHandler", new LoginAuthReqHandler())
                                    /**
                                     * 心跳
                                     */
                                    .addLast("heartBeatHandler", new HeartBeatReqHandler())
                                    /**
                                     * 传输文件
                                     */
                                    .addLast("fileHandler", new FileUploadReqHandler(ObjectConvertUtil.convert(file)));
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

                    log.info("链路连接中断，开始重新连接！" + new Date().toString());

                    try {
                        /**
                         * 每5秒钟重连一次
                         */
                        TimeUnit.SECONDS.sleep(5);
                        /**
                         * 更换地址
                         */
                        connect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
