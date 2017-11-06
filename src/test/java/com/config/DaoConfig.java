package com.config;

import com.entity.LoginDao;
import com.entity.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DaoConfig {

    @Bean
    public UserDao userDao() {
        return new UserDao();
    }

    @Bean
    public LoginDao loginDao() {
        return new LoginDao();
    }
}
