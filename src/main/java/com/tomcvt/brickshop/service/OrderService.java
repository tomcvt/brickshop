package com.tomcvt.brickshop.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.repository.OrderRepository;
import com.tomcvt.brickshop.repository.UserRepository;
import com.tomcvt.brickshop.searching.OrderSearchCriteria;
import com.tomcvt.brickshop.searching.OrderSpecifications;
import com.tomcvt.brickshop.utility.mockpayment.PaymentToken;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentProviderClient paymentProviderClient;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
            PaymentProviderClient paymentProviderClient) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.paymentProviderClient = paymentProviderClient;
    }

    public Order getOrderForSessionId(UUID uuid) {
        return orderRepository.findByCheckoutSessionIdWithTransaction(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"No order created for session"));
    }

    public Order getOrderByOrderId(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not found"));
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

    @Transactional
    public boolean verifyPaymentAndUpdateOrderStatus(Long orderId, User user) {
        Order order = orderRepository.findByOrderIdAndUser(orderId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found"));
        String token = order.getCurrentTransaction().getPaymentToken();
        //TODO handle exceptions and errors
        PaymentToken paymentToken = paymentProviderClient.verifyPaymentToken(token).block();
        if (paymentToken == null || !paymentToken.status().equals("verified")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment token could not be verified");
        }
        order.setStatus(OrderStatus.PROCESSING);
        order.getCurrentTransaction().setStatus(PaymentStatus.COMPLETED);
        orderRepository.save(order);
        return true;
    }

    public Page<Order> searchOrdersByCriteria(
            String username, String status, String paymentMethod, String createdBefore, Pageable pageable) {
        Sort sort = Sort.by("createdAt").descending();
        if (username.isEmpty()) username = null;
        OrderStatus orderStatus = status != null ? parseOrderStatus(status) : null;
        PaymentMethod paymentMethodEnum = paymentMethod != null ? parsePaymentMethod(paymentMethod) : null;
        Instant createdBeforeInstant = Instant.parse(createdBefore);
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
