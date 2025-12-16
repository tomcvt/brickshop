package com.tomcvt.brickshop.service;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.dto.AdminDashboardInfoDto;

@Service
public class AdminOrchestrator {
    private final SessionRegistry sessionRegistry;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminOrchestrator(SessionRegistry sessionRegistry, 
                            UserService userService,
                            ProductService productService,
                            OrderService orderService
    ) {
        this.sessionRegistry = sessionRegistry;
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public AdminDashboardInfoDto getAdminDashboardInfo() {
        int totalUsers = (int) userService.getTotalUserCount();
        int activeUsers = getActiveUserCount();
        int totalSessions = getTotalSessionCount();
        // Placeholder values for totalProducts, totalOrders, totalRevenue
        long totalProducts = productService.getTotalProductCount();
        long totalOrders = orderService.getTotalOrderCount();
        double totalRevenue = 0.0;

        return new AdminDashboardInfoDto(
            totalUsers,
            activeUsers,
            totalSessions,
            totalProducts,
            totalOrders,
            totalRevenue
        );
    }

    public int getActiveUserCount() {
        return sessionRegistry.getAllPrincipals().size();
    }

    public int getTotalSessionCount() {
        int count = 0;
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            count += sessionRegistry.getAllSessions(principal, false).size();
        }
        return count;
    }


}
