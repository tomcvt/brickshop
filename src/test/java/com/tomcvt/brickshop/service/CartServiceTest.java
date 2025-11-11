package com.tomcvt.brickshop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tomcvt.brickshop.model.Cart;
import com.tomcvt.brickshop.model.CartItem;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.repository.CartItemRepository;
import com.tomcvt.brickshop.repository.CartRepository;
import com.tomcvt.brickshop.repository.ProductRepository;
import com.tomcvt.brickshop.session.TempCart;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock CartRepository cartRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock ProductRepository productRepository;

    @InjectMocks
    CartService cartService; 

    // Merge temp cart test and calling repository save
    @Test
    void testCartTempCartItemsToUserActiveCart() {
        // Setup mock data
        Long userId = 1L;
        TempCart tempCart = new TempCart(productRepository);
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Product testProduct1 = new Product();
        Product testProduct2 = new Product();
        testProduct1.setPublicId(productId1);
        testProduct2.setPublicId(productId2);
        testProduct1.setStock(20);
        testProduct2.setStock(10);
        when(productRepository.findByPublicId(productId1)).thenReturn(Optional.of(testProduct1));
        when(productRepository.findByPublicId(productId2)).thenReturn(Optional.of(testProduct2));
        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(testProduct1)))
                .thenReturn(Optional.of(new CartItem(testProduct1, 1)));
        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(testProduct2)))
                .thenReturn(Optional.of(new CartItem(testProduct2, 1)));
        tempCart.addCartItem(productId1, 2);
        tempCart.addCartItem(productId1, 3); // same product, should merge
        tempCart.addCartItem(productId2, 1);

        Cart activeCart = new Cart();
        activeCart.setId(10L);
        activeCart.setUserId(userId);
        activeCart.setActive(true);
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(activeCart));

        // Call the method under test
        cartService.cartTempCartItemsToUserActiveCart(userId, tempCart);
        // Verify interactions and state
        verify(cartItemRepository, times(1)).save(argThat(cartItem -> 
            cartItem.getQuantity() == 6 && cartItem.getProduct().equals(testProduct1)
        ));
        verify(cartItemRepository, times(1)).save(argThat(cartItem -> 
            cartItem.getQuantity() == 2 && cartItem.getProduct().equals(testProduct2)
        ));
    }
}
