package com.tomcvt.brickshop.dto;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
    @NotNull UUID productPublicId,
    @NotNull @Range(min = 1, max = 5) Integer rating,
    @NotNull @Length(max = 1000) String comment
) {
    
}
