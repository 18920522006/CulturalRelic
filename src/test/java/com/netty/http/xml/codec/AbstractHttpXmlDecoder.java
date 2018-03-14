package com.netty.http.xml.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/14 10:25
 */
public abstract class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {
    private IBindingFactory factory = null;
    private StringReader reader = null;

    /**
     * 创建 Jibx 工程类必须预设 对象类
     */
    private Class<?> clazz;
    private boolean isPrint;

    final static String CHARSET_NAME = "UTF-8";
    final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

    public AbstractHttpXmlDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    public AbstractHttpXmlDecoder(Class<?> clazz, boolean isPrint) {
        this.clazz = clazz;
        this.isPrint = isPrint;
    }

    protected Object decode0(ChannelHandlerContext ctx, ByteBuf body) throws JiBXException {
        factory = BindingDirectory.getFactory(this.clazz);
        String content = body.toString(UTF_8);
        if (isPrint) {
            System.out.println("The body is : " + content);
        }
        reader = new StringReader(content);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Object result = uctx.unmarshalDocument(reader);
        reader.close();
        reader = null;
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}
