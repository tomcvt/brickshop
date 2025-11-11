package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;

import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;

public class OrderSummaryDto {
    private Long orderId;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;

    public OrderSummaryDto(Long orderId, OrderStatus status,
            PaymentMethod paymentMethod, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
    }
    public Long getOrderId() {
        return orderId;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
