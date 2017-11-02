package com.base.config;

import com.login.model.TSystemUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean(name="user")
    public TSystemUser buildUser(){
        TSystemUser user = new TSystemUser();
        user.setUsername("wangchen");
        user.setPassword("123456");
        return user;
    }
}
