package com.tomcvt.brickshop.searching;

import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;

import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.User;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class OrderSpecifications {
    
    public static Specification<Order> withFilters(OrderSearchCriteria c) {
        return Specification
            .<Order>unrestricted()
            .and(usernameEquals(c.username()))
            .and(statusEquals(c.status()))
            .and(createdBefore(c.createdBefore()))
            .and(paymentMethodEquals(c.paymentMethod()));
    }

    private static Specification<Order> usernameEquals(String username) {
        if (username == null) return null;
        return (root, query, cb) -> {
            Join<Order, User> user = root.join("user", JoinType.INNER);
            return cb.equal(user.get("username"), username);
        };
    }

    private static Specification<Order> statusEquals(OrderStatus status) {
        return (root, query, cb) -> status == null ?
            null : cb.equal(root.get("status"), status);
    }

    private static Specification<Order> createdBefore(Instant createdBefore) {
        return (root, query, cb) -> createdBefore == null ?
            null : cb.lessThan(root.get("createdAt"), createdBefore);
    }

    private static Specification<Order> paymentMethodEquals(PaymentMethod method) {
        return (root, query, cb) -> method == null ?
            null : cb.equal(root.get("paymentMethod"), method);
    }
}
