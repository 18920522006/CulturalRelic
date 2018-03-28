package com.netty.serializatble;

import com.alibaba.fastjson.JSON;
import org.msgpack.MessagePack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author wangchen
 * @date 2018/3/2 14:23
 */
public class PerformTestUserInfo {
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.buildUserID(100).buildUserName("Welcome to Netty");

        int loop = 1000000;

        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);

            os.writeObject(userInfo);
            os.flush();
            os.close();

            byte[] b = bos.toByteArray();
            bos.close();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("jdk 100万次 序列化对象时间为：" + (endTime - startTime) + " ms");

        System.out.println("--------------------------------------------");

        startTime = System.currentTimeMillis();

        for (int i = 0; i < loop; i++) {
            byte[] bytes = userInfo.codeC();
        }

        endTime = System.currentTimeMillis();

        System.out.println("二级制编码 100万次 时间为：" + (endTime - startTime) + " ms");

        System.out.println("--------------------------------------------");

        MessagePack msgpack = new MessagePack();

        startTime = System.currentTimeMillis();

        for (int i = 0; i < loop; i++) {
            byte[] bytes = msgpack.write(userInfo);
        }
        endTime = System.currentTimeMillis();

        System.out.println("MessagePack 100万次 时间为：" + (endTime - startTime) + " ms");

        System.out.println("--------------------------------------------");

        startTime = System.currentTimeMillis();

        UserInfoReqProto.UserInfoReq.Builder builder = UserInfoReqProto.UserInfoReq.newBuilder();
        builder.setUserID(100);
        builder.setUserName("Welcome to Netty");

        UserInfoReqProto.UserInfoReq infoReq = builder.build();

        for (int i = 0; i < loop; i++) {
            byte[] byteArray = infoReq.toByteArray();
        }
        endTime = System.currentTimeMillis();

        System.out.println("Protobuf 100万次 时间为：" + (endTime - startTime) + " ms");

        System.out.println("--------------------------------------------");

        startTime = System.currentTimeMillis();

        for (int i = 0; i < loop; i++) {
            byte[] jackson = JSON.toJSONBytes(userInfo);
        }
        endTime = System.currentTimeMillis();

        System.out.println("Jackson 100万次 时间为：" + (endTime - startTime) + " ms");


    }
}
