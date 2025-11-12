package com.tomcvt.brickshop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tomcvt.brickshop.model.ShipmentItem;

public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, UUID> {
    @Query("""
        SELECT si
        FROM ShipmentItem si
        JOIN si.shipment s
        WHERE s.order.orderId = :orderId AND si.product.id = :productId
    """)
    Optional<ShipmentItem> findByOrderIdAndProductId(Long orderId, Long productId);
}
