package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryDto(UUID publicId, String name, BigDecimal price, Integer stock, String thumbnailUrl) {
    
}
