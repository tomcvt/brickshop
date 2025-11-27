package com.tomcvt.brickshop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tomcvt.brickshop.model.WrapUserDetails;


@Controller
public class HomeController {
    @GetMapping("/")
    public String getHome(@AuthenticationPrincipal WrapUserDetails userDetails, Model model) {
        model.addAttribute("isAdmin", userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        } else {
            model.addAttribute("username", "Guest");
        }
        return "index";
    }
}
