package com.netty.privates.client;

import com.netty.privates.MessageType;
import com.netty.privates.model.RequestFile;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import com.netty.privates.util.MD5FileUtil;
import com.netty.privates.util.NettyMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * @author wangchen
 * @date 2018/3/28 11:43
 */
public class FileUploadReqHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static final Logger log = LoggerFactory.getLogger(FileUploadReqHandler.class);

    private RequestFile request;

    RandomAccessFile randomAccessFile;

    FileUploadReqHandler(RequestFile request) {
        this.request = request;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        /**
         * 握手成功
         * 开始传输文件
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

            log.info("服务端已回复心跳，准备发送文件 ：" + new Date().toString());

            /**
             * 指定大小的数组
             */
            byte[] bytes = new byte[(int) request.getFile().length()];

            randomAccessFile = new RandomAccessFile(request.getFile(), "r");

            int readByteSize = 0;
            while ((readByteSize = randomAccessFile.read(bytes)) != -1) {
                request.setContent(bytes);
                request.setEndPosition(request.getStartPosition() + readByteSize);
                request.setFileSize(randomAccessFile.length());
                request.setFileSectionMd5(MD5FileUtil.getMD5String(bytes));

                NettyMessage nettyMessage = NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_REQ.value());
                nettyMessage.setBody(request);

                ctx.writeAndFlush(nettyMessage);
                randomAccessFile.seek(request.getStartPosition() + readByteSize);
            }
            if (readByteSize == -1) {
                randomAccessFile.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ctx.close();
    }
}
