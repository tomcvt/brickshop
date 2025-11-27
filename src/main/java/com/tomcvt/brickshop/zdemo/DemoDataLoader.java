package com.tomcvt.brickshop.zdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.utility.CSVloader;
import com.tomcvt.brickshop.utility.CategoryExtractor;
import com.tomcvt.brickshop.utility.DummyImageLoader;

@Component
@Profile({"dev", "demo"})
public class DemoDataLoader {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DemoDataLoader.class);

    private final CSVloader csvloader;
    private final CategoryExtractor categoryExtractor;
    private final DummyImageLoader dummyImageLoader;
    /// here the refactored part
    private final DemoUsers demoUsers;
    private final DemoOrders demoOrders;
    private final DemoCarts demoCarts;
    private final DemoCache demoCache;
    private final DemoAddresses demoAddresses;

    public DemoDataLoader(
            DemoAddresses demoAddresses, CSVloader csvloader, 
            CategoryExtractor categoryExtractor, 
            DummyImageLoader dummyImageLoader, DemoOrders demoOrders, DemoUsers demoUsers,
            DemoCarts demoCarts, DemoCache demoCache) {
        this.demoAddresses = demoAddresses;
        this.csvloader = csvloader;
        this.categoryExtractor = categoryExtractor;
        this.dummyImageLoader = dummyImageLoader;
        this.demoOrders = demoOrders;
        this.demoUsers = demoUsers;
        this.demoCarts = demoCarts;
        this.demoCache = demoCache;
    }

    public void loadDemoData() {
        demoUsers.createDemoUsers();
        csvloader.loadProductsFromCSV();
        categoryExtractor.initCategories();
        demoCarts.createDemoCarts();
        log.info("Demo closed carts IDs: " + demoCache.demoClosedCartsIds);
        demoAddresses.createDemoAddresses();
        dummyImageLoader.loadDummyImages();
        // here the refactored part
        demoOrders.createDemoOrders();
    }
}