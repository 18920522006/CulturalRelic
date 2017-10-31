package com.login.model;

import java.math.BigDecimal;
import javax.persistence.*;

@Table(name = "T_SYSTEM_USER")
public class TSystemUser {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigDecimal id;

    @Column(name = "LOGINNAME")
    private String loginname;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "STATE")
    private String state;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "ORDERNO")
    private String orderno;

    @Column(name = "IFLEADER")
    private BigDecimal ifleader;

    /**
     * @return ID
     */
    public BigDecimal getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(BigDecimal id) {
        this.id = id;
    }

    /**
     * @return LOGINNAME
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
     * @return PASSWORD
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
     * @return STATE
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
     * @return USERNAME
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
     * @return ORDERNO
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
     * @return IFLEADER
     */
    public BigDecimal getIfleader() {
        return ifleader;
    }

    /**
     * @param ifleader
     */
    public void setIfleader(BigDecimal ifleader) {
        this.ifleader = ifleader;
    }
}