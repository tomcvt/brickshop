package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderFullDto(
    Long orderId,
    String username,
    String email,
    Long cartId,
    String shippingAddressString,
    String status,
    String createdAt,
    BigDecimal totalAmount,
    String paymentMethod,
    List<TransactionDto> transactions,
    CartDto cart,
    TransactionDto currentTransaction,
    UUID checkoutSessionId
) {
    
}
