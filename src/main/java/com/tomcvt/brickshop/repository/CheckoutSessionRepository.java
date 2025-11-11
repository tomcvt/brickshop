package com.tomcvt.brickshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomcvt.brickshop.model.CheckoutSession;
import com.tomcvt.brickshop.model.User;

public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, UUID> {
    List<CheckoutSession> findByUser(User user);
    @Query("""
    SELECT c
    FROM CheckoutSession c
    WHERE c.active = TRUE AND c.user = :user
    """)
    Optional<CheckoutSession> findActiveSessionByUser(@Param("user") User user);
}
