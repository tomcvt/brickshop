package com.tomcvt.brickshop.controller.web;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tomcvt.brickshop.model.CheckoutSession;
import com.tomcvt.brickshop.model.SecureUserDetails;
import com.tomcvt.brickshop.service.CheckoutSessionService;


@Controller
@PreAuthorize("isAuthenticated()")
public class OrderController {
    private final CheckoutSessionService checkoutSessionService;

    public OrderController(CheckoutSessionService checkoutSessionService) {
        this.checkoutSessionService = checkoutSessionService;
    }
    @GetMapping("/order/{sessionId}")
    public String getOrderPage(@AuthenticationPrincipal SecureUserDetails userDetails, @PathVariable UUID sessionId, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        CheckoutSession session = checkoutSessionService.getSessionIfExists(sessionId);
        if (session == null || !session.getUser().getId().equals(userDetails.getId())) {
            return "redirect:/user";
        }

        model.addAttribute("sessionId", sessionId.toString());
        return "order-w-session";
    }
    
}
