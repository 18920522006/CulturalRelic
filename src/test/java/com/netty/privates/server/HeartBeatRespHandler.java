package com.netty.privates.server;

import com.netty.privates.MessageType;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wangchen
 * @date 2018/3/21 15:18
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 返回客户端发送心跳应答
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            message = buildHeartBeat();
            System.out.println("应答客户端");
            ctx.writeAndFlush(message);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(MessageType.HEARTBEAT_RESP.value());
        return  message;
    }
}
