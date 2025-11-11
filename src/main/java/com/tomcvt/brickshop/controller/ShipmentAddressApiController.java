package com.tomcvt.brickshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.service.ShipmentAddressService;

@RestController
@RequestMapping("/api/shipment-address")
public class ShipmentAddressApiController {
    private final ShipmentAddressService shipmentAddressService;
    public ShipmentAddressApiController(ShipmentAddressService shipmentAddressService) {
        this.shipmentAddressService = shipmentAddressService;
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllShipmentAddresses(@AuthenticationPrincipal WrapUserDetails wrapUserDetails) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(shipmentAddressService.getAllShipmentAddressesForUser(wrapUserDetails.getUser()));
    }
    @PostMapping("/add")
    public ResponseEntity<?> addShipmentAddress(@AuthenticationPrincipal WrapUserDetails wrapUserDetails,
            @RequestBody ShipmentAddress shipmentAddress) {
        if (wrapUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ShipmentAddress sa = shipmentAddressService.addShipmentAddressForUser(shipmentAddress, wrapUserDetails.getUser());
        return ResponseEntity.ok().body(sa);
    }

}
