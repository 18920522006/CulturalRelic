package com.aop;

public interface BeanSelfProxyAware<T> {
    //织入自身代理类接口
    void setSelfProxy(T t);
}
