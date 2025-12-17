package com.tomcvt.brickshop.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.tomcvt.brickshop.model.SecureUserDetails;

@Controller
@PreAuthorize("isAuthenticated()")
public class CheckoutController {
    @GetMapping("/checkout")
    public String getCheckoutPage(@AuthenticationPrincipal SecureUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return "checkout";
    }
}
