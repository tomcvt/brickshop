package com.tomcvt.brickshop.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.tomcvt.brickshop.dto.ProductDto;
import com.tomcvt.brickshop.model.Product;

public class ProductMapper {
    public static final ProductMapper INSTANCE = new ProductMapper();
    private ProductMapper() {
    }

    public ProductDto toProductDto(Product product) {
        List<String> imageUrls = product.getProductImages().stream()
            .map(img -> img.getImageUuid()+ ".jpg")
            .toList();
        return new ProductDto(
            product.getPublicId(),
            product.getName(),
            product.getDescription(),
            imageUrls,
            product.getPrice(),
            product.getStock(),
            product.getCategories().stream()
                .map(c -> c.getName())
                .collect(Collectors.toSet())
        );
    }

}
