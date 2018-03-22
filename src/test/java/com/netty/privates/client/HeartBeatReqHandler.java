package com.netty.privates.client;

import com.netty.netty.TimeClient;
import com.netty.privates.MessageType;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchen
 * @date 2018/3/21 14:31
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 握手成功
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            /**
             * 每5秒向服务器 发送 Ping
             */
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);

        } else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            System.out.println("已经收到 服务端回应 心跳 ：" + new Date().toString());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireChannelRead(cause);
    }

    private class HeartBeatTask implements Runnable {

        private final ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext context) {
            this.ctx = context;
        }

        @Override
        public void run() {
            NettyMessage message = buildHeartBeat();
            System.out.println("发送心跳 ：" + new Date().toString());
            ctx.writeAndFlush(message);
        }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            message.setHeader(header);
            header.setType(MessageType.HEARTBEAT_REQ.value());
            return  message;
        }
    }
}
