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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private Map<String, RandomAccessFile> randomAccessFiles;

    public FileUploadRepsHandler() {
        this.randomAccessFiles = new ConcurrentHashMap<>();
    }

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
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
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
                    responseFile.setRequestFile(request);
                }
                /**
                 * 需要断点续传
                 */
                else {
                    responseFile.setComplete(false);
                    responseFile.setRequestFile(request);
                    responseFile.setEndPosition(randomAccessFile.length());
                }
            }
            /**
             * 新文件
             */
            else {
                responseFile.setComplete(false);
                responseFile.setRequestFile(request);
                responseFile.setEndPosition((long) 0);
            }

            ctx.writeAndFlush(
                    NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_RESP.value(),
                            Collections.singletonMap("file","transfer"),
                            responseFile));

            randomAccessFile.close();
        }
        /**
         * 传输数据包
         */
        else if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()
                && message.getHeader().getAttachment() != null
                && "transfer".equals(message.getHeader().getAttachment().get("file"))){

            RequestFile request = (RequestFile) message.getBody();

            long fileSize = request.getFileSize();
            String fileMd5 = request.getFileMd5();
            String fileName = request.getFileName();

            File file = new File(file_dir + File.separator + fileMd5 + "_"+ fileName);

            RandomAccessFile randomAccessFile = getAccessFile(file.getPath(), file);

            randomAccessFile.seek(request.getStartPosition());
            randomAccessFile.write(request.getContent());

            ResponseFile responseFile = new ResponseFile();

            if (fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file))) {
                responseFile.setComplete(true);
                responseFile.setRequestFile(request);
                responseFile.setProgress(math(randomAccessFile.length(), fileSize));
                randomAccessFile.close();
                this.randomAccessFiles.remove(file.getPath());
            } else {
                responseFile.setComplete(false);
                responseFile.setRequestFile(request);
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
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

    /**
     * 保留两位小数
     */
    private static String math(long divisor1, long divisor2) {
        double percent = Double.parseDouble(String.valueOf(divisor1))/ Double.parseDouble(String.valueOf(divisor2));
        NumberFormat nt = NumberFormat.getPercentInstance();
        nt.setMinimumFractionDigits(0);
        String format = nt.format(percent);
        return format.substring(0, format.indexOf("%"));
    }

    private RandomAccessFile getAccessFile(String fileName, File file) throws FileNotFoundException {
        if (this.randomAccessFiles.containsKey(fileName)) {
            return this.randomAccessFiles.get(fileName);
        } else {
            RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
            this.randomAccessFiles.put(fileName, accessFile);
            return accessFile;
        }
    }
}
