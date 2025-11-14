package com.tomcvt.brickshop.utility;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.service.*;
import com.tomcvt.brickshop.zdemo.DemoOrders;

@Component
@Profile({"dev", "demo"})
public class DevDataLoader {
    private final AuthService authService;
    private final ProductService productService;
    private final CartService cartService;
    private final ShipmentAddressService shipmentAddressService;
    private final CSVloader csvloader;
    private final CategoryExtractor categoryExtractor;
    private final UserService userService;
    private final DummyImageLoader dummyImageLoader;
    /// here the refactored part
    private final DemoOrders demoOrders;

    public DevDataLoader(AuthService authService, ProductService productService,
            ProductImageService productImageService, CartService cartService,
            ShipmentAddressService shipmentAddressService, CSVloader csvloader, 
            CategoryExtractor categoryExtractor, UserService userService, 
            DummyImageLoader dummyImageLoader, DemoOrders demoOrders) {
        this.authService = authService;
        this.productService = productService;
        this.cartService = cartService;
        this.shipmentAddressService = shipmentAddressService;
        this.csvloader = csvloader;
        this.categoryExtractor = categoryExtractor;
        this.userService = userService;
        this.dummyImageLoader = dummyImageLoader;
        this.demoOrders = demoOrders;
    }

    public void loadDevData() {
        authService.registerActivatedUser("admin", "123", "abc@mail.com", "ADMIN");
        authService.registerActivatedUser("packer", "123", "abe@mail.com", "PACKER");
        authService.registerActivatedUser("user", "123", "abd@mail.com", "USER");
        var u1 = userService.findByUsername("admin");
        csvloader.loadProductsFromCSV();
        categoryExtractor.initCategories();
        var p1 = productService.getProductById(1L);
        var p2 = productService.getProductById(2L);
        var p3 = productService.getProductById(3L);
        cartService.addProductToActiveUserCart(1L, p1.getId(), 1);
        cartService.addProductToActiveUserCart(1L, p2.getId(), 2);
        cartService.addProductToActiveUserCart(1L, p3.getId(), 3);
        shipmentAddressService.addShipmentAddressForUser(
                new ShipmentAddress(null, "JohnDoe", "123 Main St", "12-345", "Mielno", "Poland",
                        "123-456-7890"),
                u1);
        dummyImageLoader.loadDummyImages();
        // here the refactored part
        demoOrders.createDemoOrders();
    }
}
