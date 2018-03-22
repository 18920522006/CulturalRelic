package com.netty.privates.codec.encode;

import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/20 8:59
 * 消息编码器
 */
public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage msg, ByteBuf sendBuf) throws Exception {
        Header header = msg.getHeader();
        if (msg == null || header == null) {
            throw new Exception("The encode message is null");
        }
        sendBuf.writeInt(header.getCrcCode());
        sendBuf.writeInt(header.getLength());
        sendBuf.writeLong(header.getSessionID());
        sendBuf.writeByte(header.getType());
        sendBuf.writeByte(header.getPriority());
        /**
         * 记录附件的数量，解码时通过数量来循环取值
         */
        sendBuf.writeInt(header.getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;

        for (Iterator<String> it = header.getAttachment().keySet().iterator(); it.hasNext(); ){
            key = it.next();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = header.getAttachment().get(key);
            /**
             * 使用 Jboss Marshalling 序列化 value
             * 加入到 sendBuf 中
             */
            marshallingEncoder.encode(value, sendBuf);
        }

        key = null;
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        } else {
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
    }
}
