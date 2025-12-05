package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.enums.PaymentStatus;

public record TransactionDto(
    UUID transactionId,
    String createdAt,
    String updatedAt,
    PaymentStatus status,
    PaymentMethod paymentMethod,
    BigDecimal amount,
    String paymentToken
) {
    
}
