package com.advisor;

import com.aop.BeanSelfProxyAware;
import org.springframework.stereotype.Component;

public class Seller implements BeanSelfProxyAware<Seller> {

    private Seller seller;

    @Override
    public void setSelfProxy(Seller seller) {
        this.seller = seller;
    }

    public void greetTo(String name) {
        System.out.println("seller greet to "+name+"...");
    }
    public void serveTo(String name) {
        System.out.println("seller serving "+name+"...");
        seller.helloTo(name);
    }
    public void helloTo(String name){
        System.out.println("seller hello "+name+"...");
    }
}
