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
import com.tomcvt.brickshop.dto.ShipmentAddressDto;
import com.tomcvt.brickshop.dto.TextResponse;
import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.SecureUserDetails;
import com.tomcvt.brickshop.service.CartService;
import com.tomcvt.brickshop.service.OrderService;
import com.tomcvt.brickshop.service.ShipmentAddressService;
import com.tomcvt.brickshop.utility.SanitizerUtil;
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

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal SecureUserDetails userDetails,
                                                 @RequestBody PassPayload passPayload) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        authService.changePassword(userDetails.getId(), passPayload);
        return ResponseEntity.ok().body(new TextResponse("Password changed successfully"));
    }
    //TODO additional endpoint
    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders(@AuthenticationPrincipal SecureUserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        List<CustomerOrderDto> ordersDto = orderService.getAllCustomerOrderDtoForUserId(userDetails.getUser());
        return ResponseEntity.ok(ordersDto);
    }

    @GetMapping("/orders/summaries")
    public ResponseEntity<?> getUserOrdersSummary(@AuthenticationPrincipal SecureUserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        List<OrderSummaryDto> osDto = orderService.getAllOrderSummariesForUser(userDetails.getUser());
        return ResponseEntity.ok().body(osDto);
    }


    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getUserOrderById(@AuthenticationPrincipal SecureUserDetails userDetails,
                                              @PathVariable Long orderId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        CustomerOrderDto orderDto = orderService.getOrderByOrderIdAndUser(orderId, userDetails.getUser()).toCustomerOrderDto();
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/carts/{cartId}")
    public ResponseEntity<?> getUserCartById(@AuthenticationPrincipal SecureUserDetails userDetails,
                                             @PathVariable Long cartId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        CartDto cartDto = cartService.getCartDtoByIdAndUserId(cartId, userDetails.getId());
        return ResponseEntity.ok().body(cartDto);
    }
    @GetMapping("/address/all")
    public ResponseEntity<?> getAllShipmentAddresses(@AuthenticationPrincipal SecureUserDetails wrapUserDetails) {
        if (wrapUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        var addresses = shipmentAddressService.getAllShipmentAddressesForUser(wrapUserDetails.getUser())
                .stream().map(ShipmentAddress::toDto).toList();
        return ResponseEntity.ok().body(addresses);
    }
    @PostMapping("/address/add")
    public ResponseEntity<?> addShipmentAddress(@AuthenticationPrincipal SecureUserDetails wrapUserDetails,
            @RequestBody ShipmentAddressDto shipmentAddressDto) {
        if (wrapUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        validateShipmentAddressDto(shipmentAddressDto);
        var saDto = shipmentAddressService.addShipmentAddressForUser(shipmentAddressDto, wrapUserDetails.getUser()).toDto();
        return ResponseEntity.ok().body(saDto);
    }
    @PostMapping("/address/update")
    public ResponseEntity<?> updateShipmentAddress(@AuthenticationPrincipal SecureUserDetails wrapUserDetails,
            @RequestBody ShipmentAddressDto shipmentAddressDto) {
        if (wrapUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        validateShipmentAddressDto(shipmentAddressDto);
        ShipmentAddress sa = shipmentAddressService.updateShipmentAddressForUser(shipmentAddressDto, wrapUserDetails.getUser());
        return ResponseEntity.ok().body(sa.toDto());
    }
    @PostMapping("/address/delete")
    public ResponseEntity<?> deleteShipmentAddress(@AuthenticationPrincipal SecureUserDetails wrapUserDetails,
            @RequestBody PublicIdDto publicIdDto) {
        if (wrapUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        shipmentAddressService.deleteShipmentAddressForUser(publicIdDto.publicId(), wrapUserDetails.getUser());
        return ResponseEntity.ok().build();
    }

    private void validateShipmentAddressDto(ShipmentAddressDto dto) {
        SanitizerUtil.validate(dto);
    }

    
}
