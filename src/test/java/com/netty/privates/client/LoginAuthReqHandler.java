package com.netty.privates.client;

import com.netty.privates.MessageType;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wangchen
 * @date 2018/3/21 13:44
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
        System.out.println("请求握手");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 是否为握手应答消息
         */
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            /**
             * 约定如果捂手成功为0 否则失败 关闭链路
             */
            if (loginResult != (byte) 0) {
                ctx.close();
            }
            ctx.fireChannelRead(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireChannelRead(cause);
    }

    /**
     * 请求握手消息
     * @return
     */
    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(MessageType.LOGIN_REQ.value());
        return message;
    }
}
