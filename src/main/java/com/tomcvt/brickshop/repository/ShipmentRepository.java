package com.tomcvt.brickshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomcvt.brickshop.enums.ShipmentStatus;
import com.tomcvt.brickshop.model.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    @Query("""
        SELECT s
        FROM Shipment s
        WHERE s.order.id = :orderId
    """)
    //TODO change to order.orderId , check behavoiour for testing 
    Optional<Shipment> findByOrderOrderId(@Param("orderId") Long orderId);
    @Query("""
        SELECT s
        FROM Shipment s
        LEFT JOIN FETCH s.order o
        LEFT JOIN FETCH s.packedBy
        LEFT JOIN FETCH s.items si
        LEFT JOIN FETCH si.product
        WHERE s.order.orderId = :orderId
    """)
    Optional<Shipment> findHydratedByOrderOrderId(@Param("orderId") Long orderId);
    @Query("""
        SELECT s.id
        FROM Shipment s
        WHERE s.status = :status
    """)
    Page<Long> findIdsByStatus(ShipmentStatus status, Pageable pageable);

    @Query("""
        SELECT s
        FROM Shipment s
        LEFT JOIN FETCH s.items si
        LEFT JOIN FETCH si.product
        WHERE s.id IN :ids
    """)
    List<Shipment> findShipmentsWithItemsByIds(@Param("ids") List<Long> ids);
}
