package com.tomcvt.brickshop.utility;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.service.*;
import com.tomcvt.brickshop.zdemo.DemoCache;
import com.tomcvt.brickshop.zdemo.DemoCarts;
import com.tomcvt.brickshop.zdemo.DemoOrders;
import com.tomcvt.brickshop.zdemo.DevUsers;

@Component
@Profile({"dev", "demo"})
public class DevDataLoader {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DevDataLoader.class);

    private final ShipmentAddressService shipmentAddressService;
    private final CSVloader csvloader;
    private final CategoryExtractor categoryExtractor;
    private final UserService userService;
    private final DummyImageLoader dummyImageLoader;
    /// here the refactored part
    private final DevUsers devUsers;
    private final DemoOrders demoOrders;
    private final DemoCarts demoCarts;
    private final DemoCache demoCache;

    public DevDataLoader(ProductImageService productImageService,
            ShipmentAddressService shipmentAddressService, CSVloader csvloader, 
            CategoryExtractor categoryExtractor, UserService userService, 
            DummyImageLoader dummyImageLoader, DemoOrders demoOrders, DevUsers devUsers,
            DemoCarts demoCarts, DemoCache demoCache) {
        this.shipmentAddressService = shipmentAddressService;
        this.csvloader = csvloader;
        this.categoryExtractor = categoryExtractor;
        this.userService = userService;
        this.dummyImageLoader = dummyImageLoader;
        this.demoOrders = demoOrders;
        this.devUsers = devUsers;
        this.demoCarts = demoCarts;
        this.demoCache = demoCache;
    }

    public void loadDevData() {
        devUsers.createDevUsers();
        var u1 = userService.findByUsername("admin");
        csvloader.loadProductsFromCSV();
        categoryExtractor.initCategories();
        //var p1 = productService.getProductById(1L);
        //var p2 = productService.getProductById(2L);
        //var p3 = productService.getProductById(3L);
        demoCarts.createDemoCarts();
        log.info("Demo closed carts IDs: " + demoCache.demoClosedCartsIds);
        shipmentAddressService.addShipmentAddressForUser(
                new ShipmentAddress(null, "JohnDoe", "123 Main St", "12-345", "Mielno", "Poland",
                        "123-456-7890"),
                u1);
        dummyImageLoader.loadDummyImages();
        // here the refactored part
        demoOrders.createDemoOrders();
    }
}
