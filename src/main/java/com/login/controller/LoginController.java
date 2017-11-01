package com.login.controller;

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
        ModelAndView view = new ModelAndView("/login/login");
        return view.addObject("title",service.login());
    }

}
