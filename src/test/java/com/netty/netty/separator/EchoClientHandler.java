package com.netty.netty.separator;

import com.netty.serializatble.UserInfo;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;

/**
 * @author wangchen
 * @date 2018/3/2 10:58
 */
public class EchoClientHandler extends ChannelHandlerAdapter {

    private final int sendNumber = 100;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserInfo[] userInfos = UserInfo();
        for (UserInfo info : userInfos) {
            ctx.write(info);
        }
        ctx.flush();
    }

    private UserInfo[] UserInfo(){
        UserInfo[] userInfos = new UserInfo[sendNumber];
        for (int i = 0; i < sendNumber; i++ ){
            UserInfo userInfo = new UserInfo();
            userInfo.setUserID(i);
            userInfo.setUserName("ABCDEFG --->" + i);
            userInfos[i] = userInfo;
        }
        return userInfos;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessagePack msgPack = new MessagePack();
        UserInfo info = msgPack.convert((Value)msg, UserInfo.class);
        System.out.println("接收到的信息 UserName ：" + info.getUserName() + " UserID : " + info.getUserID());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
