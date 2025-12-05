package com.tomcvt.brickshop.mappers;

import java.math.BigDecimal;

import com.tomcvt.brickshop.dto.CartDto;
import com.tomcvt.brickshop.dto.FlatCartRowDto;
import com.tomcvt.brickshop.model.Cart;

public class CartMapper {
    public CartMapper() {
    }

    public CartDto toCartDto(Cart cart) {
        BigDecimal totalPrice = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var items = cart.getItems().stream()
                .map(item -> new FlatCartRowDto(
                        cart.getId(),
                        item.getId(),
                        item.getQuantity(),
                        item.getProduct().getPublicId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getProduct().getThumbnailUuid()
                ))
                .toList();
        return new CartDto(items, totalPrice);
    }
}
