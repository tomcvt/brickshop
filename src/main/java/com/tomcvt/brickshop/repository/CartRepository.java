package com.tomcvt.brickshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomcvt.brickshop.dto.FlatCartRowDto;
import com.tomcvt.brickshop.model.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    @Query("""
    SELECT c
    FROM Cart c
    JOIN FETCH c.items ci
    JOIN FETCH ci.product p
    WHERE c.id = :id
    """)
    Optional<Cart> findCartWithProducts(@Param("id") Long id);

    @Query("""
    SELECT DISTINCT c
    FROM Cart c
    LEFT JOIN FETCH c.items ci
    LEFT JOIN FETCH ci.product p
    WHERE c.userId = :userId AND c.active = TRUE
    """)
    Optional<Cart> findHydratedActiveCartByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT DISTINCT c
    FROM Cart c
    WHERE c.userId = :userId AND c.active = TRUE
    """)
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId);
    
    @Query("""
    SELECT COUNT(ci)
    FROM Cart c
    JOIN c.items ci
    WHERE c.id = :id AND c.active = TRUE 
    """)
    Long countItemsInActiveCartById(@Param("id") Long id);
    //TODO CART BY USER NOT USERID

    @Query("""
    SELECT c.id
    FROM Cart c
    WHERE c.userId = :userId AND c.active = TRUE
    """)
    Optional<Long> findActiveCartIdByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT new com.tomcvt.brickshop.dto.FlatCartRowDto(
    c.id,
    ci.id,
    ci.quantity,
    p.publicId,
    p.name,
    p.price,
    p.thumbnailUuid
    )
    FROM Cart c
    JOIN c.items ci
    JOIN ci.product p
    WHERE c.id = :id
    """)
    List<FlatCartRowDto> findFlatCartById(@Param("id") Long id);

    @Query("""
        SELECT c
        FROM Cart c
        JOIN FETCH c.items ci
        JOIN FETCH ci.product p
        WHERE c.id = :id
    """)
    Optional<Cart> findCartWithItemsAndProductsById(@Param("id") Long id);

    @Query("""
    SELECT SUM(ci.quantity * price)
    FROM Cart c
    JOIN c.items ci
    JOIN ci.product p
    WHERE c.id = :id
    """)
    BigDecimal calculateTotalPriceById(@Param("id") Long id);
}
