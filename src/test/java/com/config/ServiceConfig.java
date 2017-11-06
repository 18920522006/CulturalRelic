package com.config;

import com.entity.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Autowired
    private DaoConfig daoConfig;

    @Bean("LoginService1")
    public LoginService loginService() {
        LoginService loginService = new LoginService();
        loginService.setUserDao(daoConfig.userDao());
        loginService.setLoginDao(daoConfig.loginDao());
        return loginService;
    }
}
