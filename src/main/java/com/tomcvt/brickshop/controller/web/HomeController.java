package com.tomcvt.brickshop.controller.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tomcvt.brickshop.model.WrapUserDetails;


@Controller
public class HomeController {

    private static final List<String> ADMIN_ROLES = List.of("ROLE_ADMIN", "ROLE_SUPERUSER", "ROLE_MODERATOR");
    private static final List<String> PACKER_ROLES = List.of("ROLE_PACKER", "ROLE_ADMIN", "ROLE_SUPERUSER", "ROLE_MODERATOR");

    @GetMapping("/")
    public String getHome(@AuthenticationPrincipal WrapUserDetails userDetails, Model model) {
        model.addAttribute("isPacker", userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(auth -> PACKER_ROLES.contains(auth.getAuthority())));
        model.addAttribute("isAdmin", userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(auth -> ADMIN_ROLES.contains(auth.getAuthority())));
        model.addAttribute("isSuperuser", userDetails != null && userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPERUSER")));
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        } else {
            model.addAttribute("username", "Guest");
        }
        return "index";
    }
}
