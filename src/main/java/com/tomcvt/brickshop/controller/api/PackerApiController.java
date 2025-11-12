package com.tomcvt.brickshop.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.brickshop.dto.ShipmentDto;
import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.service.PackingService;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/packer")
@PreAuthorize("hasAnyRole('ADMIN','PACKER')")
public class PackerApiController {
    private final PackingService packingService;

    public PackerApiController(PackingService packingService) {
        this.packingService = packingService;
    }

    @GetMapping("/to-pack")
    public ResponseEntity<?> getOrdersToPack(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        List<ShipmentDto> shipments = packingService.getShipmentsToPack(page, size);
        return ResponseEntity.ok().body(shipments);
    }
    @GetMapping("/with-order/{orderId}")
    public ResponseEntity<?> getShipmentWithOrder(
            @PathVariable Long orderId) {
        ShipmentDto shipment = packingService.getShipmentHydratedByOrderOrderId(orderId);
        return ResponseEntity.ok().body(shipment);
    }
    @PostMapping("/start-packing")
    public ResponseEntity<?> startPackingShipment(
            @AuthenticationPrincipal WrapUserDetails userDetails,
            @RequestParam(name = "orderId") Long orderId
    ) {
        ShipmentDto dto = packingService.startPackingShipment(orderId, userDetails.getUser());
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/pack-all")
    public ResponseEntity<?> packAllItemsInOrder(
            @RequestParam(name = "orderId") Long orderId
    ) {
        ShipmentDto dto = packingService.packAllItemsInShipment(orderId);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping("/pack")
    public ResponseEntity<?> packItemInOrder(
            @RequestParam(name = "orderId") Long orderId,
            @RequestParam(name = "productId") Long productId
    ) {
        ShipmentDto dto = packingService.packItemInShipment(orderId, productId);
        return ResponseEntity.ok().body(dto);
    }
    
}
