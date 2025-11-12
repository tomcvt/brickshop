package com.tomcvt.brickshop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.enums.ShipmentItemStatus;
import com.tomcvt.brickshop.enums.ShipmentStatus;
import com.tomcvt.brickshop.model.Cart;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.Shipment;
import com.tomcvt.brickshop.model.ShipmentItem;
import com.tomcvt.brickshop.repository.CartRepository;
import com.tomcvt.brickshop.repository.ShipmentItemRepository;
import com.tomcvt.brickshop.repository.ShipmentRepository;

@Service
public class ShipmentCreator {
    
    private final ShipmentRepository shipmentRepository;
    private final CartRepository cartRepository;
    private final ShipmentItemRepository shipmentItemRepository;

    public ShipmentCreator(ShipmentRepository shipmentRepository, CartRepository cartRepository, ShipmentItemRepository shipmentItemRepository) {
        this.shipmentRepository = shipmentRepository;
        this.cartRepository = cartRepository;
        this.shipmentItemRepository = shipmentItemRepository;
    }

    @Transactional
    public Shipment createShipmentForOrder(Order order) {
        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setPackedBy(null);
        shipment.setTrackingNumber(null);
        shipment.setAddressString(order.getShippingAddressString());
        Shipment savedShipment = shipmentRepository.save(shipment);
        // Additional initialization logic can go here
        Cart hydratedCart = cartRepository.findHydratedCartById(order.getCart().getId())
                .orElseThrow(() -> new IllegalStateException("Cart not found for order"));
        List<ShipmentItem> shipmentItems = hydratedCart.getItems().stream().map(cartItem -> {
            ShipmentItem shipmentItem = new ShipmentItem();
            shipmentItem.setShipment(savedShipment);
            shipmentItem.setProduct(cartItem.getProduct());
            shipmentItem.setQuantity(cartItem.getQuantity());
            shipmentItem.setStatus(ShipmentItemStatus.PENDING);
            return shipmentItem;
        }).collect(Collectors.toList());
        shipmentItemRepository.saveAll(shipmentItems);
        savedShipment.setItems(shipmentItems);
        return shipmentRepository.save(savedShipment);
    }


}
