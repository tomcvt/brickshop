package com.tomcvt.brickshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomcvt.brickshop.model.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdOrderByImgOrderAsc(Long productId);
    Optional<ProductImage> findByProductIdAndImgOrder(Long productId, Integer imgOrder);
    @Query("""
    SELECT pi
    FROM ProductImage pi
    WHERE pi.product.id = :productId        
    """)
    List<ProductImage> findAllByProductId(@Param("productId") Long productId);
    @Query("""
    SELECT pi
    FROM ProductImage pi
    WHERE pi.product.id = :productId
    ORDER BY pi.imgOrder ASC        
    """)
    List<ProductImage> findAllByProductIdOrdered(@Param("productId") Long productId);
}
