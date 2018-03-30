package com.netty.privates.client;

import com.netty.privates.MessageType;
import com.netty.privates.model.RequestFile;
import com.netty.privates.model.ResponseFile;
import com.netty.privates.pojo.Header;
import com.netty.privates.pojo.NettyMessage;
import com.netty.privates.util.MD5FileUtil;
import com.netty.privates.util.NettyMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.coyote.http2.ByteUtil;
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
            byte[] bytes = new byte[8192];

            randomAccessFile = new RandomAccessFile(request.getFile(), "r");

            int readByteSize = 0;
            while ((readByteSize = randomAccessFile.read(bytes)) != -1) {
                /**
                 * 避免由于数组长度过长导致
                 * 上传的服务端文件比本地的
                 * 文件大
                 */
                if (readByteSize < 8192) {
                    bytes = new byte[readByteSize];
                    randomAccessFile.read(bytes);
                }
                /**
                 * 传输对象
                 */
                request.setContent(bytes);
                request.setFileSize(randomAccessFile.length());
                request.setFileSectionMd5(MD5FileUtil.getMD5String(bytes));
                /**
                 * 对象属性
                 */
                NettyMessage nettyMessage = NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_REQ.value());
                nettyMessage.setBody(request);

                ctx.writeAndFlush(nettyMessage);
                /**
                 * 设定循环属性
                 */
                request.setStartPosition(request.getStartPosition() + readByteSize);
                randomAccessFile.seek(request.getStartPosition());
            }
            if (readByteSize == -1) {
                randomAccessFile.close();
            }
        }
        /**
         * 接到服务器传输回调
         */
        else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_RESP.value()) {
            ResponseFile responseFile = (ResponseFile) message.getBody();

            if (responseFile.isComplete()) {
                log.info("传输完成，关闭连接！");
                ctx.close();
            }
            /**
             * 完成百分比
             */
            String progress = responseFile.getProgress();
            log.info("progress :" + progress);
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
