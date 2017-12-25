package com.login.service;

import com.login.mapper.TSystemUserMapper;
import com.login.model.TSystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private TSystemUserMapper userMapper;

    @Autowired
    public LoginService(TSystemUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public TSystemUser login(){
        return userMapper.selectAll().get(0);
    }

}
