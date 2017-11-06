package com.beanfactory;

import com.entity.User;
import org.springframework.beans.factory.FactoryBean;

public class UserFactoryBean implements FactoryBean<User> {

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public User getObject() throws Exception {
        User user = new User();
        user.setUsername(this.username);
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
