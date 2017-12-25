package com.login.model;

import javax.persistence.*;

@Table(name = "t_system_user")
public class TSystemUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String loginname;

    private String password;

    private String state;

    private String username;

    private String orderno;

    private Integer ifleader;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return loginname
     */
    public String getLoginname() {
        return loginname;
    }

    /**
     * @param loginname
     */
    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return orderno
     */
    public String getOrderno() {
        return orderno;
    }

    /**
     * @param orderno
     */
    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    /**
     * @return ifleader
     */
    public Integer getIfleader() {
        return ifleader;
    }

    /**
     * @param ifleader
     */
    public void setIfleader(Integer ifleader) {
        this.ifleader = ifleader;
    }
}