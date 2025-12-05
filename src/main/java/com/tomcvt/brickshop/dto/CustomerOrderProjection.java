package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.enums.PaymentStatus;

public record CustomerOrderProjection(
    Long orderId,
    String username,
    Long cartId,
    String shippingAddressString,
    OrderStatus status,
    Instant createdAt,
    BigDecimal totalAmount,
    PaymentMethod paymentMethod,
    PaymentStatus paymentStatus,
    UUID checkoutSessionId
) {
    
}
