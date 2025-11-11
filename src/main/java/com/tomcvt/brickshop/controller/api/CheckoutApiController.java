package com.tomcvt.brickshop.controller.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.CheckoutDataDto;
import com.tomcvt.brickshop.model.*;
import com.tomcvt.brickshop.service.CheckoutSessionService;
import com.tomcvt.brickshop.service.OrderService;
import com.tomcvt.brickshop.session.CartModifiedFlag;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutApiController {

    private final CheckoutSessionService checkoutSessionService;
    private final OrderService orderService;
    private final CartModifiedFlag cartModifiedFlag;

    public CheckoutApiController(CheckoutSessionService checkoutSessionService, OrderService orderService, CartModifiedFlag cartModifiedFlag) {
        this.checkoutSessionService = checkoutSessionService;
        this.orderService = orderService;
        this.cartModifiedFlag = cartModifiedFlag;
    }

    @GetMapping("/create")
    public ResponseEntity<?> getActiveCheckout(@AuthenticationPrincipal WrapUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login to checkout");
        }
        CheckoutDataDto checkoutData = checkoutSessionService.getActiveCheckoutSessionForUser(userDetails.getUser());
        if (checkoutData.getUuidData() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No active cart");
        }
        checkoutData.setCartFlag(cartModifiedFlag.getUUID()); 
        return ResponseEntity.ok().body(checkoutData);
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeSessionAndCreateOrder(
            @AuthenticationPrincipal WrapUserDetails userDetails,
            @RequestBody CheckoutDataDto checkoutData) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login to checkout");
        }
        if(checkoutData.getUuidData() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No session id provided");
        }
        UUID uuid = UUID.fromString(checkoutData.getUuidData());
        CheckoutSession session = checkoutSessionService.getSessionIfExists(uuid);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid session");
        }
        if (!session.isActive()) {
            Long orderId = orderService.getOrderForSessionId(uuid).getOrderId();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Session already closed, order number: " + orderId);
        }
        if (checkoutData.getCartFlag() == null || !checkoutData.getCartFlag().equals(cartModifiedFlag.getUUID())) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Cart was modified, please review your order");
        }
        Order order = checkoutSessionService.closeSessionAndCreateOrderForUser(userDetails.getUser(), checkoutData);
        return ResponseEntity.status(HttpStatus.CREATED).body(order.getOrderId());
    }

}
