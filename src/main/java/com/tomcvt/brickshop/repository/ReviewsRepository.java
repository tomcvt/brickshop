package com.tomcvt.brickshop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.model.Review;
import com.tomcvt.brickshop.model.User;

@Repository
public interface ReviewsRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct(Product product, Pageable pageable);
    Page<Review> findByUser(User user, Pageable pageable);
    Optional<Review> findByPublicId(UUID publicId);
    Optional<Review> findByUserAndProduct(User user, Product product);
}
