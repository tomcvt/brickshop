package com.tomcvt.brickshop.controller.api;

import org.springframework.web.bind.annotation.RestController;

import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.dto.ProductSummaryDto;
import com.tomcvt.brickshop.mappers.ProductMapper;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.service.CategoryService;
import com.tomcvt.brickshop.service.ProductService;

import io.micrometer.core.ipc.http.HttpSender.Response;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductApiController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
    @GetMapping("/summaries")
    public SimplePage<ProductSummaryDto> searchProductSummaries(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) List<String> category,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        if (query == null && (category == null || category.isEmpty())) {
            return productService.getProductSummariesByPage(page, size);
        }
        if (query == null && category != null && !category.isEmpty()) {
            return productService.getProductSummariesByCategoriesAndPage(category, page, size);
        }
        return productService.getProductSummariesByKeywordAndCategoriesAndPage(query, category, page, size);
    }
    //TODO implement correct paging
    @GetMapping("/summaries-no-pic/search")
    public List<ProductSummaryDto> searchProducts(@RequestParam String keyword) {
        return productService.getProductSummariesNoPicByKeyword(keyword);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ProductDto> getProductHydratedByPublicId(@PathVariable UUID publicId) {
        var dto = productService.getProductDtoByPublicId(publicId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/categories")
    public List<String> getCategoriesNames() {
        System.out.print(categoryService.getCategoriesNames());
        return categoryService.getCategoriesNames();
    }
}
