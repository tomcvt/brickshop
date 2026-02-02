package com.tomcvt.brickshop.dto;

import java.time.Instant;
import java.util.UUID;

public record ReviewProjection(
    Integer rating,
    String comment,
    Instant createdAt,
    String username,
    UUID publicId
) {
    
}
