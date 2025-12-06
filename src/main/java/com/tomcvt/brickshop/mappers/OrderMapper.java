package com.tomcvt.brickshop.mappers;

import java.util.List;

import com.tomcvt.brickshop.dto.CartDto;
import com.tomcvt.brickshop.dto.OrderFullDto;
import com.tomcvt.brickshop.dto.TransactionDto;
import com.tomcvt.brickshop.model.Order;

public class OrderMapper {
    public static final OrderMapper INSTANCE = new OrderMapper();
    private final CartMapper cartMapper = new CartMapper();
    public OrderMapper() {
    }

    public OrderFullDto toOrderFullDto(Order order) {
        //TODO: map cart and tranactions properly
        List<TransactionDto> transactionDtos = order.getTransactions().stream()
            .map(t -> new TransactionDto(
                t.getTransactionId(),
                t.getCreatedAt().toString(),
                t.getUpdatedAt().toString(),
                t.getStatus(),
                t.getPaymentMethod(),
                t.getAmount(),
                t.getPaymentToken()
            ))
            .toList();
        CartDto cartDto = cartMapper.toCartDto(order.getCart());
        return new OrderFullDto(
            order.getOrderId(),
            order.getUser().getUsername(),
            order.getUser().getEmail(),
            order.getCart().getId(),
            order.getShippingAddressString(),
            order.getStatus().toString(),
            order.getCreatedAt().toString(),
            order.getTotalAmount(),
            order.getPaymentMethod().toString(),
            transactionDtos,
            cartDto,
            order.getCurrentTransaction() != null ? order.getCurrentTransaction().toTransactionDto() : null,
            order.getCheckoutSessionId()
        );
    }

}
