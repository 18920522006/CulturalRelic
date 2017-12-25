package com.login.controller;

import com.login.model.TSystemUser;
import com.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.Clock;

@RestController
public class LoginController {

    private LoginService service;

    @Autowired
    public LoginController(LoginService service) {
        this.service = service;
    }

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("/index");
    }

    @RequestMapping("/main")
    public ModelAndView test() {
        ModelAndView modelAndView = new ModelAndView("/main/main");
        TSystemUser user = service.login();
        modelAndView.addObject("user", user);
        return modelAndView;
    }

}
