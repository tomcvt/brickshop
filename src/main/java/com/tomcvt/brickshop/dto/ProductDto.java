package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProductDto(
        UUID publicId,
        String name,
        String description,
        List<String> imageUrls,
        BigDecimal price,
        int stock,
        Set<String> categoriesNames
        ) {}
