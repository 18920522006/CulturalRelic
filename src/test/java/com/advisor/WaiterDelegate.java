package com.advisor;

public class WaiterDelegate {

    private Waiter waiter;

    public void setWaiter(Waiter waiter) {
        this.waiter = waiter;
    }

    public void service(String clientName) {
        waiter.greetTo(clientName);
        waiter.serveTo(clientName);
    }
}
