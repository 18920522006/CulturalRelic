package com.entity;

import com.config.DaoConfig;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

public class LoginService {

    private LoginDao loginDao;

    private UserDao userDao;

    public LoginDao getLoginDao() {
        return loginDao;
    }

    public void setLoginDao(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
