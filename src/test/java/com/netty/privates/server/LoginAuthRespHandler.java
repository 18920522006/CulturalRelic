package com.netty.privates.server;

import com.netty.privates.MessageType;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchen
 * @date 2018/3/21 14:01
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {
    /**
     * 用来存已经登录的名单
     */
    private ConcurrentHashMap<String, Boolean> nodeCheck = new ConcurrentHashMap<>();
    /**
     * 白名单
     */
    private String[] whiteList = new String[]{"127.0.0.1", "192.168.1,142"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        /**
         * 处理请求的握手消息，其他消息向下传递
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            /**
             * 拒绝重复登录
             */
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
            }
            /**
             * 比对ip 返回连接标志
             */
            else {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOK = false;
                for (String WIP : whiteList) {
                    if (WIP.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }
                loginResp =  isOK ? buildResponse((byte) 0) : buildResponse((byte) -1);
                if (isOK) {
                    nodeCheck.put(nodeIndex, true);
                }
                System.out.println("握手成功");
                ctx.writeAndFlush(loginResp);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * 分布式系统 迁移至专用缓存服务
         */
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireChannelRead(cause);
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        message.setHeader(header);
        header.setType(MessageType.LOGIN_RESP.value());
        message.setBody(result);
        return message;
    }
}
