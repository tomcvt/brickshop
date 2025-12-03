package com.tomcvt.brickshop.searching;

import java.time.Instant;

import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;

public record OrderSearchCriteria(
    String username,
    OrderStatus status,
    PaymentMethod paymentMethod,
    Instant createdBefore
) {
    
}
