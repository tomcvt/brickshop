package com.tomcvt.brickshop.dto;

public record ShipmentItemDto(
    String productId,
    String productName,
    Integer quantity,
    String status
) {
    
}
