package com.netty.netty.separator;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wangchen
 * @date 2018/3/2 10:49
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    private int count;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("接收到的信息 ：" + body + " 计数 ：" + ++count);

        //给客户端设置解码标记
        body += "$_";
        ctx.writeAndFlush(Unpooled.copiedBuffer(body.getBytes()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
