package com.tomcvt.brickshop.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tomcvt.brickshop.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    //TODO implement paging
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            WHERE (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<Product> findByNameOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);

    //TODO track where to use, if need to fetch categories
    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.productImages pi
            WHERE p.id = :id
            ORDER BY pi.imgOrder ASC
            """)
    Optional<Product> findByIdHydrated(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT p.id
            FROM Product p
            JOIN p.categories c
            WHERE c.id IN :categoryIds
            """)
    Page<Long> findIdsByCategoryIds(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("""
            SELECT DISTINCT p.id
            FROM Product p
            JOIN p.categories c
            WHERE c.id IN :categoryIds AND (
                LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
    """)
    Page<Long> findIdsByCategoryIdsAndKeyword(@Param("categoryIds") List<Long> categoryIds, @Param("keyword") String keyword, Pageable pageable);


    @Query("""
            SELECT DISTINCT p
            FROM Product p
            WHERE p.id IN :ids
            """)
    List<Product> findByIds(@Param("ids") List<Long> ids);
    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.categories
            WHERE p.id IN :ids 
            """)
    List<Product> findByIdsWithCategories(@Param("ids") List<Long> ids);

    @Query("""
            SELECT DISTINCT p.description
            FROM Product p
            """)
    List<String> findAllDescriptions();

    Optional<Product> findByPublicId(UUID publicId);

    @Query("""
            SELECT DISTINCT p
            FROM Product p
            LEFT JOIN FETCH p.productImages pi
            WHERE p.publicId = :publicId
            ORDER BY pi.imgOrder ASC
            """)
    Optional<Product> findByPublicIdHydrated(UUID publicId);
    //TODO maybe later project on intermediate DTO
    
}
