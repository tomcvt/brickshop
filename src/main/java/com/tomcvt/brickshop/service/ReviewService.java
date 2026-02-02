package com.tomcvt.brickshop.service;

import java.util.Optional;
import java.util.UUID;

import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tomcvt.brickshop.dto.ReviewDto;
import com.tomcvt.brickshop.exception.NotAuthorizedException;
import com.tomcvt.brickshop.exception.NotFoundException;
import com.tomcvt.brickshop.model.Product;
import com.tomcvt.brickshop.model.Review;
import com.tomcvt.brickshop.model.User;
import com.tomcvt.brickshop.pagination.SimplePage;
import com.tomcvt.brickshop.repository.ProductRepository;
import com.tomcvt.brickshop.repository.ReviewsRepository;
import com.tomcvt.brickshop.utility.HtmlPolicies;

@Service
public class ReviewService {
    private final ReviewsRepository reviewsRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewsRepository reviewsRepository, ProductRepository productRepository) {
        this.reviewsRepository = reviewsRepository;
        this.productRepository = productRepository;
    }

    public SimplePage<ReviewDto> getReviewDtosByProductPublicId(UUID productPublicId, int page, int size) {
        var product = productRepository.findByPublicId(productPublicId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var reviewsPage = reviewsRepository.findReviewDtosByProduct(product, pageable);
        return SimplePage.fromPage(reviewsPage);
    }

    public SimplePage<Review> getReviewsByProduct(Product product, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var reviewsPage = reviewsRepository.findByProduct(product, pageable);
        return SimplePage.fromPage(reviewsPage);
    }

    public SimplePage<Review> getReviewsByProductPublicId(UUID productPublicId, int page, int size) {
        var product = productRepository.findByPublicId(productPublicId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return getReviewsByProduct(product, page, size);
    }

    public Optional<Review> getReviewByPublicId(UUID reviewPublicId) {
        return reviewsRepository.findByPublicId(reviewPublicId);
    }

    public Optional<Review> getReviewByUserAndProduct(User user, Product product) {
        return reviewsRepository.findByUserAndProduct(user, product);
    }

    public Optional<Review> getReviewByUserAndProductPublicId(User user, UUID productPublicId) {
        var productOpt = productRepository.findByPublicId(productPublicId);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }
        return getReviewByUserAndProduct(user, productOpt.get());
    }

    public Review editReviewByUser(User user, UUID reviewPublicId, Integer rating, String comment) {
        var review = getReviewByPublicId(reviewPublicId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new NotAuthorizedException("You are not authorized to edit this review");
        }
        return editReview(review, rating, comment);
    }

    public Review editReview(Review review, Integer rating, String comment) {
        review.setRating(rating);
        String sanitizedComment = HtmlPolicies.sanitizeNoHtml(comment);
        review.setComment(sanitizedComment);
        return reviewsRepository.save(review);
    }

    public Review addReview(User user, UUID productPublicId, Integer rating, String comment) {
        Product product = productRepository.findByPublicId(productPublicId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return addReview(user, product, rating, comment);
    }

    public Review addReview(User user, Product product, Integer rating, String comment) {
        var existingReview = getReviewByUserAndProduct(user, product);
        if (existingReview.isPresent()) {
            throw new IllegalArgumentException("Product already reviewed, edit the existing review instead");
        }
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        String sanitizedComment = HtmlPolicies.sanitizeNoHtml(comment);
        review.setComment(sanitizedComment);
        return reviewsRepository.save(review);
    }

    public void deleteReviewByUser(User user, UUID reviewPublicId) {
        var review = getReviewByPublicId(reviewPublicId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new NotAuthorizedException("You are not authorized to delete this review");
        }
        reviewsRepository.delete(review);
    }

}
