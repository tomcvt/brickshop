package com.tomcvt.brickshop.controller.api;

import java.util.UUID;

import org.attoparser.dom.Text;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.CheckoutDataDto;
import com.tomcvt.brickshop.dto.ErrorResponse;
import com.tomcvt.brickshop.dto.TextResponse;
import com.tomcvt.brickshop.model.*;
import com.tomcvt.brickshop.service.CheckoutSessionService;
import com.tomcvt.brickshop.service.OrderService;
import com.tomcvt.brickshop.service.ShipmentAddressService;
import com.tomcvt.brickshop.session.CartModifiedFlag;

@RestController
@RequestMapping("/api/checkout")
@PreAuthorize("isAuthenticated()")
public class CheckoutApiController {

    private final CheckoutSessionService checkoutSessionService;
    private final OrderService orderService;
    private final CartModifiedFlag cartModifiedFlag;
    private final ShipmentAddressService shipmentAddressService;

    public CheckoutApiController(CheckoutSessionService checkoutSessionService, OrderService orderService, 
        CartModifiedFlag cartModifiedFlag, ShipmentAddressService shipmentAddressService) {
        this.checkoutSessionService = checkoutSessionService;
        this.orderService = orderService;
        this.cartModifiedFlag = cartModifiedFlag;
        this.shipmentAddressService = shipmentAddressService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> getActiveCheckout(@AuthenticationPrincipal WrapUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("UNAUTHORIZED", "Login to proceed with checkout")
            );
        }
        CheckoutDataDto checkoutData = checkoutSessionService.getActiveCheckoutSessionForUser(userDetails.getUser());
        if (checkoutData.getUuidData() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse("INTERNAL_SERVER_ERROR", "Failed to create checkout session")
            );
        }
        checkoutData.setCartFlag(cartModifiedFlag.getUUID()); 
        return ResponseEntity.ok().body(checkoutData);
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeSessionAndCreateOrder(
            @AuthenticationPrincipal WrapUserDetails userDetails,
            @RequestBody CheckoutDataDto checkoutData) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse("UNAUTHORIZED", "Login to proceed with checkout")
            );
        }
        if(checkoutData.getUuidData() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse("BAD_REQUEST", "Invalid checkout data")
            );
        }
        UUID uuid = UUID.fromString(checkoutData.getUuidData());
        CheckoutSession session = checkoutSessionService.getSessionIfExists(uuid);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse("BAD_REQUEST", "Invalid checkout session")
            );
        }
        if (!session.isActive()) {
            Long orderId = orderService.getOrderForSessionId(uuid).getOrderId();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse("CONFLICT", "Checkout session is already closed, order ID: " + orderId)
            );
        }
        if (checkoutData.getCartFlag() == null || !checkoutData.getCartFlag().equals(cartModifiedFlag.getUUID())) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponse("METHOD_NOT_ALLOWED", "Cart was modified during checkout. Please restart the checkout process.")
            );
        }
        Order order = checkoutSessionService.closeSessionAndCreateOrderForUser(userDetails.getUser(), checkoutData);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new TextResponse(String.valueOf(order.getOrderId()))
        );
    }
    @GetMapping("/address/all")
    public ResponseEntity<?> getAllShipmentAddresses(@AuthenticationPrincipal WrapUserDetails wrapUserDetails) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var addresses = shipmentAddressService.getAllShipmentAddressesForUser(wrapUserDetails.getUser())
                .stream()
                .map(ShipmentAddress::toDto);
        return ResponseEntity.ok().body(addresses);
    }

}
