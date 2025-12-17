package com.tomcvt.brickshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.tomcvt.brickshop.dto.CustomerOrderDto;
import com.tomcvt.brickshop.dto.OrderSummaryDto;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findAllByUser(User user);
    Optional<Order> findByCheckoutSessionId(UUID uuid);
    @Query("""
        SELECT o FROM Order o LEFT JOIN FETCH o.currentTransaction
        WHERE o.checkoutSessionId = :uuid
    """)
    Optional<Order> findByCheckoutSessionIdWithTransaction(UUID uuid);
    Optional<Order> findByOrderId(Long orderId);
    Optional<Order> findByOrderIdAndUser(Long orderId, User user);
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.user
        LEFT JOIN FETCH o.currentTransaction
        LEFT JOIN FETCH o.transactions
        WHERE o.orderId = :orderId
    """)
    Optional<Order> findByOrderIdWithTransactionsAndUser(Long orderId);
    @Query("SELECT MAX(o.orderId) FROM Order o")
    Long findMaxOrderId();
    @Query("""
            SELECT new com.tomcvt.brickshop.dto.CustomerOrderDto(
                o.orderId,
                u.username,
                c.id,
                o.shippingAddressString,
                o.status,
                o.createdAt,
                o.totalAmount,
                o.paymentMethod,
                t.status,
                o.checkoutSessionId
            )
            FROM Order o
            JOIN o.user u
            JOIN o.cart c
            LEFT JOIN o.currentTransaction t
            WHERE o.orderId = :orderId
            """)
    Optional<CustomerOrderDto> findCustomerOrderDtoByOrderId(Long orderId);
    @Query("""
            SELECT new com.tomcvt.brickshop.dto.CustomerOrderDto(
                o.orderId,
                u.username,
                c.id,
                o.shippingAddressString,
                o.status,
                o.createdAt,
                o.totalAmount,
                o.paymentMethod,
                t.status,
                o.checkoutSessionId
            )
            FROM Order o
            JOIN o.user u
            JOIN o.cart c
            LEFT JOIN o.currentTransaction t
            WHERE o.checkoutSessionId = :checkoutSessionId
            """)
    Optional<CustomerOrderDto> findCustomerOrderDtoByCheckoutSessionId(UUID checkoutSessionId);
    @Query("""
            SELECT new com.tomcvt.brickshop.dto.CustomerOrderDto(
                o.orderId,
                u.username,
                c.id,
                o.shippingAddressString,
                o.status,
                o.createdAt,
                o.totalAmount,
                o.paymentMethod,
                t.status,
                o.checkoutSessionId
            )
            FROM Order o
            JOIN o.user u
            JOIN o.cart c
            LEFT JOIN o.currentTransaction t
            WHERE o.user = :user
            """)
    List<CustomerOrderDto> findCustomerOrderDtosByUser(User user);
    @Query("""
            SELECT new com.tomcvt.brickshop.dto.OrderSummaryDto(
                o.orderId,
                o.status,
                o.paymentMethod,
                o.totalAmount
            )
            FROM Order o
            WHERE o.user = :user
            """)
    List<OrderSummaryDto> findOrderSummaryDtoByUser(User user);
    @Query("""
        SELECT t.paymentToken FROM Order o
        LEFT JOIN o.currentTransaction t
        WHERE o.orderId = :orderId AND o.user = :user
    """)
    Optional<String> findPaymentTokenByOrderIdAndUser(Long orderId, User user);
    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.user
        JOIN FETCH o.cart
        LEFT JOIN FETCH o.currentTransaction
        WHERE o.id IN :ids
    """)
    List<Order> findAllByIdInWithUserCartAndTransaction(List<Long> ids);

}
