package com.tomcvt.brickshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AddresspanelController {
    @GetMapping("/addresspanel")
    public String getAddresspanel() {
        return "addresspanel";
    }
    
}
