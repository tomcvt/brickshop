package com.tomcvt.brickshop.dto;

public record AdminDashboardInfoDto(
    int totalUsers,
    int activeUsers,
    int totalSessions,
    long totalProducts,
    long totalOrders,
    double totalRevenue
) {
    
}
