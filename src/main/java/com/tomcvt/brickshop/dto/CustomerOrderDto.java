package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.enums.PaymentStatus;

public class CustomerOrderDto {
    private Long orderId;
    private String username;
    private Long cartId;
    private String shippingAddressString;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private UUID checkoutSessionId;

    public CustomerOrderDto(Long orderId, String username, Long cartId, String shippingAddressString, OrderStatus status,
            BigDecimal totalAmount, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
            UUID checkoutSessionId) {
        this.orderId = orderId;
        this.username = username;
        this.cartId = cartId;
        this.shippingAddressString = shippingAddressString;
        this.status = status;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.checkoutSessionId = checkoutSessionId;
    }
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Long getCartId() {
        return cartId;
    }
    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }
    public String getShippingAddressString() {
        return shippingAddressString;
    }
    public void setShippingAddressString(String shippingAddressString) {
        this.shippingAddressString = shippingAddressString;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public UUID getCheckoutSessionId() {
        return checkoutSessionId;
    }
    public void setCheckoutSessionId(UUID checkoutSessionId) {
        this.checkoutSessionId = checkoutSessionId;
    }

}
