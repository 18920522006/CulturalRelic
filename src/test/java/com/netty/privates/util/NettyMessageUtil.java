package com.netty.privates.util;

import com.netty.privates.MessageType;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;

/**
 * @author wangchen
 * @date 2018/3/29 10:19
 */
public class NettyMessageUtil {
    public static NettyMessage buildNettyMessage(byte type) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(type);
        return  message;
    }
    public static NettyMessage buildNettyMessage(byte type, Object body) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(type);
        message.setBody(body);
        return  message;
    }
}
