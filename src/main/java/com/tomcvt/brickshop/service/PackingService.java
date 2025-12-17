package com.tomcvt.brickshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.dto.ShipmentDto;
import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.ShipmentItemStatus;
import com.tomcvt.brickshop.enums.ShipmentStatus;
import com.tomcvt.brickshop.exception.WrongOperationException;
import com.tomcvt.brickshop.model.Shipment;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.repository.OrderRepository;
import com.tomcvt.brickshop.repository.ShipmentRepository;

@Service
public class PackingService {
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    public PackingService(ShipmentRepository shipmentRepository, OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

    public ShipmentDto getShipmentHydratedByOrderOrderId(Long orderId) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        return shipment.toShipmentDto();
    }

    public SimplePage<ShipmentDto> getShipmentsByStatus(ShipmentStatus status, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Long> shipmentIds = shipmentRepository.findIdsByStatus(status, pageable);
        List<Shipment> shipments = shipmentRepository.findShipmentsByIds(shipmentIds.getContent());
        return SimplePage.of(shipments, shipmentIds).map(Shipment::toShipmentDto);
    }

    public SimplePage<ShipmentDto> getShipmentsToPack(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Long> shipmentIds = shipmentRepository.findIdsByStatus(ShipmentStatus.PENDING, pageable);
        List<Shipment> shipments = shipmentRepository.findShipmentsByIds(shipmentIds.getContent());
        return SimplePage.of(shipments, shipmentIds).map(Shipment::toShipmentDto);
    }

    @Transactional
    public ShipmentDto startPackingShipment(Long orderId, User user) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new WrongOperationException("Shipment is not in PENDING status");
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
            throw new WrongOperationException("Shipment is not in packing status");
        }
        shipmentItem.setStatus(ShipmentItemStatus.PACKED);
        if(shipment.getItems().stream().allMatch(item -> item.getStatus() == ShipmentItemStatus.PACKED)) {
            shipment.setStatus(ShipmentStatus.PACKED);
            markOrderAsPacked(orderId);
        }
        shipment = shipmentRepository.save(shipment);
        return shipment.toShipmentDto();
    }

    @Transactional
    public ShipmentDto packAllItemsInShipment(Long orderId) {
        Shipment shipment = shipmentRepository.findHydratedByOrderOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Shipment not found for order ID: " + orderId));
        if (shipment.getStatus() != ShipmentStatus.PACKING) {
            throw new WrongOperationException("Shipment is not in packing status");
        }
        shipment.getItems().forEach(item -> item.setStatus(ShipmentItemStatus.PACKED));
        shipment.setStatus(ShipmentStatus.PACKED);
        markOrderAsPacked(orderId);
        shipment = shipmentRepository.save(shipment);
        return shipment.toShipmentDto();
    }

    @Transactional
    private void markOrderAsPacked(Long orderId) {
        var order = orderRepository.findByOrderId(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found for order ID: " + orderId));
        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order is not in PROCESSING status");
        }
        order.setStatus(OrderStatus.PACKED);
        orderRepository.save(order);
    }
    

}
