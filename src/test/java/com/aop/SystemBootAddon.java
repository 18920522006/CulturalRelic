package com.aop;

import org.springframework.core.Ordered;

public interface SystemBootAddon extends Ordered {
    //系统就绪后调用
    void onReady();
}
