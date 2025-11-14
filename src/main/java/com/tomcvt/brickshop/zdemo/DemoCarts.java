package com.tomcvt.brickshop.zdemo;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.tomcvt.brickshop.dto.FlatCartRowDto;
import com.tomcvt.brickshop.model.Cart;
import com.tomcvt.brickshop.service.CartService;

@Component
@Profile({ "dev", "demo" })
public class DemoCarts {
    private final DemoCache demoCache;
    private final CartService cartService;

    public DemoCarts(DemoCache demoCache, CartService cartService) {
        this.demoCache = demoCache;
        this.cartService = cartService;
    }

    public void createDemoCarts() {
        Long userId = 1L;
        for (int j = 0; j < 20; j++) {
            FlatCartRowDto fcrdto = null;
            for (int i = 1; i < 6; i++) {
                fcrdto = cartService.addProductToActiveUserCart(userId, Long.valueOf(j + i), i);
            }
            Long cartId = fcrdto != null ? fcrdto.cartId() : 1L;
            Cart cart = cartService.lockCartAndProducts(cartId);
            demoCache.demoClosedCartsIds.add(cart.getId());
        }
    }

}
