package com.tomcvt.brickshop.dto;

import java.util.UUID;

public record ShipmentAddressDto(
    UUID publicId,
    String fullName,
    String street,
    String zipCode,
    String city,
    String country,
    String phoneNumber
) {
    
}
