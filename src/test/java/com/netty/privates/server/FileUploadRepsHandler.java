package com.netty.privates.server;

import com.netty.privates.MessageType;
import com.netty.privates.model.RequestFile;
import com.netty.privates.pojo.NettyMessage;
import com.netty.privates.util.MD5FileUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

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
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, NettyMessage message) throws Exception {
        /**
         * 收到客户端发送文件
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()) {
            log.info("接收文件 ：" + new Date().toString());
            RequestFile request = (RequestFile) message.getBody();

            byte[] content = request.getContent();
            long startPosition = request.getStartPosition();
            long endPosition = request.getEndPosition();
            String fileMd5 = request.getFileMd5();
            String fileSectionMd5 = request.getFileSectionMd5();
            String fileName = request.getFileName();
            long fileSize = request.getFileSize();

            /**
             * 文件路径
             */
            String path = file_dir + File.separator + fileMd5 + "_"+ fileName;

            log.info(path);

            File file = new File(path);

            /**
             * 文件已经存在 断点续传
             */
            if (file.exists()) {
                byte[] bytes = new byte[(int) (fileSize)];
                randomAccessFile = new RandomAccessFile(new File(path), "rw");
                randomAccessFile.seek(startPosition);
                randomAccessFile.read(bytes);
                /**
                 * 已经存在的MD5值 片段
                 */
                String md5String = MD5FileUtil.getMD5String(file);
                /**
                 * 如果不一致覆盖
                 */
                if (!fileSectionMd5.equals(md5String)) {
                    randomAccessFile.seek(startPosition);
                    randomAccessFile.write(content);
                }
            }
            /**
             * 新传入的文件
             */
            else {
                randomAccessFile = new RandomAccessFile(new File(path), "rw");
                randomAccessFile.seek(startPosition);
                randomAccessFile.write(content);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if(randomAccessFile != null ){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ctx.close();
    }
}
