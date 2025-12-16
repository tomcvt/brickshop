package com.tomcvt.brickshop.controller.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.AdminDashboardInfoDto;
import com.tomcvt.brickshop.dto.CustomerOrderDto;
import com.tomcvt.brickshop.dto.NewProductInput;
import com.tomcvt.brickshop.dto.OrderFullDto;
import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.mappers.OrderMapper;
import com.tomcvt.brickshop.mappers.ProductMapper;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.service.AdminOrchestrator;
import com.tomcvt.brickshop.service.CategoryService;
import com.tomcvt.brickshop.service.OrderService;
import com.tomcvt.brickshop.service.ProductService;
import com.tomcvt.brickshop.session.ImageOrderValidator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'SUPERUSER')")
@RequestMapping("/api/admin")
public class AdminApiController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminApiController.class);
    private final AdminOrchestrator adminOrchestrator;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImageOrderValidator imageOrderValidator;
    private final OrderService orderService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public AdminApiController(
            ProductService productService,
            ImageOrderValidator imageOrderValidator,
            CategoryService categoryService,
            OrderService orderService, 
            AdminOrchestrator adminOrchestrator) {
        this.productService = productService;
        this.imageOrderValidator = imageOrderValidator;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.adminOrchestrator = adminOrchestrator;
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    @PostMapping("/add-product")
    public ResponseEntity<ProductDto> addProduct(@ModelAttribute NewProductInput productInput,
            @RequestParam("images") List<MultipartFile> images
    ) {
        Product product = productService.addProductWithImages(productInput, images);
        var dto = productMapper.toProductDto(product);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER','MODERATOR')")
    @GetMapping("/edit-product/{publicId}")
    public ResponseEntity<ProductDto> getProductEditRequest(@PathVariable UUID publicId) {
        ProductDto dto = productMapper.toProductDto(productService.getProductByPublicId(publicId));
        imageOrderValidator.storeImageOrder(publicId, Set.copyOf(dto.imageUrls()));
        return ResponseEntity.ok().body(dto);
    }
    //TODO add productId validation in path variable
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    @PatchMapping("/edit-product")
    public ResponseEntity<String> postEditProduct(@RequestBody ProductDto productDto) {
        if (!imageOrderValidator.validateImageOrder(productDto.publicId(), productDto.imageUrls())) {
            return ResponseEntity.status(400).body("Produce edit request validation failed, reload the page and try again");
        }
        productService.editProductFromDto(productDto);
        imageOrderValidator.clearImageOrder(productDto.publicId());
        return ResponseEntity.ok().body("Product edited");
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    @PostMapping("/add-category")
    public ResponseEntity<String> addCategory(@RequestBody String categoryName) {
        String response = categoryService.addCategory(categoryName);
        return response != null ?
                ResponseEntity.ok().body(response) :
                ResponseEntity.status(400).body("Category creation failed");
    }
    //TODO refactor sql error exception handling and response (inform about products with this category)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERUSER')")
    @DeleteMapping("/delete-category")
    public ResponseEntity<String> removeCategory(@RequestBody String categoryName) {
        String response = categoryService.deleteCategory(categoryName);
        return response != null ?
                ResponseEntity.ok().body(response) :
                ResponseEntity.status(400).body("Category removal failed");
    }
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'SUPERUSER')")
    //TODO add more admin endpoints for order management, user management, etc.
    @GetMapping("/orders/search")
    public ResponseEntity<SimplePage<CustomerOrderDto>> searchOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String createdBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (orderId != null && !orderId.isEmpty()) {
            var order = orderService.findOrderByOrderId(Long.parseLong(orderId));
            if (order.isPresent()) {
                SimplePage<CustomerOrderDto> singleOrderPage = SimplePage.onePage(List.of(order.get().toCustomerOrderDto()));
                return ResponseEntity.ok().body(singleOrderPage);
            }
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderService.searchOrdersByCriteria(username, status, paymentMethod, createdBefore, pageable);
        SimplePage<CustomerOrderDto> simpleOrderPage = SimplePage.fromPage(
                orderPage.map(Order::toCustomerOrderDto)
        );
        return ResponseEntity.ok().body(simpleOrderPage);
    }
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'SUPERUSER')")
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderFullDto> getOrderFullDtoById(@PathVariable Long orderId) {
        Order order = orderService.getFullOrderDetailsByOrderId(orderId);
        OrderFullDto orderFullDto = orderMapper.toOrderFullDto(order);
        return ResponseEntity.ok().body(orderFullDto);
    }
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'SUPERUSER')")
    @GetMapping("/dashboard-info")
    public ResponseEntity<AdminDashboardInfoDto> getAdminDashboardInfo() {
        var dto = adminOrchestrator.getAdminDashboardInfo();
        return ResponseEntity.ok().body(dto);
    }
}
