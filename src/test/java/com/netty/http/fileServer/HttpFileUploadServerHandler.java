package com.netty.http.fileServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.*;
import org.apache.tools.ant.types.CommandlineJava;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/19 14:07
 */
public class HttpFileUploadServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private HttpHeaders headers;
    private HttpRequest request0;

    private HttpPostRequestDecoder decoder;

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {

        if (HttpMethod.POST.equals(request.getMethod())) {
            request0 = request;
            headers = request.headers();

            /**
             * 文件上传类型
             */
            if (getContentType().equals(HttpHeaders.Values.MULTIPART_FORM_DATA.toString())) {
                initPostRequestDecoder();
                try {
                    List<InterfaceHttpData> datas = decoder.getBodyHttpDatas();
                    for (InterfaceHttpData data : datas) {
                        writeHttpData(data);
                    }
                } catch (Exception e) {
                    //此处仅简单抛出异常至上一层捕获处理，可自定义处理
                    throw new Exception(e);
                }

            }
        }
    }

    /**
     * 获得内容类型
     * application/json
     * application/x-www-form-urlencoded
     * multipart/form-data
     * @return
     */
    private String getContentType(){
        String typeStr = headers.get(HttpHeaders.Names.CONTENT_TYPE.toString());
        String[] list = typeStr.split(";");
        return list[0];
    }

    /**
     * 初始化
     */
    private void initPostRequestDecoder(){
        if (decoder != null) {
            decoder.cleanFiles();
            decoder = null;
        }
        decoder = new HttpPostRequestDecoder(factory, request0, Charset.forName("UTF-8"));
    }

    private void writeHttpData(InterfaceHttpData data) throws Exception{
        //后续会加上块传输（HttpChunk），目前仅简单处理
        if(data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            String fileName = fileUpload.getFilename();
            if(fileUpload.isCompleted()) {
                //保存到磁盘
                StringBuffer fileNameBuf = new StringBuffer();
                fileNameBuf.append(DiskFileUpload.baseDirectory).append(fileName);
                fileUpload.renameTo(new File(fileNameBuf.toString()));
            }
        }
    }
}
