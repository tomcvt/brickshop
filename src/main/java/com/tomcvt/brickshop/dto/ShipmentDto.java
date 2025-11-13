package com.tomcvt.brickshop.dto;

import java.util.List;

import com.tomcvt.brickshop.enums.ShipmentStatus;

public record ShipmentDto(
    Long shipmentId,
    Long orderId,
    String trackingNumber,
    String addressString,
    String packedByUsername,
    ShipmentStatus status,
    List<ShipmentItemDto> items
) {
}
