package com.tomcvt.brickshop.controller.api;

import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.brickshop.dto.CustomerOrderDto;
import com.tomcvt.brickshop.dto.ErrorResponse;
import com.tomcvt.brickshop.dto.TextResponse;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.model.SecureUserDetails;
import com.tomcvt.brickshop.service.OrderService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("isAuthenticated()")
public class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/withsession/{sessionId}")
    public ResponseEntity<CustomerOrderDto> getCustomerOrderDtoBySessionId(
            @AuthenticationPrincipal SecureUserDetails userDetails, @PathVariable UUID sessionId) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        User user = userDetails.getUser();
        CustomerOrderDto cod = orderService.getCustomerOrderDtoForSessionId(sessionId, user);
        return ResponseEntity.ok().body(cod);
    }

    //TODO is this needed?
    @GetMapping("/{orderId}")
    public ResponseEntity<CustomerOrderDto> getCustomerOrderDtoByOrderId(
            @AuthenticationPrincipal SecureUserDetails userDetails, @PathVariable Long orderId) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        User user = userDetails.getUser();
        CustomerOrderDto cod = orderService.getOrderByOrderIdAndUser(orderId, user).toCustomerOrderDto();
        return ResponseEntity.ok().body(cod);
    }
    @GetMapping("/payment/token")
    public ResponseEntity<String> getPaymentToken(
            @AuthenticationPrincipal SecureUserDetails userDetails, @RequestParam Long orderId) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        String token = orderService.getPaymentTokenForOrder(orderId, userDetails.getUser());
        return ResponseEntity.ok().body(token);
    }
    @GetMapping("/payment/verify")
    public ResponseEntity<?> payForOrder(
            @AuthenticationPrincipal SecureUserDetails userDetails, @RequestParam Long orderId) {
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        User user = userDetails.getUser();

        //TODO handle different behaviours based on result
        //TODO return json with status, new transaction id etc.
        // acutally just return 200 and for failure make frontend call api to create new transaction
        if(orderService.verifyPaymentAndUpdateOrderStatus(orderId, user)) {
            return ResponseEntity.ok().body(
                new TextResponse("Payment verified and order updated successfully")
            );
        } else {
            return ResponseEntity.status(500).body(
                new ErrorResponse("INTERNAL_SERVER_ERROR", "Payment verification failed")
            );
            //TODO make a new transaction
        }
    }

}
