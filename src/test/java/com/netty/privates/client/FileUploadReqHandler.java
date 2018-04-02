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
import java.io.FileNotFoundException;
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
         * 验证文件
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

            log.info("询问文件是否在服务器存在！");

            /**
             * 询问服务器是否有文件，或文件需要 断点续传
             */
            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_REQ.value(),
                            Collections.singletonMap("file", "ask"),
                            request));
        }
        /**
         * 开始读取数据包
         */
        else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_RESP.value()
                && message.getHeader().getAttachment() != null
                && "transfer".equals(message.getHeader().getAttachment().get("file"))) {

            ResponseFile responseFile = (ResponseFile) message.getBody();
            boolean complete = responseFile.isComplete();

            /**
             * 文件已经存在
             */
            if (complete) {
                log.info("文件已经存在服务器！");
                ctx.close();
            } else {
                request.setStartPosition(responseFile.getEndPosition());
                log.info("百分比：" + responseFile.getProgress());
                read0(ctx);
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

    private void read0(ChannelHandlerContext ctx) throws IOException {
        byte[] bytes = new byte[8192];

        randomAccessFile = new RandomAccessFile(request.getFile(), "r");
        randomAccessFile.seek(request.getStartPosition());

        int readByteSize = 0;
        if ((readByteSize = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - request.getStartPosition()) > 0) {
            if (readByteSize < 8192) {
                bytes = new byte[readByteSize];
                randomAccessFile.read(bytes);
            }
            /**
             * 传输对象
             */
            request.setContent(bytes);
            request.setFileSize(randomAccessFile.length());

            /**
             * 传输到服务器
             */
            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_REQ.value(),
                            Collections.singletonMap("file", "transfer"),
                            request));
        } else {
            randomAccessFile.close();
            ctx.close();
        }
    }
}
