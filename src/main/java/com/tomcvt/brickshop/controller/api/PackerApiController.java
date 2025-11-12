package com.tomcvt.brickshop.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.brickshop.dto.ShipmentDto;
import com.tomcvt.brickshop.service.PackerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/packer")
@PreAuthorize("hasAnyRole('ADMIN','PACKER')")
public class PackerApiController {
    private final PackerService packerService;

    public PackerApiController(PackerService packerService) {
        this.packerService = packerService;
    }

    @GetMapping("/to-pack")
    public ResponseEntity<?> getOrdersToPack(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        List<ShipmentDto> shipments = packerService.getShipmentsToPack(page, size);
        return ResponseEntity.ok().body(shipments);
    }
    
}
