package com.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

/**
 * @author wangchen
 * @date 2018/3/7 15:51
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private String url;

    /**
     * 合法的URI
     */
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    /**
     * 校验合法的文件名
     */
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            /**
             * 解码失败 400
             * 因为错误的语法导致服务器无法理解请求信息。
             */
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.getMethod() != HttpMethod.GET) {
            /**
             * 如果不是从浏览器或者表单设置为Get发起的请求
             * 方法不被允许，必须为Get请求 405
             */
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        String uri = request.getUri();
        String path = sanitizeUri(uri);
        if (path == null) {
            /**
             * 拒绝处理
             */
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        File file= new File(path);
        if (file.isHidden() || !file.exists()) {
            /**
             * 找不到文件
             */
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        if(file.isDirectory()){
            if(uri.endsWith("/")){
                    sendListing(ctx, file);
            }else{
                sendRedirect(ctx, uri+'/');
            }
            return ;
        }
        if (!file.isFile()) {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        /**
         * 读取文件内容类
         */
        RandomAccessFile randomAccessFile = null;
        try {
            /**
             * 但是在设置时需要设置模式："r": 只读、"w": 只写、"rw": 读写。
             */
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        /**
         * 消息长度
         */
        HttpHeaders.setContentLength(response, fileLength);
        /**
         * 设置返回给客户端的消息类型
         */
        setContentTypeHeader(response, file);
        /**
         * 如果客户端请求为长连接， http 1.1 默认开启长连接
         */
        if(HttpHeaders.isKeepAlive(request)){
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        /**
         * response 输出到 SocketChannel
         */
        ctx.write(response);

        /**
         *  文件传输
         */
        ChannelFuture future = ctx.write(
                /**
                 * 消息体 传输 大文件
                 */
                new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        /**
         * 监听 传输过程
         */
        future.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture channelProgressiveFuture, long progress, long total) throws Exception {
                if (total < 0){
                    /**
                     *  total 为 -1
                     */
                    System.err.println("Transfer progress:" + progress);
                } else {
                    /**
                     *  total 为 总的字节数
                     */
                    System.err.println("Transfer progress:" + progress +"/" + total);
                }
            }
            @Override
            public void operationComplete(ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                System.out.println("传递完成！");
            }
        });
        /**
         *  如果使用 chunked 编码，最后需要发送一个编码结束的空消息体，将 LastHttpContent.EMPTY_LAST_CONTENT
         *  发送到缓冲区中，标识所有的消息体已经发送完成，同时调用 flush方法将之前在发送缓冲区的消息刷新到
         *  SocketChannel中发送给客户端
         */
        ChannelFuture lastfuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        /**
         * 如果客户端发来的请求不是  keep-alive
         */
        if(!HttpHeaders.isKeepAlive(request)){
            /**
             * 传输完成，自动关闭 channel 断开http连接
             */
            lastfuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 返回错误信息， 浏览器接受
     * 消息体类型 "text/html;charset=UTF-8"
     * @param ctx
     * @param status
     */
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 消息体类型
     * @param response
     * @param file
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
    }

    /**
     * 重定向 重新刷新列表
     * @param ctx
     * @param newuri
     */
    private static void sendRedirect(ChannelHandlerContext ctx, String newuri){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, newuri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 获得完整的 URI = 项目名称 + 相对路径
     * @param uri
     * @return
     */
    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        if (!uri.startsWith(url)) {
            return null;
        }
        if (!uri.startsWith("/")) {
            return null;
        }
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.startsWith(".") || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }
        //      http://192.168.31.228:8080          /              src/test/java/com/netty/netty/
        return System.getProperty("user.dir") + File.separator + uri;
    }

    private static void sendListing(ChannelHandlerContext ctx, File dir){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append(" 目录: ");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append(" 目录 :");
        buf.append("</h3>");
        buf.append("<ul>");
        buf.append("<li>链接: <a href=\"../\">..</a></li>\r\n");
        for(File f : dir.listFiles()){
            if(f.isHidden() || !f.canRead()){
                continue;
            }
            String name = f.getName();
            if(!ALLOWED_FILE_NAME.matcher(name).matches()){
                continue;
            }
            buf.append("<li>链接:<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
