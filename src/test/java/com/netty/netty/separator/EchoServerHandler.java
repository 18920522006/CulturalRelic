package com.netty.netty.separator;

import com.netty.serializatble.UserInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;

import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/2 10:49
 */
public class EchoServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * 由于 messagePack.read(bytes) 没有指定解码类型
         * 接受时需要指定类型，进行转化
         */
        MessagePack msgPack = new MessagePack();
        UserInfo info = msgPack.convert((Value)msg, UserInfo.class);
        System.out.println("接收到的信息 UserName ：" + info.getUserName() + " UserID : " + info.getUserID());        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
