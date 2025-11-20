package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.ReviewDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;

    // Booking Reviews (Pandit Reviews)
    @PostMapping("/booking")
    public ResponseEntity<CommonDTO.ApiResponse<ReviewDTO.ReviewResponse>> createReview(
            @Valid @RequestBody ReviewDTO.CreateReviewRequest request) {
        Long userId = authService.getCurrentUserId();
        ReviewDTO.ReviewResponse response = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Review created successfully"));
    }

    @PutMapping("/booking/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<ReviewDTO.ReviewResponse>> updateReview(
            @PathVariable Long id, @Valid @RequestBody ReviewDTO.UpdateReviewRequest request) {
        Long userId = authService.getCurrentUserId();
        ReviewDTO.ReviewResponse response = reviewService.updateReview(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Review updated successfully"));
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteReview(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        reviewService.deleteReview(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Review deleted successfully"));
    }

    @GetMapping("/pandit/{panditId}")
    public ResponseEntity<CommonDTO.PaginatedResponse<ReviewDTO.ReviewResponse>> getPanditReviews(
            @PathVariable Long panditId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ReviewDTO.ReviewResponse> reviews = reviewService.getPanditReviews(panditId, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(reviews, page, size));
    }

    @GetMapping("/pandit/{panditId}/summary")
    public ResponseEntity<CommonDTO.ApiResponse<ReviewDTO.ReviewSummaryResponse>> getPanditReviewSummary(@PathVariable Long panditId) {
        ReviewDTO.ReviewSummaryResponse response = reviewService.getPanditReviewSummary(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Review summary retrieved successfully"));
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<CommonDTO.ApiResponse<List<ReviewDTO.ReviewResponse>>> getUserReviews() {
        Long userId = authService.getCurrentUserId();
        List<ReviewDTO.ReviewResponse> response = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "User reviews retrieved successfully"));
    }

    // Product Reviews
    @PostMapping("/product")
    public ResponseEntity<CommonDTO.ApiResponse<ReviewDTO.ProductReviewResponse>> createProductReview(
            @Valid @RequestBody ReviewDTO.ProductReviewRequest request) {
        Long userId = authService.getCurrentUserId();
        ReviewDTO.ProductReviewResponse response = reviewService.createProductReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Product review created successfully"));
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<ReviewDTO.ProductReviewResponse>> updateProductReview(
            @PathVariable Long id, @Valid @RequestBody ReviewDTO.UpdateReviewRequest request) {
        Long userId = authService.getCurrentUserId();
        ReviewDTO.ProductReviewResponse response = reviewService.updateProductReview(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Product review updated successfully"));
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteProductReview(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        reviewService.deleteProductReview(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Product review deleted successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<CommonDTO.PaginatedResponse<ReviewDTO.ProductReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<ReviewDTO.ProductReviewResponse> reviews = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(reviews, page, size));
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<CommonDTO.ApiResponse<ReviewDTO.ReviewSummaryResponse>> getProductReviewSummary(@PathVariable Long productId) {
        ReviewDTO.ReviewSummaryResponse response = reviewService.getProductReviewSummary(productId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Product review summary retrieved successfully"));
    }

    // Helper methods
    private <T> CommonDTO.ApiResponse<T> buildSuccessResponse(T data, String message) {
        CommonDTO.ApiResponse<T> response = new CommonDTO.ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private CommonDTO.SuccessResponse buildSuccessResponse(String message) {
        CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private <T> CommonDTO.PaginatedResponse<T> buildPaginatedResponse(List<T> content, Integer page, Integer size) {
        CommonDTO.PaginatedResponse<T> response = new CommonDTO.PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((long) content.size());
        response.setTotalPages((int) Math.ceil((double) content.size() / size));
        response.setHasNext(page < response.getTotalPages() - 1);
        response.setHasPrevious(page > 0);
        response.setIsFirst(page == 0);
        response.setIsLast(page >= response.getTotalPages() - 1);
        return response;
    }
}

