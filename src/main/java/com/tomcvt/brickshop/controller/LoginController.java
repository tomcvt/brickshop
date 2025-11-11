package com.tomcvt.brickshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {
    @GetMapping("/login")
    public String getLogin() {
        return "forward:login.html";
    }
    @GetMapping("/registration")
    public String getRegistration() {
        return "registration";
    }
}
