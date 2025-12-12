package com.tomcvt.brickshop.controller.web;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN','SUPERUSER', 'MODERATOR')")
public class AdminController {
    
    @GetMapping("/manage-products")
    public String getProductsManagementPage() {
        return "admin/productsmanagement";
    }
    @GetMapping("/add-product")
    public String getAddProductPage() {
        return "admin/add-product";
    }
    @GetMapping("/edit-product/{publicId}")
    public String getEditProductPage(@PathVariable UUID publicId, Model model) {
        model.addAttribute("publicId", publicId);
        return "editproduct";
    }
    @GetMapping("/orders")
    public String getOrdersManagementPage() {
        return "admin/ordersmanagement";
    }
    @GetMapping("/orders/{orderId}")
    public String getOrderDetailsPage(@PathVariable Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "admin/order-full-details";
    }
}
