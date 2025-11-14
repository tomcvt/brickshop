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
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DemoPersistenceLayer.class);
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
    public Order createDemoOrder(User user, Long cartId) {
        //TODO implement Payment Method
        
        ShipmentAddress address = shipmentAddressRepository.findById(1L).orElseThrow(() -> new RuntimeException("No such address"));
        String addressString = address.addressToString();
        PaymentMethod paymentMethod = PaymentMethod.PAYPAL;
        //for now detached from session
        UUID sessionId = UUID.randomUUID();
        //Reusing demo cart
        //TODO think about using cartId in business logic to not fetch the cart when not needed
        //TODO handle possible DataIntegrityViolationException on order ID conflict
        //implement counter with retries, after 10 rethrow exception
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("No demo cart found"));
        Long orderId = orderCreator.getNextOrderId();
        // we relly here on having first mock order here, we can do this conditionally with null check too
        boolean created = false;
        Order newOrder = null;
        while (!created) {
            try {
                log.info("Creating demo order with order ID: " + orderId);
                newOrder = orderCreator.createOrderFromSession(sessionId, user, addressString, paymentMethod, cart, orderId);
                created = true;
            } catch (DataIntegrityViolationException e) {
                orderId = orderCreator.getNextOrderId();
                log.warn("Order ID conflict detected, retrying with new order ID: " + orderId);
                log.error("Exception details", e);
                continue;
            }
        }
        shipmentCreator.createShipmentForOrder(newOrder);
        return newOrder;
    }
}
