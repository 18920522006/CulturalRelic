package com.netty.http.xml.codec.response;

import com.netty.http.xml.codec.AbstractHttpXmlDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/14 11:09
 */
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

    public HttpXmlResponseDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out) throws Exception {
        HttpXmlResponse httpXmlResponse = new HttpXmlResponse(msg, super.decode0(ctx, msg.content()));
        out.add(httpXmlResponse);
    }
}
