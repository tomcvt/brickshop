package com.tomcvt.brickshop.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.tomcvt.brickshop.dto.CustomerOrderDto;
import com.tomcvt.brickshop.enums.*;

import jakarta.persistence.*;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_order_id", columnList = "order_id"),
    @Index(name = "idx_order_user_id", columnList = "user_id"),
    @Index(name = "idx_order_checkout_session_id", columnList = "checkout_session_id"),
    @Index(name = "idx_order_status_created_at", columnList = "status, created_at"),
    @Index(name = "idx_order_created_at", columnList = "created_at")
})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    private String shippingAddressString;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("createdAt DESC")
    @Fetch(FetchMode.SUBSELECT)
    private List<TransactionEntity> transactions;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_transaction_id")
    private TransactionEntity currentTransaction;
    @Column(name = "checkout_session_id", columnDefinition = "uuid", nullable = false)
    private UUID checkoutSessionId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
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
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
    public List<TransactionEntity> getTransactions() {
        return transactions;
    }
    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }
    public TransactionEntity getCurrentTransaction() {
        return currentTransaction;
    }
    public void setCurrentTransaction(TransactionEntity currentTransaction) {
        this.currentTransaction = currentTransaction;
    }
    public UUID getCheckoutSessionId() {
        return checkoutSessionId;
    }
    public void setCheckoutSessionId(UUID checkoutSessionId) {
        this.checkoutSessionId = checkoutSessionId;
    }

    public CustomerOrderDto toCustomerOrderDto() {
        return new CustomerOrderDto(
            this.orderId,
            this.user.getUsername(),
            this.cart.getId(),
            this.shippingAddressString,
            this.status,
            this.createdAt,
            this.totalAmount,
            this.paymentMethod,
            this.currentTransaction != null ? this.currentTransaction.getStatus() : null,
            this.checkoutSessionId
        );
    }
}
