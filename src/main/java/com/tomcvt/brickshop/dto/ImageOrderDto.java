package com.tomcvt.brickshop.dto;

import java.util.List;

public class ImageOrderDto {
    private Long productId;
    private List<String> imageUrls;

    public ImageOrderDto(Long productId, List<String> imageUrls) {
        this.productId = productId;
        this.imageUrls = imageUrls;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public List<String> getImageUrls() {
        return imageUrls;
    }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
