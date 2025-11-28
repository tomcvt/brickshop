package com.tomcvt.brickshop.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.tomcvt.brickshop.dto.CartDto;
import com.tomcvt.brickshop.dto.FlatCartRowDto;
import com.tomcvt.brickshop.exception.ProductNotFoundException;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.repository.ProductRepository;

@Component
@SessionScope
public class TempCart {
    private final ProductRepository productRepository;
    private List<TempCartItem> items = new ArrayList<>();

    public TempCart(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<TempCartItem> getTempCartItems() {
        return items;
    }

    // TODO refactor to get list of products in one query
    public CartDto getTempCartWithTotal() {
        BigDecimal total = BigDecimal.ZERO;
        List<FlatCartRowDto> fcrDtos = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            TempCartItem tci = items.get(i);
            Product product = productRepository.findByPublicId(tci.getProductPublicId()).orElseThrow();
            fcrDtos.add(new FlatCartRowDto(null, Long.valueOf(i), tci.getQuantity(), tci.getProductPublicId(),
                    product.getName(), product.getPrice(), product.getThumbnailUuid()));
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(tci.getQuantity())));
        }
        return new CartDto(fcrDtos, total);
    }

    public FlatCartRowDto addCartItem(UUID productPublicId, int quantity) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProductPublicId().equals(productPublicId)) {
                items.get(i).setQuantity(items.get(i).getQuantity() + quantity);
                Product product = productRepository.findByPublicId(productPublicId)
                        .orElseThrow(() -> new ProductNotFoundException(
                                "Product not found with publicId: " + productPublicId));
                return new FlatCartRowDto(
                        null,
                        Long.valueOf(i),
                        items.get(i).getQuantity(),
                        items.get(i).getProductPublicId(),
                        product.getName(),
                        product.getPrice(),
                        product.getThumbnailUuid());
            }
        }
        Product product = productRepository.findByPublicId(productPublicId)
                .orElseThrow();
        FlatCartRowDto fcrDto = new FlatCartRowDto(
                null,
                Long.valueOf(items.size()),
                quantity,
                productPublicId,
                product.getName(),
                product.getPrice(),
                product.getThumbnailUuid());
        items.add(new TempCartItem(productPublicId, quantity));
        return fcrDto;
    }

    public List<FlatCartRowDto> getActiveTempFlatCartRowDto() {
        List<FlatCartRowDto> dtoList = items.stream()
                .map(tempcartitem -> {
                    Product product = productRepository.findByPublicId(tempcartitem.getProductPublicId()).orElseThrow();
                    return new FlatCartRowDto(
                            null,
                            Long.valueOf(items.indexOf(tempcartitem)),
                            tempcartitem.getQuantity(),
                            tempcartitem.getProductPublicId(),
                            product.getName(),
                            product.getPrice(),
                            product.getThumbnailUuid());
                })
                .toList();
        return dtoList;
    }

    public void removeTempCartItem(Long cartItemId) {
        TempCartItem cartItem = items.get(cartItemId.intValue());
        if (cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);
        } else {
            items.remove(cartItem);
        }
    }

    public BigDecimal getTotal() {
        if (items.size() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = items.stream()
                .map(tempCartItem -> {
                    Product product = productRepository.findByPublicId(tempCartItem.getProductPublicId()).orElseThrow();
                    return product.getPrice().multiply(BigDecimal.valueOf(tempCartItem.getQuantity()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    public void clear() {
        items.clear();
    }
}
