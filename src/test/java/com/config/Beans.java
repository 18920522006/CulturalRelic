package com.config;

import com.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean(name="user",initMethod = "myInit",destroyMethod = "myDestroy")
    public User buildUser(){
        User user = new User();
        user.setUsername("wangchen");
        return user;
    }
}
