package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;

public record ProductInput(
    String name,
    String description,
    BigDecimal price,
    int stock
) {
    
}
