package com.netty.serializatble.marshalling.codec;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * @author wangchen
 * @date 2018/3/20 9:33
 */
public class MarshallingCodecFactory {
    /**
     * 创建JBoss Marshalling解码器 MarshallingDecoder
     * @return
     */
    public static MarshallingDecoder buildMarshallingDecoder() {
        /**
         * 创建MarshallingFactory实例，参数“serial”表示穿件的是 java序列化工厂对象
         */
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        /**
         * 创建了MarshallingConfiguration对象，配置了版本号为5
         */
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        /**
         * 创建UnmarshallerProvider实例
         */
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        /**
         * 构建Netty的MarshallingDecoder对象，俩个参数分别为provider和单个消息序列化后的最大长度
         */
        MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024 * 1024 * 1);
        return decoder;
    }

    public static MarshallingEncoder buildMarshallingEncoder() {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }
}
