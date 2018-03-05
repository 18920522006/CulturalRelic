package com.netty.serializatble;

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

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();

        byte[] b = bos.toByteArray();
        bos.close();

        MessagePack msgpack = new MessagePack();
        byte[] bytes = msgpack.write(userInfo);

        System.out.println("jdk 序列化对象大小为：" + b.length);
        System.out.println("--------------------------------------------");
        System.out.println("二级制编码大小为：" + userInfo.codeC().length);
        System.out.println("--------------------------------------------");
        System.out.println("MessagePack编码大小为：" + bytes.length);
    }
}