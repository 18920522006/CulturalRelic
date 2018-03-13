package com.netty.http.xml.tools;

import com.netty.http.xml.pojo.Address;
import com.netty.http.xml.pojo.Customer;
import com.netty.http.xml.pojo.Order;
import org.jibx.runtime.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author wangchen
 * @date 2018/3/13 13:35
 */
public class TestOrder {
    private IBindingFactory factory = null;
    private StringWriter writer = null;
    private StringReader reader = null;
    private static final String CHARSET_NAME = "UTF-8";

    private String encode2Xml(Order order) throws JiBXException, IOException {
        factory = BindingDirectory.getFactory(Order.class);
        writer = new StringWriter();
        IMarshallingContext mctx = factory.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(order, CHARSET_NAME, null, writer);
        String xmlStr = writer.toString();
        writer.close();
        System.out.println(xmlStr.toString());
        return xmlStr;
    }

    private Order decode2Order(String xmlBody) throws JiBXException {
        reader = new StringReader(xmlBody);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Order order = (Order) uctx.unmarshalDocument(reader);
        return order;
    }
    
    public static void main(String[] args) throws JiBXException, IOException {
        TestOrder test = new TestOrder();
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

        String body = test.encode2Xml(order);
        Order order2 = test.decode2Order(body);
        System.out.println(order2.toString());
    }
}
