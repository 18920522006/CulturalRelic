package com.netty.http.xml.client;

import com.netty.http.xml.codec.request.HttpXmlRequest;
import com.netty.http.xml.codec.response.HttpXmlResponse;
import com.netty.http.xml.pojo.Address;
import com.netty.http.xml.pojo.Customer;
import com.netty.http.xml.pojo.Order;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangchen
 * @date 2018/3/14 14:05
 */
public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Order order = new Order();
        order.setOrderNumber(123);
        order.setTotal((float) 9999.999);

        Customer customer = new Customer();
        order.setCustomer(customer);
        customer.setFirstName("李");
        customer.setLastName("林峰");
        customer.setCustomerNumber(123);

        Address address = new Address();
        order.setBillTo(address);
        order.setShipTo(address);
        address.setStreet1("龙眠大道");
        address.setCity("南京市");
        address.setState("江苏省");
        address.setCountry("中国");

        HttpXmlRequest request = new HttpXmlRequest(null, order);
        ctx.writeAndFlush(request);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpXmlResponse response) throws Exception {
        System.out.println("client 接收到的对象 : " + response.getResult().toString()) ;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
