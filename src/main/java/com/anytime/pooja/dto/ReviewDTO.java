package com.anytime.pooja.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReviewRequest {
        @NotNull(message = "Booking ID is required")
        private Long bookingId;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;

        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        private String comment;

        private Boolean isAnonymous = false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponse {
        private Long id;
        private Long bookingId;
        private Long userId;
        private String userName;
        private String userImageUrl;
        private Long panditId;
        private String panditName;
        private Integer rating;
        private String comment;
        private Boolean isAnonymous;
        private String adminResponse;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReviewRequest {
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;

        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        private String comment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductReviewRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;

        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        private String comment;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductReviewResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Long userId;
        private String userName;
        private String userImageUrl;
        private Integer rating;
        private String comment;
        private Boolean isVerifiedPurchase;
        private Integer isHelpfulCount;
        private String adminResponse;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummaryResponse {
        private Double averageRating;
        private Integer totalReviews;
        private Integer fiveStarCount;
        private Integer fourStarCount;
        private Integer threeStarCount;
        private Integer twoStarCount;
        private Integer oneStarCount;
    }
}

