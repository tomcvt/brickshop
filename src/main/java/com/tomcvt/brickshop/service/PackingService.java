package com.tomcvt.brickshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.dto.ShipmentDto;
import com.tomcvt.brickshop.enums.ShipmentItemStatus;
import com.tomcvt.brickshop.enums.ShipmentStatus;
import com.tomcvt.brickshop.model.Shipment;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.repository.ShipmentRepository;

@Service
public class PackingService {
    private final ShipmentRepository shipmentRepository;

    public PackingService(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public ShipmentDto getShipmentHydratedByOrderOrderId(Long orderId) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        return shipment.toShipmentDto();
    }

    public SimplePage<ShipmentDto> getShipmentsByStatus(ShipmentStatus status, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Long> shipmentIds = shipmentRepository.findIdsByStatus(status, pageable);
        List<Shipment> shipments = shipmentRepository.findShipmentsWithItemsByIds(shipmentIds.getContent());
        return SimplePage.of(shipments, shipmentIds).map(Shipment::toShipmentDto);
    }

    public SimplePage<ShipmentDto> getShipmentsToPack(int page, int size) {
        //TODO AND NOW HERE WE GO WITH PAGINATION
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Long> shipmentIds = shipmentRepository.findIdsByStatus(ShipmentStatus.PENDING, pageable);
        List<Shipment> shipments = shipmentRepository.findShipmentsWithItemsByIds(shipmentIds.getContent());
        return SimplePage.of(shipments, shipmentIds).map(Shipment::toShipmentDto);
    }

    //TODO refactor to custom exception and better logging
    @Transactional
    public ShipmentDto startPackingShipment(Long orderId, User user) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new RuntimeException("Shipment is not in PENDING status");
        }
        shipment.setStatus(ShipmentStatus.PACKING);
        shipment.setPackedBy(user);
        shipment.getItems().forEach(item -> item.setStatus(ShipmentItemStatus.PACKING));
        shipment = shipmentRepository.save(shipment);
        return shipment.toShipmentDto();
    }

    @Transactional
    public ShipmentDto packItemInShipment(Long orderId, Long productId) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        var shipmentItem = shipment.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (shipmentItem.getStatus() != ShipmentItemStatus.PACKING) {
            throw new RuntimeException("Shipment is not in packing status");
        }
        shipmentItem.setStatus(ShipmentItemStatus.PACKED);
        if(shipment.getItems().stream().allMatch(item -> item.getStatus() == ShipmentItemStatus.PACKED)) {
            shipment.setStatus(ShipmentStatus.PACKED);
        }
        shipment = shipmentRepository.save(shipment);
        return shipment.toShipmentDto();
    }

    @Transactional
    public ShipmentDto packAllItemsInShipment(Long orderId) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        if (shipment.getStatus() != ShipmentStatus.PACKING) {
            throw new RuntimeException("Shipment is not in packing status");
        }
        shipment.getItems().forEach(item -> item.setStatus(ShipmentItemStatus.PACKED));
        shipment.setStatus(ShipmentStatus.PACKED);
        shipment = shipmentRepository.save(shipment);
        return shipment.toShipmentDto();
    }
    

}
