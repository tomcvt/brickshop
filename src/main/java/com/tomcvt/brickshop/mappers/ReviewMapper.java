package com.tomcvt.brickshop.mappers;

import com.tomcvt.brickshop.dto.ReviewDto;
import com.tomcvt.brickshop.model.Review;
import com.tomcvt.brickshop.model.User;

public class ReviewMapper {
    public static final ReviewMapper INSTANCE = new ReviewMapper();

    public ReviewMapper() {
    }
    
    /**
     * Converts a {@link Review} entity to a {@link ReviewDto} using a straightforward mapping approach.
     *
     * @param review the {@link Review} entity to convert
     * @return a {@link ReviewDto} containing the rating, comment, creation date,
     *         username of the reviewer, and the public ID from the given review
     * 
     * @implNote Naive implementation can trigger N+1 query issues when fetching associated user data.
     */
    public ReviewDto toReviewDtoNaive(Review review) {
        return new ReviewDto(
            review.getRating(),
            review.getComment(),
            review.getCreatedAt(),
            review.getUser().getUsername(),
            review.getPublicId()
        );
    }

    public ReviewDto toReviewDtoWithUser(Review review, User user) {
        return new ReviewDto(
            review.getRating(),
            review.getComment(),
            review.getCreatedAt(),
            user.getUsername(),
            review.getPublicId()
        );
    }

}
