package com.tomcvt.brickshop.model;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private Integer imgOrder;
    @Column(name = "image_uuid", columnDefinition = "uuid", nullable = false, unique = true)
    private UUID imageUuid = UUID.randomUUID();

    public ProductImage() {}

    public ProductImage(Product product, Integer imgOrder, String imageUuid) {
        this.product = product;
        this.imgOrder = imgOrder;
        this.imageUuid = UUID.fromString(imageUuid);
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
    public Integer getImgOrder() {
        return imgOrder;
    }
    public void setImgOrder(Integer imgOrder) {
        this.imgOrder = imgOrder;
    }
    public UUID getImageUuid() {
        return imageUuid;
    }
    public void setImageUuid(UUID imageUuid) {
        this.imageUuid = imageUuid;
    }
}
