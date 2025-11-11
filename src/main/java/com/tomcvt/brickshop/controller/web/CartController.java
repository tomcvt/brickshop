package com.tomcvt.brickshop.controller.web;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {
    
    @GetMapping("/cart")
    public String showCartPage() {
        return "cart-details";
    }
}
