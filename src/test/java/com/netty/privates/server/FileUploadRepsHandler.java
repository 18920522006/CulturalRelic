package com.netty.privates.server;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.netty.privates.MessageType;
import com.netty.privates.model.RequestFile;
import com.netty.privates.model.ResponseFile;
import com.netty.privates.pojo.NettyMessage;
import com.netty.privates.util.MD5FileUtil;
import com.netty.privates.util.NettyMessageUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.eclipse.jdt.internal.compiler.classfmt.MethodInfoWithAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    protected void messageReceived(ChannelHandlerContext ctx, NettyMessage message) throws Exception {
        /**
         * 收到客户端发送文件
         */
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.SERVICE_REQ.value()) {
            RequestFile request = (RequestFile) message.getBody();

            byte[] content = request.getContent();
            long startPosition = request.getStartPosition();
            String fileMd5 = request.getFileMd5();
            String fileSectionMd5 = request.getFileSectionMd5();
            String fileName = request.getFileName();
            long fileSize = request.getFileSize();
            /**
             * 新文件
             */
            File file = new File(file_dir + File.separator + fileMd5 + "_"+ fileName);
            randomAccessFile = new RandomAccessFile(file, "rw");
            /**
             * 文件已经存在 断点续传
             */
            if (file.exists()) {
                byte[] bytes = new byte[8192];
                randomAccessFile.seek(startPosition);
                randomAccessFile.read(bytes);
                /**
                 * 已经存在的MD5值 片段
                 */
                String md5String = MD5FileUtil.getMD5String(bytes);
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
                randomAccessFile.seek(startPosition);
                randomAccessFile.write(content);
            }
            /**
             *  应答客户端
             */
            ResponseFile responseFile = new ResponseFile();
            /**
             * 先比较长度,每次比较MD5,需重新计算，太慢
             */
            responseFile.setComplete(fileSize == randomAccessFile.length() && fileMd5.equals(MD5FileUtil.getMD5String(file)));
            responseFile.setFileName(fileName);
            responseFile.setProgress(math(randomAccessFile.length(), fileSize));
            ctx.writeAndFlush(NettyMessageUtil.buildNettyMessage(MessageType.SERVICE_RESP.value(), responseFile));
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
