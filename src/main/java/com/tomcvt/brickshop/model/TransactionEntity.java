package com.tomcvt.brickshop.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.enums.PaymentStatus;

import jakarta.persistence.*;

@Entity
@Table(name="transactions", indexes = {
    @Index(name="idx_transaction_transaction_id", columnList = "transaction_id"),
    @Index(name="idx_transaction_order_id", columnList = "order_id")
})
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="transaction_id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID transactionId = UUID.randomUUID();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String paymentToken;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UUID getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }
    public PaymentStatus getStatus() {
        return status;
    }
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getPaymentToken() {
        return paymentToken;
    }
    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}
