package com.tomcvt.brickshop.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.tomcvt.brickshop.model.WrapUserDetails;

@Controller
public class CheckoutController {
    @GetMapping("/checkout")
    public String getCheckoutPage(@AuthenticationPrincipal WrapUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "checkout";
    }
}
