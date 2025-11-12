package com.tomcvt.brickshop.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tomcvt.brickshop.model.ShipmentItem;

public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, UUID> {
    
}
