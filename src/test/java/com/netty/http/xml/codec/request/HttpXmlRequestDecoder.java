package com.netty.http.xml.codec.request;

import com.netty.http.fileServer.HttpFileServerHandler;
import com.netty.http.xml.codec.AbstractHttpXmlDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/14 10:38
 */
public class HttpXmlRequestDecoder extends AbstractHttpXmlDecoder<FullHttpRequest> {
    public HttpXmlRequestDecoder(Class<?> clazz) {
        this(clazz,false);
    }

    public HttpXmlRequestDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> list) throws Exception {
        if (!request.getDecoderResult().isSuccess()) {
            HttpFileServerHandler.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        HttpXmlRequest httpXmlRequest = new HttpXmlRequest(request, super.decode0(ctx, request.content()));
        list.add(httpXmlRequest);
    }
}
