package com.tomcvt.brickshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomcvt.brickshop.model.*;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
