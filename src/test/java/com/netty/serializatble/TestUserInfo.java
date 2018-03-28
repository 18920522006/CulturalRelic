package com.netty.serializatble;

import com.alibaba.fastjson.JSON;
import org.msgpack.MessagePack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author wangchen
 * @date 2018/3/2 14:14
 */
public class TestUserInfo {
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.buildUserID(100).buildUserName("Welcome to Netty");

        /**
         * stream
         */
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();

        byte[] b = bos.toByteArray();
        bos.close();

        /**
         *  MessagePack
         */
        MessagePack msgpack = new MessagePack();
        byte[] bytes = msgpack.write(userInfo);

        /**
         *  Protobuf
         */
        UserInfoReqProto.UserInfoReq.Builder builder = UserInfoReqProto.UserInfoReq.newBuilder();
        builder.setUserID(100);
        builder.setUserName("Welcome to Netty");
        UserInfoReqProto.UserInfoReq infoReq = builder.build();
        byte[] byteArray = infoReq.toByteArray();

        byte[] fastjson = JSON.toJSONBytes(userInfo);


        System.out.println("jdk 序列化对象大小为：" + b.length);
        System.out.println("--------------------------------------------");
        System.out.println("二级制编码大小为：" + userInfo.codeC().length);
        System.out.println("--------------------------------------------");
        System.out.println("MessagePack编码大小为：" + bytes.length);
        System.out.println("--------------------------------------------");
        System.out.println("Protobuf编码大小为：" + byteArray.length);
        System.out.println("--------------------------------------------");
        System.out.println("fastjson编码大小为：" + fastjson.length);
    }
}
