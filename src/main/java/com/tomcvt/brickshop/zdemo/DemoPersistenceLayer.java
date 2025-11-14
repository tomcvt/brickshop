package com.tomcvt.brickshop.zdemo;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.model.Cart;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.ShipmentAddress;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.CartRepository;
import com.tomcvt.brickshop.repository.OrderRepository;
import com.tomcvt.brickshop.repository.ShipmentAddressRepository;
import com.tomcvt.brickshop.service.OrderCreator;
import com.tomcvt.brickshop.service.ShipmentCreator;

@Component
@Profile({"dev", "demo"})
public class DemoPersistenceLayer {
    private final ShipmentAddressRepository shipmentAddressRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderCreator orderCreator;
    private final ShipmentCreator shipmentCreator;

    public DemoPersistenceLayer(
            ShipmentAddressRepository shipmentAddressRepository,
            CartRepository cartRepository,
            OrderRepository orderRepository,
            OrderCreator orderCreator,
            ShipmentCreator shipmentCreator) {
        this.shipmentAddressRepository = shipmentAddressRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderCreator = orderCreator;
        this.shipmentCreator = shipmentCreator;
    }

    @Transactional
    public Order createDemoOrder(User user) {
        //TODO implement Payment Method
        
        ShipmentAddress address = shipmentAddressRepository.findById(1L).orElseThrow(() -> new RuntimeException("No such address"));
        String addressString = address.addressToString();
        PaymentMethod paymentMethod = PaymentMethod.PAYPAL;
        UUID sessionId = UUID.randomUUID();
        //Reusing demo cart
        Cart cart = cartRepository.findById(1L).orElseThrow(() -> new RuntimeException("No demo cart found"));
        Long maxOrderId = orderRepository.findMaxOrderId();
        Long orderId = maxOrderId == null ? 12345678L : maxOrderId + 1L;
        // we relly here on having first mock order here, we can do this conditionally with null check too
        boolean created = false;
        Order newOrder = null;
        while (!created) {
            try {
                newOrder = orderCreator.createOrderFromSession(sessionId, user, addressString, paymentMethod, cart, orderId);
                created = true;
            } catch (DataIntegrityViolationException e) {
                orderId = orderRepository.findMaxOrderId() + 1L;
                //log.warn("Order ID conflict detected, retrying with new order ID: " + orderId);
                continue;
            }
        }
        shipmentCreator.createShipmentForOrder(newOrder);
        return newOrder;
    }
}
