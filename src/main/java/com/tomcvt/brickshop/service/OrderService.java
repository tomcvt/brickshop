package com.tomcvt.brickshop.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.tomcvt.brickshop.clients.PaymentProviderClient;
import com.tomcvt.brickshop.dto.CustomerOrderDto;
import com.tomcvt.brickshop.dto.OrderSummaryDto;
import com.tomcvt.brickshop.enums.OrderStatus;
import com.tomcvt.brickshop.enums.PaymentMethod;
import com.tomcvt.brickshop.enums.PaymentStatus;
import com.tomcvt.brickshop.exception.NoOrderForSessionException;
import com.tomcvt.brickshop.exception.NotAuthorizedException;
import com.tomcvt.brickshop.model.Cart;
import com.tomcvt.brickshop.model.Order;
import com.tomcvt.brickshop.model.TransactionEntity;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.repository.CartRepository;
import com.tomcvt.brickshop.repository.OrderRepository;
import com.tomcvt.brickshop.repository.UserRepository;
import com.tomcvt.brickshop.specifications.OrderSearchCriteria;
import com.tomcvt.brickshop.specifications.OrderSpecifications;
import com.tomcvt.brickshop.utility.mockpayment.PaymentToken;

import jakarta.persistence.EntityManager;

@Service
public class OrderService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PaymentProviderClient paymentProviderClient;
    private final EntityManager entityManager;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
            CartRepository cartRepository,
            PaymentProviderClient paymentProviderClient, EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.paymentProviderClient = paymentProviderClient;
        this.entityManager = entityManager;
    }

    public Order getOrderForSessionId(UUID uuid) {
        return orderRepository.findByCheckoutSessionIdWithTransaction(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"No order created for session"));
    }

    public Order getOrderByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not found"));
    }

    public Optional<Order> findOrderByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public Order getOrderByOrderIdAndUser(Long orderId, User user) {
        // TODO custom exception and handler
        Order order = orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not found"));
        return order;
    }

    public CustomerOrderDto getCustomerOrderDtoForSessionId(UUID uuid, User user) {
        // TODO custom exception and handler
        Order order = getOrderForSessionId(uuid);
        Long userId = user.getId();
        if (!order.getUser().getId().equals(userId) && !user.isStaff()) {
            throw new NotAuthorizedException("Not authorized to view this order");
        }
        return orderRepository.findCustomerOrderDtoByCheckoutSessionId(uuid)
                .orElseThrow(() -> new NoOrderForSessionException("No order for session"));
    }

    public List<CustomerOrderDto> getAllCustomerOrderDtoForUserId(User user) {
        return orderRepository.findCustomerOrderDtosByUser(user);
    }

    public List<OrderSummaryDto> getAllOrderSummariesForUser(User user) {
        return orderRepository.findOrderSummaryDtoByUser(user);
    }

    public String getPaymentTokenForOrder(Long orderId, User user) {
        String token = orderRepository.findPaymentTokenByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No payment token found for order"));
        return token;
    }

    @Transactional(readOnly = true)
    public Order getFullOrderDetailsByOrderId(Long orderId) {
        Order order = orderRepository.findByOrderIdWithTransactionsAndUser(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found"));
        entityManager.detach(order);
        Cart cart = cartRepository.findHydratedCartById(order.getCart().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found"));
        order.setCart(cart);
        return order;
    }


    @Transactional
    public boolean verifyPaymentAndUpdateOrderStatus(Long orderId, User user) {
        Order order = orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found"));
        TransactionEntity transaction = order.getCurrentTransaction();
        String token = transaction.getPaymentToken();
        //TODO handle exceptions and errors
        PaymentToken paymentToken = null;
        try {
            paymentToken = paymentProviderClient.verifyPaymentToken(token).block();
        } catch (WebClientResponseException e) {
            // here logic depends on actual payment provider behavior
            log.error("Error verifying payment token: {}", e.getMessage());
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error verifying payment token: {}", e.getMessage());
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            return false;
        }
        //here logic depends on actual payment provider behavior
        if (paymentToken == null || !paymentToken.status().equals("verified")) {
            log.warn("Payment token verification failed for orderId {}", orderId);
            transaction.setStatus(PaymentStatus.FAILED);
            transaction.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            return false;
        }
        order.setStatus(OrderStatus.PROCESSING);
        transaction.setStatus(PaymentStatus.COMPLETED);
        transaction.setUpdatedAt(Instant.now());
        orderRepository.save(order);
        return true;
    }

    public Page<Order> searchOrdersByCriteria(
            String username, String status, String paymentMethod, String createdBefore, Pageable pageable) {
        Sort sort = Sort.by("createdAt").descending();
        if (username != null && username.isEmpty()) username = null;
        OrderStatus orderStatus = status != null ? parseOrderStatus(status) : null;
        PaymentMethod paymentMethodEnum = paymentMethod != null ? parsePaymentMethod(paymentMethod) : null;
        if (createdBefore != null && createdBefore.isEmpty()) createdBefore = null;
        Instant createdBeforeInstant = null;
        if (createdBefore != null) {
            try {
                createdBeforeInstant = Instant.parse(createdBefore);
            } catch (Exception e) {
                log.warn("Invalid createdBefore format: {} : ", createdBefore, e.getMessage());
                createdBefore = null;
            }
        }
        OrderSearchCriteria criteria = new OrderSearchCriteria(
            username,
            orderStatus,
            paymentMethodEnum,
            createdBeforeInstant
        );
        Specification<Order> spec = OrderSpecifications.withFilters(criteria);
        Pageable pageSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Order> orderPage = orderRepository.findAll(spec, pageSort);
        return orderPage;
    }

    public long getTotalOrderCount() {
        return orderRepository.count();
    }

    private PaymentMethod parsePaymentMethod(String paymentMethodStr) {
        try {
            return PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private OrderStatus parseOrderStatus(String statusStr) {
        try {
            return OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
