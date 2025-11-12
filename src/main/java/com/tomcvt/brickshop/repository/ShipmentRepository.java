package com.tomcvt.brickshop.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomcvt.brickshop.enums.ShipmentStatus;
import com.tomcvt.brickshop.model.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Long> findShipmentIdsByStatus(ShipmentStatus status, Pageable pageable);

    @Query("""
        SELECT s
        FROM Shipment s
        LEFT JOIN FETCH s.items si
        LEFT JOIN FETCH si.product
        WHERE s.shipmentId IN :shipmentIds
    """)
    List<Shipment> findShipmentsWithItemsByIds(@Param("shipmentIds") List<Long> shipmentIds);
}
