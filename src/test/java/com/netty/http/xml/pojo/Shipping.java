package com.netty.http.xml.pojo;

/**
 * @author wangchen
 * @date 2018/3/9 15:57
 */
public enum Shipping {
    /**
     * 普通邮寄
     */
    STANDARD_MAIL,
    /**
     * 宅急送
     */
    PRIORITY_MAIL,
    /**
     * 国际邮递
     */
    INTERNATIONAL_MAIL,
    /**
     * 国内快递
     */
    DOMESTIC_EXPRESS,
    /**
     * 国际快读
     */
    INTERNATIONAL_EXPRESS;
}
