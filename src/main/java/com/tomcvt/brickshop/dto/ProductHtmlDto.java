package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProductHtmlDto(
    UUID publicId,
    String name,
    String description,
    String htmlDescription,
    List<String> imageUrls,
    BigDecimal price,
    Integer stock,
    Set<String> categoriesNames
) {
    
}
