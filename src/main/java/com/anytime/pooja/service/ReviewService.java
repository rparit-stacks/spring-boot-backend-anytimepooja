package com.anytime.pooja.service;

import com.anytime.pooja.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    // Booking Reviews (Pandit Reviews)
    ReviewDTO.ReviewResponse createReview(Long userId, ReviewDTO.CreateReviewRequest request);
    ReviewDTO.ReviewResponse updateReview(Long userId, Long reviewId, ReviewDTO.UpdateReviewRequest request);
    void deleteReview(Long userId, Long reviewId);
    List<ReviewDTO.ReviewResponse> getPanditReviews(Long panditId, Integer page, Integer size);
    ReviewDTO.ReviewSummaryResponse getPanditReviewSummary(Long panditId);
    List<ReviewDTO.ReviewResponse> getUserReviews(Long userId);
    
    // Product Reviews
    ReviewDTO.ProductReviewResponse createProductReview(Long userId, ReviewDTO.ProductReviewRequest request);
    ReviewDTO.ProductReviewResponse updateProductReview(Long userId, Long reviewId, ReviewDTO.UpdateReviewRequest request);
    void deleteProductReview(Long userId, Long reviewId);
    List<ReviewDTO.ProductReviewResponse> getProductReviews(Long productId, Integer page, Integer size);
    ReviewDTO.ReviewSummaryResponse getProductReviewSummary(Long productId);
}

