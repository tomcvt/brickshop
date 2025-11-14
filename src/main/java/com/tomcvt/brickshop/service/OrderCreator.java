package com.tomcvt.brickshop.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.enums.PaymentStatus;
import com.tomcvt.brickshop.model.Cart;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.TransactionEntity;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.repository.CartRepository;
import com.tomcvt.brickshop.repository.OrderRepository;

@Service
public class OrderCreator {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final TransactionCreator transactionCreator;
    public OrderCreator(OrderRepository orderRepository, CartRepository cartRepository, 
            TransactionCreator transactionCreator) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.transactionCreator = transactionCreator;
    }
    @Transactional
    public Long getNextOrderId() {
        Long maxOrderId = orderRepository.findMaxOrderId();
        return maxOrderId == null ? 12345678L : maxOrderId + 1L;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order createOrderFromSession(UUID sessionId, 
    User user, String addressString, PaymentMethod paymentMethod, Cart cart, Long orderId) {
        Order newOrder = new Order();
        newOrder.setCart(cart);
        newOrder.setOrderId(orderId);
        newOrder.setCheckoutSessionId(sessionId);
        newOrder.setUser(user);
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setTotalAmount(cartRepository.calculateTotalPriceById(cart.getId()));
        newOrder.setShippingAddressString(addressString);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder = orderRepository.save(newOrder);
        TransactionEntity transaction = transactionCreator.createTransaction(newOrder);
        newOrder.setCurrentTransaction(transaction);
        newOrder = orderRepository.save(newOrder);
        return newOrder;
    }
}
