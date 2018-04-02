package com.netty.privates.server;

import com.netty.privates.MessageType;
import com.netty.privates.model.RequestFile;
import com.netty.privates.model.ResponseFile;
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
import java.text.NumberFormat;
import java.util.Collections;

/**
 * @author wangchen
 * @date 2018/3/28 15:15
 */
public class FileUploadRepsHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static final Logger log = LoggerFactory.getLogger(FileUploadRepsHandler.class);

    /**
     * 文件默认存储地址, 用户当前目录
     */
    private String file_dir = System.getProperty("user.dir");
    /**
     *
     */
    private RandomAccessFile randomAccessFile;

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage message) throws Exception {

        /**
         * 接收客户端询问
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()
                && message.getHeader().getAttachment() != null
                && "ask".equals(message.getHeader().getAttachment().get("file"))) {

            log.info("接收客户端询问文件位置！");

            RequestFile request = (RequestFile) message.getBody();
            String fileMd5 = request.getFileMd5();
            String fileName = request.getFileName();
            long fileSize = request.getFileSize();
            /**
             * 文件位置
             */
            File file = new File(file_dir + File.separator + fileMd5 + "_"+ fileName);
            randomAccessFile = new RandomAccessFile(file, "rw");
            /**
             * 返回客户端
             */
            ResponseFile responseFile = new ResponseFile();
            if (file.exists()) {
                boolean complete = fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file));
                /**
                 * 已经传输完毕
                 */
                if (complete) {
                    responseFile.setComplete(true);
                }
                /**
                 * 需要断点续传
                 */
                else {
                    responseFile.setComplete(false);
                    responseFile.setEndPosition(randomAccessFile.length());
                }
            }
            /**
             * 新文件
             */
            else {
                responseFile.setComplete(false);
                responseFile.setEndPosition((long) 0);
            }

            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_RESP.value(),
                            Collections.singletonMap("file","transfer"),
                            responseFile));
        }
        /**
         * 传输数据包
         */
        else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()
                && message.getHeader().getAttachment() != null
                && "transfer".equals(message.getHeader().getAttachment().get("file"))){

            log.info("开始写入文件！");

            RequestFile request = (RequestFile) message.getBody();
            long fileSize = request.getFileSize();
            String fileMd5 = request.getFileMd5();
            String fileName = request.getFileName();

            randomAccessFile.seek(request.getStartPosition());
            randomAccessFile.write(request.getContent());

            File file = new File(file_dir + File.separator + fileMd5 + "_"+ fileName);

            ResponseFile responseFile = new ResponseFile();

            if (fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file))) {
                responseFile.setComplete(true);
                responseFile.setProgress(math(randomAccessFile.length(), fileSize));
            } else {
                responseFile.setComplete(false);
                responseFile.setEndPosition(randomAccessFile.getFilePointer());
                responseFile.setProgress(math(randomAccessFile.length(), fileSize));
            }
            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_RESP.value(),
                            Collections.singletonMap("file","transfer"),
                            responseFile));

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if(randomAccessFile != null){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ctx.close();
    }

    /**
     * 保留两位小数
     */
    private static String math(long divisor1, long divisor2) {
        double percent = Double.parseDouble(String.valueOf(divisor1))/ Double.parseDouble(String.valueOf(divisor2));
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(5);
        return nt.format(percent);
    }
}
