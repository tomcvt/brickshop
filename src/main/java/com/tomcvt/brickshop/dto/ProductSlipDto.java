package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSlipDto(
    String name,
    UUID publicId,
    BigDecimal price
) {
    
}
