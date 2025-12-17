package com.tomcvt.brickshop.dto;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public record ShipmentAddressDto(
    UUID publicId,
    @NotNull @Length(min = 1, max = 200) String fullName,
    @NotNull @Length(min = 1, max = 200) String street,
    @NotNull @Length(min = 1, max = 200) String zipCode,
    @NotNull @Length(min = 1, max = 200) String city,
    @NotNull @Length(min = 1, max = 200) String country,
    @NotNull @Length(min = 1, max = 200) String phoneNumber
) {
    
}
