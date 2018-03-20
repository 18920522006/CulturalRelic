package com.netty.serializatble.marshalling.server;

import com.netty.serializatble.UserInfo;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wangchen
 * @date 2018/3/20 9:46
 */
public class SubReqServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UserInfo userInfo = (UserInfo) msg;
        if ("Lilinfeng".equalsIgnoreCase(userInfo.getUserName())) {
            System.out.println("Service accept client subscrib req : ["
                    + userInfo.toString() + "]");
            ctx.writeAndFlush(resp(userInfo.getUserID()));
        }
    }

    private UserInfo resp(int subReqID) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserID(subReqID);
        return userInfo;
    }
}
