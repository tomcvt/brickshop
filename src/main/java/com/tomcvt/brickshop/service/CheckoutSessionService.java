package com.tomcvt.brickshop.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.dto.CheckoutDataDto;
import com.tomcvt.brickshop.enums.*;
import com.tomcvt.brickshop.exception.EmptyCartException;
import com.tomcvt.brickshop.model.*;
import com.tomcvt.brickshop.repository.*;


@Service
public class CheckoutSessionService {
    private final static Logger log = LoggerFactory.getLogger(CheckoutSessionService.class);
    private final CheckoutSessionRepository checkoutSessionRepository;
    private final ShipmentAddressRepository shipmentAddressRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderCreator orderCreator;
    private final ShipmentCreator shipmentCreator;

    public CheckoutSessionService(
            CheckoutSessionRepository checkoutSessionRepository,
            CartRepository cartRepository,
            ShipmentAddressRepository shipmentAddressRepository,
            OrderRepository orderRepository,
            CartService cartService,
            OrderCreator orderCreator, 
            ShipmentCreator shipmentCreator) {
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.cartRepository = cartRepository;
        this.shipmentAddressRepository = shipmentAddressRepository;
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderCreator = orderCreator;
        this.shipmentCreator = shipmentCreator;
    }

    @Transactional
    public CheckoutDataDto getActiveCheckoutSessionForUser(User user) {
        Optional<CheckoutSession> optSession = checkoutSessionRepository.findActiveSessionByUser(user);
        if (optSession.isPresent()) {
            return new CheckoutDataDto(optSession.get().getId().toString(),null,null);
        }

        Cart cart = cartRepository.findActiveCartByUserId(user.getId())
                .orElseThrow(() -> new EmptyCartException("Can't checkout empty cart"));
        Long itemCount = cartRepository.countItemsInActiveCartById(cart.getId());
        if (itemCount == 0) {
            throw new EmptyCartException("Can't checkout empty cart");
        }
        CheckoutSession session = new CheckoutSession();
        session.setCart(cart);
        session.setUser(user);
        session.setActive(true);
        cartRepository.save(cart);
        session = checkoutSessionRepository.save(session);
        return new CheckoutDataDto(session.getId().toString(), null, null);
    }

    public CheckoutSession getSessionIfExists(UUID uuid) {
        return checkoutSessionRepository.findById(uuid).orElse(null);
    }

    //TODO think about payment method implementation, and maybe it should be in order service
    @Transactional
    public Order closeSessionAndCreateOrderForUser(User user, CheckoutDataDto checkoutData) {
        //TODO implement Payment Method
        
        Long addressId = checkoutData.getShipmentAddressId();
        if (addressId == null) throw new IllegalArgumentException("No address selected");
        ShipmentAddress address = shipmentAddressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("No such address"));
        String addressString = address.addressToString();

        PaymentMethod paymentMethod = PaymentMethod.fromCode(checkoutData.getPaymentMethodId());
        if (paymentMethod == null) throw new IllegalArgumentException("No payment method selected");
        
        UUID sessionId = UUID.fromString(checkoutData.getUuidData());
        CheckoutSession session = checkoutSessionRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("No such session"));
        //After basic checks, check if products are still available in stock
        Long cartId = session.getCart().getId();
        Cart cart = cartService.lockCartAndProducts(cartId);
        session.setActive(false);
        checkoutSessionRepository.save(session);
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
                //Assuming it's a unique constraint violation on orderId
                orderId = orderRepository.findMaxOrderId() + 1L;
                log.warn("Order ID conflict detected, retrying with new order ID: " + orderId);
                continue;
            }
        }
        shipmentCreator.createShipmentForOrder(newOrder);
        return newOrder;
    }
}
