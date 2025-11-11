package com.tomcvt.brickshop.controller.api;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.dto.ProductInput;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.model.WrapUserDetails;
import com.tomcvt.brickshop.service.CategoryService;
import com.tomcvt.brickshop.service.ProductService;
import com.tomcvt.brickshop.session.ImageOrderValidator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminApiController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImageOrderValidator imageOrderValidator;

    public AdminApiController(
            ProductService productService,
            ImageOrderValidator imageOrderValidator,
            CategoryService categoryService) {
        this.productService = productService;
        this.imageOrderValidator = imageOrderValidator;
        this.categoryService = categoryService;
    }

    @GetMapping("/info")
    public ResponseEntity<String> getInfo(@AuthenticationPrincipal WrapUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok("Guest");
        }
        return ResponseEntity
                .ok("Name: " + userDetails.getUser().getUsername() + ", role: " + userDetails.getUser().getRole());
    }
    //TODO refactor to dto
    @PostMapping("/newproduct")
    public ResponseEntity<?> addProduct(@RequestBody ProductInput input) {
        Product newProduct = productService.addProduct(input);
        return ResponseEntity.ok().body(newProduct);
    }

    @GetMapping("/edit-product/{publicId}")
    public ResponseEntity<ProductDto> getProductEditRequest(@PathVariable UUID publicId) {
        ProductDto dto = productService.getProductHydratedByPublicId(publicId).toDto();
        imageOrderValidator.storeImageOrder(publicId, Set.copyOf(dto.imageUrls()));
        System.out.println("Stored image order for product " + publicId);
        for (String url : dto.imageUrls()) {
            System.out.println(" - " + url);
        }
        return ResponseEntity.ok().body(dto);
    }
    //TODO add productId validation in path variable
    @PatchMapping("/edit-product")
    public ResponseEntity<String> postEditProduct(@RequestBody ProductDto productDto) {
        if (!imageOrderValidator.validateImageOrder(productDto.publicId(), productDto.imageUrls())) {
            return ResponseEntity.status(400).body("Produce edit request validation failed, reload the page and try again");
        }
        productService.editProductFromDto(productDto);
        imageOrderValidator.clearImageOrder(productDto.publicId());
        return ResponseEntity.ok().body("Product edited");
    }
    @PostMapping("/add-category")
    public ResponseEntity<String> addCategory(@RequestBody String categoryName) {
        String response = categoryService.addCategory(categoryName);
        return response != null ?
                ResponseEntity.ok().body(response) :
                ResponseEntity.status(400).body("Category creation failed");
    }
    //TODO refactor sql error exception handling and response (inform about products with this category)
    @DeleteMapping("/delete-category")
    public ResponseEntity<String> removeCategory(@RequestBody String categoryName) {
        String response = categoryService.deleteCategory(categoryName);
        return response != null ?
                ResponseEntity.ok().body(response) :
                ResponseEntity.status(400).body("Category removal failed");
    }
    //TODO add more admin endpoints for order management, user management, etc.

}
