package com.tomcvt.brickshop.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductEditDto {
    private Long id;
    private String name;
    private String description;
    //private int stock;
    private BigDecimal price;
    private List<String> imageUrls;
    public ProductEditDto() {}
    public ProductEditDto(Long id, String name, String description, BigDecimal price, List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public List<String> getImageUrls() {
        return imageUrls;
    }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
