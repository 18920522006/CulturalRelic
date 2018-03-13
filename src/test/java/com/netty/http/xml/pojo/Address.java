package com.netty.http.xml.pojo;

/**
 * @author wangchen
 * @date 2018/3/9 15:49
 */
public class Address {
    /**
     * 街道1
     */
    private String street1;
    /**
     * 街道2
     */
    private String street2;
    /**
     * 城市
     */
    private String city;
    /**
     * 邮政编码
     */
    private String state;
    /**
     * 国家
     */
    private String country;

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
