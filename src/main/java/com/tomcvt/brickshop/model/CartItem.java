package com.tomcvt.brickshop.model;

import org.hibernate.annotations.BatchSize;

import com.tomcvt.brickshop.dto.FlatCartRowDto;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cart_items_cart_id_product_id", columnList = "cart_id, product_id", unique = true)
})
@BatchSize(size = 20)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private int quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public CartItem() {}
    public CartItem(Product product, int quantity, Cart cart) {
        this.product = product;
        this.quantity = quantity;
        this.cart = cart;
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public FlatCartRowDto toFlatCartRowDto() {
        return new FlatCartRowDto(
                this.cart.getId(),
                this.id,
                this.quantity,
                this.product.getPublicId(),
                this.product.getName(),
                this.product.getPrice(),
                this.product.getThumbnailUuid()
        );
    }
}
