package com.tomcvt.brickshop.controller.web;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyRole('ADMIN','PACKER','SUPERUSER','MODERATOR')")
public class PackerController {
    @GetMapping("/packer")
    public String packerHome() {
        return "packer-home";
    }
    @GetMapping("/packer/{orderId}")
    public String packerOrderDetail() {
        return "packer-shipment-detail";
    }
    @GetMapping("/packer/orders")
    public String packerOrdersList() {
        return "packer-home";
    }
    @GetMapping("/packer/orders/{orderId}")
    public String packerOrderDetails() {
        return "packer-shipment-detail";
    }
}
