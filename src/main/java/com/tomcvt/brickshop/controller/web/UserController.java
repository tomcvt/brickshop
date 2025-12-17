package com.tomcvt.brickshop.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tomcvt.brickshop.model.SecureUserDetails;

import org.springframework.ui.Model;


@Controller
@PreAuthorize("isAuthenticated()")
public class UserController {
    @GetMapping("/user")
    public String getUserpanel(@AuthenticationPrincipal SecureUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "userpanel";
    }
    @GetMapping("/user/orders/{orderId}")
    public String getOrderDetails(@AuthenticationPrincipal SecureUserDetails userDetails, Model model, @PathVariable String orderId) {
        if (userDetails == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("orderId", orderId); // Replace with actual order ID retrieval logic
        }
        return "orderdetails";
    }
    @GetMapping("/user/orders")
    public String getUserOrders(@AuthenticationPrincipal SecureUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "user-orders";
    }
    @GetMapping("/user/addresses")
    public String getUserAddresses(@AuthenticationPrincipal SecureUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "user-addresses";
    }
    
}
