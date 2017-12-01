package com.advisor;

public class Seller {
    public void greetTo(String name) {
        System.out.println("seller greet to "+name+"...");
    }
    public void serveTo(String name) {
        System.out.println("seller serving "+name+"...");
        helloTo(name);
    }

    public void helloTo(String name){
        System.out.println("seller hello "+name+"...");
    }
}
