package com.tomcvt.brickshop.dto;

import com.tomcvt.brickshop.enums.ShipmentItemStatus;

public record ShipmentItemDto(
    Long productId,
    String productName,
    Integer quantity,
    ShipmentItemStatus status
) {
    
}
