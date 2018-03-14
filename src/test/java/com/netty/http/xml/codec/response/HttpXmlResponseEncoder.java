package com.netty.http.xml.codec.response;

import com.netty.http.xml.codec.AbstractHttpXmlEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/14 10:59
 */
public class HttpXmlResponseEncoder extends AbstractHttpXmlEncoder<HttpXmlResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, HttpXmlResponse msg, List<Object> out) throws Exception {
        ByteBuf body = super.encode0(ctx, msg.getResult());
        FullHttpResponse response = msg.getResponse();
        if (response == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, body);
        } else {
            response = new DefaultFullHttpResponse(msg.getResponse().getProtocolVersion(), msg.getResponse().getStatus(), body);
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/xml");
        HttpHeaders.setContentLength(response, body.readableBytes());
        out.add(response);
    }
}
