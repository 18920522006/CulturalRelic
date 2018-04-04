package com.netty.privates.client;

import com.netty.privates.MessageType;
import com.netty.privates.frame.JProgressBarPanel;
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

import javax.sound.sampled.Line;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangchen
 * @date 2018/3/28 11:43
 */
public class FileUploadReqHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static final Logger log = LoggerFactory.getLogger(FileUploadReqHandler.class);

    JProgressBarPanel panel;

    private Map<String, RequestFile> requests;

    private Map<String, RandomAccessFile> randomAccessFiles;

    FileUploadReqHandler(Map<String, RequestFile> requests, JProgressBarPanel panel) {
        this.panel = panel;
        this.requests = requests;
        this.randomAccessFiles = new ConcurrentHashMap<>();
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

            for (Iterator<RequestFile> it = requests.values().iterator(); it.hasNext(); ){
                /**
                 * 询问服务器是否有文件，或文件需要 断点续传
                 */
                ctx.writeAndFlush(
                        NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_REQ.value(),
                                Collections.singletonMap("file", "ask"),
                                it.next()));
            }
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
                log.info("文件传输完毕！");
                String path = responseFile.getRequestFile().getFile().getPath();
                RandomAccessFile accessFile = this.randomAccessFiles.get(path);
                if (accessFile != null) {
                    accessFile.close();
                    this.randomAccessFiles.remove(path);
                }
            } else {
                RequestFile requestFile = responseFile.getRequestFile();
                requestFile.setStartPosition(responseFile.getEndPosition());
                /**
                 * 绘制百分比
                 */
                log.info(requestFile.getFileName() + " 百分比："  +  responseFile.getProgress());
                panel.progress(panel, requestFile.getFile(), responseFile.getProgress());
                read0(ctx, requestFile);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (this.randomAccessFiles.size() > 0) {
            for (Iterator<RandomAccessFile> it = this.randomAccessFiles.values().iterator(); it.hasNext();) {
                RandomAccessFile next = it.next();
                if (next != null) {
                    next.close();
                }
            }
        }
        ctx.close();
    }

    private void read0(ChannelHandlerContext ctx, RequestFile request) throws IOException {
        byte[] bytes = null;

        RandomAccessFile randomAccessFile = getAccessFile(request.getFile().getPath(), request.getFile());
        randomAccessFile.seek(request.getStartPosition());

        int capacity = (int) (randomAccessFile.length() - request.getStartPosition());

        /**
         * 计算剩余量
         */
        if (capacity < 8192) {
            bytes = new byte[capacity];
        } else {
            bytes = new byte[8192];
        }

        int readByteSize = 0;
        if ((readByteSize = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - request.getStartPosition()) > 0) {

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
        }
    }

    /**
     * 获取操作类
     * @param fileName
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    private RandomAccessFile getAccessFile(String fileName, File file) throws FileNotFoundException {
        if (this.randomAccessFiles.containsKey(fileName)) {
            return this.randomAccessFiles.get(fileName);
        } else {
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");
            this.randomAccessFiles.put(fileName, accessFile);
            return accessFile;
        }
    }
}
