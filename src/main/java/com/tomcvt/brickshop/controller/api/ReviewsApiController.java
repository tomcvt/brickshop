package com.tomcvt.brickshop.controller.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tomcvt.brickshop.dto.ReviewDto;
import com.tomcvt.brickshop.dto.ReviewRequest;
import com.tomcvt.brickshop.exception.NotLoggedInException;
import com.tomcvt.brickshop.mappers.ReviewMapper;
import com.tomcvt.brickshop.model.Review;
import com.tomcvt.brickshop.model.SecureUserDetails;
import com.tomcvt.brickshop.service.ReviewService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


//@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/reviews")
public class ReviewsApiController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper = ReviewMapper.INSTANCE;

    public ReviewsApiController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@AuthenticationPrincipal SecureUserDetails userDetails,
        @RequestBody ReviewRequest reviewRequest) {
        if (userDetails == null) {
            throw new NotLoggedInException("Log in to add reviews.");
        }
        Review createdReview = reviewService.addReview(
                userDetails.getUser(),
                reviewRequest.productPublicId(),
                reviewRequest.rating(),
                reviewRequest.comment()
        );
        return ResponseEntity.ok(createdReview);
    }

    @PostMapping("/edit/{reviewPublicId}")
    public ResponseEntity<Review> editReview(@AuthenticationPrincipal SecureUserDetails userDetails,
        @RequestBody ReviewRequest reviewRequest,
        @PathVariable("reviewPublicId") UUID reviewPublicId) {
        if (userDetails == null) {
            throw new NotLoggedInException("Log in to edit reviews.");
        }
        Review updatedReview = reviewService.editReviewByUser(
                userDetails.getUser(),
                reviewPublicId,
                reviewRequest.rating(),
                reviewRequest.comment()
        );
        return ResponseEntity.ok(updatedReview);
    }
    @DeleteMapping("/delete/{reviewPublicId}")
    public ResponseEntity<Void> deleteReview(@AuthenticationPrincipal SecureUserDetails userDetails,
        @PathVariable("reviewPublicId") UUID reviewPublicId) {
        if (userDetails == null) {
            throw new NotLoggedInException("Log in to delete reviews.");
        }
        reviewService.deleteReviewByUser(
                userDetails.getUser(),
                reviewPublicId
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/personal/{productPublicId}")
    public ResponseEntity<ReviewDto> getPersonalReviewForProduct(@AuthenticationPrincipal SecureUserDetails userDetails,
        @PathVariable("productPublicId") UUID productPublicId) {
        if (userDetails == null) {
            throw new NotLoggedInException("Log in to view your review.");
        }
        var reviewOpt = reviewService.getReviewByUserAndProductPublicId(
                userDetails.getUser(),
                productPublicId
        );
        return reviewOpt
                .map(review -> ResponseEntity.ok(reviewMapper.toReviewDtoWithUser(review, userDetails.getUser())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
