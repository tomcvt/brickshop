package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FlatCartRowDto(
    Long cartId,
    Long cartItemId,
    int quantity,
    UUID productPublicId,
    String productName,
    BigDecimal price,
    UUID thumbnailUuid
) {}
