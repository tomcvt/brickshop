package com.tomcvt.brickshop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.dto.CartDto;
import com.tomcvt.brickshop.dto.FlatCartRowDto;
import com.tomcvt.brickshop.exception.NotAuthorizedException;
import com.tomcvt.brickshop.exception.NotInStockException;
import com.tomcvt.brickshop.model.*;
import com.tomcvt.brickshop.repository.*;
import com.tomcvt.brickshop.session.TempCart;
import com.tomcvt.brickshop.session.TempCartItem;

import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final static Logger log = LoggerFactory.getLogger(CartService.class);
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public List<FlatCartRowDto> getActiveFlatCartDtoByUserId(Long userId) {
        Optional<Long> optActiveCartId = cartRepository.findActiveCartIdByUserId(userId);
        if (optActiveCartId.isPresent()) {
            return cartRepository.findFlatCartById(optActiveCartId.get());
        }
        return List.of();
    }
    @Transactional Cart getActiveCartByUserId(Long userId) {
        Optional<Cart> optActiveCart = cartRepository.findActiveCartByUserId(userId);
        if (optActiveCart.isPresent()) {
            return optActiveCart.get();
        }
        Cart newCart = new Cart();
        newCart.setUserId(userId);
        newCart.setActive(true);
        newCart = cartRepository.save(newCart);
        return newCart;
    }

    @Transactional 
    public FlatCartRowDto addProductToActiveUserCart(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return addProductToActiveUserCart(userId, product, quantity);
    }

    @Transactional
    public FlatCartRowDto addProductToActiveUserCart(Long userId, UUID productPublicId, int quantity) {
        Product product = productRepository.findByPublicId(productPublicId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return addProductToActiveUserCart(userId, product, quantity);
    }

    //TODO refactor to not fetch every time???? why not? how else to check stock?
    @Transactional
    public FlatCartRowDto addProductToActiveUserCart(Long userId, Product product, int quantity) {
        Cart cart = getActiveCartByUserId(userId);
        Optional<CartItem> optCartItem = cartItemRepository.findByCartAndProduct(cart, product);
        CartItem cartItem = null;
        if (optCartItem.isPresent()) {
            cartItem = optCartItem.get();
            if (product.getStock() < cartItem.getQuantity() + quantity) {
                throw new NotInStockException("Not enough stock for product: " + product.getName()
                        + ", available: " + product.getStock() + ", requested: "
                        + (cartItem.getQuantity() + quantity));
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            if (product.getStock() < quantity) {
                throw new NotInStockException("Not enough stock for product: " + product.getName()
                        + ", available: " + product.getStock() + ", requested: " + quantity);
            }
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return new FlatCartRowDto(cart.getId(), cartItem.getId(), quantity, product.getPublicId(), product.getName(),
                product.getPrice(), product.getThumbnailUuid());
    }

    @Transactional
    public void removeCartItemByIdAndUserId(Long cartItemId, Long userId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!cartItem.getCart().getUserId().equals(userId)) {
            throw new NotAuthorizedException("Not authorized to modify this cart item");
        }
        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
            cartItemRepository.save(cartItem);
        } else {
            cartItemRepository.deleteById(cartItemId);
        }
    }

    public BigDecimal getTotalAmount(Long userId) {
        Long cartId = cartRepository.findActiveCartIdByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No active cart"));
        return cartRepository.calculateTotalPriceById(cartId);
    }

    // TODO Cart with CartItems -> to order.ProductList(Product(productid,
    // quantity);

    public CartDto getActiveCartDtoByUserId(Long userId) {
        List<FlatCartRowDto> items = getActiveFlatCartDtoByUserId(userId);
        if (items.size() == 0) {
            return new CartDto(List.of(), BigDecimal.ZERO);
        }
        BigDecimal totalAmount = items.stream().map(row -> row.price().multiply(BigDecimal.valueOf(row.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto(items, totalAmount);
    }

    public CartDto getCartDtoByIdAndUserId(Long cartId, Long userId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (!cart.getUserId().equals(userId)) {
            throw new NotAuthorizedException("Not authorized to access this cart");
        }
        List<FlatCartRowDto> items = cartRepository.findFlatCartById(cartId);
        if (items.size() == 0) {
            return new CartDto(List.of(), BigDecimal.ZERO);
        }
        BigDecimal totalAmount = items.stream().map(row -> row.price().multiply(BigDecimal.valueOf(row.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartDto(items, totalAmount);
    }

    public BigDecimal calculateTotalCartPrice(Long cartId) {
        return cartRepository.calculateTotalPriceById(cartId);
    }

    @Transactional
    public Cart lockCartAndProducts(Long cartId) {
        Cart cart = cartRepository.findCartWithItemsAndProductsById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        if (!cart.isActive()) {
            throw new IllegalStateException("Cart is not active");
        }
        cart.setActive(false);
        for (CartItem cartitem : cart.getItems()) {
            Product product = cartitem.getProduct();
            if (product.getStock() < cartitem.getQuantity()) {
                throw new NotInStockException(
                        "Not enough stock for product: " + product.getName() + ", available: " + product.getStock()
                                + ", requested: " + cartitem.getQuantity());
            }
            product.setStock(product.getStock() - cartitem.getQuantity());
            productRepository.save(product);
        }

        //BigDecimal totalAmount = cartRepository.calculateTotalPriceById(cartId);
        //List<FlatCartRowDto> itemsdto = cart.getItems().stream().map(CartItem::toFlatCartRowDto).toList();
        //return new CartDto(itemsdto, totalAmount);

        cart = cartRepository.save(cart);
        return cart;
    }

    //batch add from temp cart to user active cart
    @Transactional
    public void cartTempCartItemsToUserActiveCart(Long userId, TempCart tempCart) {
        for (TempCartItem item : tempCart.getTempCartItems()) {
            addProductToActiveUserCart(userId, item.getProductPublicId(), item.getQuantity());
        }
        tempCart.clear();
    }
}
