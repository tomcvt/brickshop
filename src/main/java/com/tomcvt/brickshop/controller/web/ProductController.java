package com.tomcvt.brickshop.controller.web;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.service.ProductService;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/products")
    public String getProductsPage() {
        return "products";
    }
    @GetMapping("/products/{publicId}")
    public String getProductPage(Model model, @PathVariable UUID publicId) {
        ProductDto productData = productService.getProductHydratedByPublicId(publicId).toDto();
        model.addAttribute("product", productData);
        model.addAttribute("productPublicId", publicId);
        return "product-details";
    }
    //TODO implement create product page
    @GetMapping("/products/new")
    public String getCreateProductPage() {
        return "create-product";
    }
}
