package com.netty.serializatble.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangchen
 * @date 2018/3/5 15:51
 */
public class TestSubscribeReq {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("Lilinfeng");
        builder.setProductName("Netty Book");
        List<String> address = new ArrayList<>();
        address.add("NanJing YuHuaTai");
        address.add("Beijing LiuLiChang");
        address.add("ShenZhen HongShuLin");
        builder.addAllAddress(address);
        return builder.build();
    }

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("编码前 ：" + req.toString());
        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("解码后 ：" + req.toString());
        System.out.println("比较结果 ：" + req2.equals(req));
    }
}
