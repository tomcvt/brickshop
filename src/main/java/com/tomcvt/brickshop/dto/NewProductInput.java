package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.Set;

public record NewProductInput(
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Set<String> categories
) {
    
}
