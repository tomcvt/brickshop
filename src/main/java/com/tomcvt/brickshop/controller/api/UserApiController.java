package com.tomcvt.brickshop.controller.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tomcvt.brickshop.dto.CartDto;
import com.tomcvt.brickshop.dto.CustomerOrderDto;
import com.tomcvt.brickshop.dto.OrderSummaryDto;
import com.tomcvt.brickshop.dto.PassPayload;
import com.tomcvt.brickshop.dto.PublicIdDto;
import com.tomcvt.brickshop.dto.RegistrationRequest;
import com.tomcvt.brickshop.dto.ShipmentAddressDto;
import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.service.CartService;
import com.tomcvt.brickshop.service.OrderService;
import com.tomcvt.brickshop.service.ShipmentAddressService;
import com.tomcvt.brickshop.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class UserApiController {
    private final AuthService authService;
    private final OrderService orderService;
    private final CartService cartService;
    private final ShipmentAddressService shipmentAddressService;

    public UserApiController(AuthService authService, OrderService orderService, CartService cartService, 
            ShipmentAddressService shipmentAddressService
    ) {
        this.authService = authService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.shipmentAddressService = shipmentAddressService;
    }

    //TODO register user with email and role USER
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        authService.registerUser(request.username(), request.rawPassword(), request.email(), "USER");
        return ResponseEntity.ok("User " + request.username() + " registered successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal WrapUserDetails userDetails,
                                                 @RequestBody PassPayload passPayload) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        authService.changePassword(userDetails.getId(), passPayload);
        return ResponseEntity.status(500).body("Failed to change password");
    }
    //TODO additional endpoint
    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal WrapUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<CustomerOrderDto> ordersDto = orderService.getAllCustomerOrderDtoForUserId(userDetails.getUser());
        return ResponseEntity.ok(ordersDto);
    }

    @GetMapping("/orders/summaries")
    public ResponseEntity<?> getUserOrdersSummary(@AuthenticationPrincipal WrapUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<OrderSummaryDto> osDto = orderService.getAllOrderSummariesForUser(userDetails.getUser());
        return ResponseEntity.ok().body(osDto);
    }


    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getUserOrderById(@AuthenticationPrincipal WrapUserDetails userDetails,
                                              @PathVariable Long orderId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        CustomerOrderDto orderDto = orderService.getOrderByOrderIdAndUser(orderId, userDetails.getUser()).toCustomerOrderDto();
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/carts/{cartId}")
    public ResponseEntity<?> getUserCartById(@AuthenticationPrincipal WrapUserDetails userDetails,
                                             @PathVariable Long cartId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        CartDto cartDto = cartService.getCartDtoByIdAndUserId(cartId, userDetails.getId());
        return ResponseEntity.ok().body(cartDto);
    }
    @GetMapping("/address/all")
    public ResponseEntity<?> getAllShipmentAddresses(@AuthenticationPrincipal WrapUserDetails wrapUserDetails) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var addresses = shipmentAddressService.getAllShipmentAddressesForUser(wrapUserDetails.getUser())
                .stream().map(ShipmentAddress::toDto).toList();
        return ResponseEntity.ok().body(addresses);
    }
    @PostMapping("/address/add")
    public ResponseEntity<?> addShipmentAddress(@AuthenticationPrincipal WrapUserDetails wrapUserDetails,
            @RequestBody ShipmentAddressDto shipmentAddressDto) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var saDto = shipmentAddressService.addShipmentAddressForUser(shipmentAddressDto, wrapUserDetails.getUser()).toDto();
        return ResponseEntity.ok().body(saDto);
    }
    @PostMapping("/address/update")
    public ResponseEntity<?> updateShipmentAddress(@AuthenticationPrincipal WrapUserDetails wrapUserDetails,
            @RequestBody ShipmentAddressDto shipmentAddressDto) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ShipmentAddress sa = shipmentAddressService.updateShipmentAddressForUser(shipmentAddressDto, wrapUserDetails.getUser());
        return ResponseEntity.ok().body(sa.toDto());
    }
    @PostMapping("/address/delete")
    public ResponseEntity<?> deleteShipmentAddress(@AuthenticationPrincipal WrapUserDetails wrapUserDetails,
            @RequestBody PublicIdDto publicIdDto) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        shipmentAddressService.deleteShipmentAddressForUser(publicIdDto.publicId(), wrapUserDetails.getUser());
        return ResponseEntity.ok().build();
    }
    
}
