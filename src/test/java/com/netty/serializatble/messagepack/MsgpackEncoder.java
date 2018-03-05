package com.netty.serializatble.messagepack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @author wangchen
 * @date 2018/3/5 9:59
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack messagePack = new MessagePack();
        /**
         * 使用 MessagePack 编码对象
         */
        byte[] write = messagePack.write(o);
        /**
         * 写入到 byteBuf 中
         */
        byteBuf.writeBytes(write);
    }
}
