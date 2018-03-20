package com.netty.serializatble.marshalling.client;

import com.netty.serializatble.UserInfo;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author wangchen
 * @date 2018/3/20 10:15
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private UserInfo subReq(int i) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserID(i);
        userInfo.setUserName("Lilinfeng");
        return userInfo;
    }
}
