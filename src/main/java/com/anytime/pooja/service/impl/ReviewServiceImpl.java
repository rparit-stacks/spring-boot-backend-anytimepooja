package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.ReviewDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanditProfileRepository panditProfileRepository;

    @Override
    @Transactional
    public ReviewDTO.ReviewResponse createReview(Long userId, ReviewDTO.CreateReviewRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (booking.getStatus() != com.anytime.pooja.model.enums.BookingStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed bookings");
        }
        
        // Check if review already exists
        if (reviewRepository.findByBookingId(request.getBookingId()).isPresent()) {
            throw new RuntimeException("Review already exists for this booking");
        }
        
        Review review = new Review();
        review.setUser(user);
        review.setBooking(booking);
        review.setPandit(booking.getPandit());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        review = reviewRepository.save(review);
        
        // Update pandit rating
        updatePanditRating(booking.getPandit().getId());
        
        return mapToReviewResponse(review);
    }

    @Override
    @Transactional
    public ReviewDTO.ReviewResponse updateReview(Long userId, Long reviewId, ReviewDTO.UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
        
        review = reviewRepository.save(review);
        
        // Update pandit rating
        updatePanditRating(review.getPandit().getId());
        
        return mapToReviewResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        Long panditId = review.getPandit().getId();
        reviewRepository.delete(review);
        
        // Update pandit rating
        updatePanditRating(panditId);
    }

    @Override
    public List<ReviewDTO.ReviewResponse> getPanditReviews(Long panditId, Integer page, Integer size) {
        List<Review> reviews = reviewRepository.findByPanditId(panditId);
        return reviews.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToReviewResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO.ReviewSummaryResponse getPanditReviewSummary(Long panditId) {
        List<Review> reviews = reviewRepository.findByPanditId(panditId);
        
        ReviewDTO.ReviewSummaryResponse response = new ReviewDTO.ReviewSummaryResponse();
        response.setTotalReviews(reviews.size());
        
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
            response.setAverageRating(avgRating);
            
            // Count by rating
            response.setFiveStarCount((int) reviews.stream().filter(r -> r.getRating() == 5).count());
            response.setFourStarCount((int) reviews.stream().filter(r -> r.getRating() == 4).count());
            response.setThreeStarCount((int) reviews.stream().filter(r -> r.getRating() == 3).count());
            response.setTwoStarCount((int) reviews.stream().filter(r -> r.getRating() == 2).count());
            response.setOneStarCount((int) reviews.stream().filter(r -> r.getRating() == 1).count());
        } else {
            response.setAverageRating(0.0);
        }
        
        return response;
    }

    @Override
    public List<ReviewDTO.ReviewResponse> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
            .map(this::mapToReviewResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO.ProductReviewResponse createProductReview(Long userId, ReviewDTO.ProductReviewRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if review already exists
        if (productReviewRepository.findByProductIdAndUserId(request.getProductId(), userId).isPresent()) {
            throw new RuntimeException("Review already exists for this product");
        }
        
        ProductReview review = new ProductReview();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        review = productReviewRepository.save(review);
        
        // Update product rating
        updateProductRating(request.getProductId());
        
        return mapToProductReviewResponse(review);
    }

    @Override
    @Transactional
    public ReviewDTO.ProductReviewResponse updateProductReview(Long userId, Long reviewId, ReviewDTO.UpdateReviewRequest request) {
        ProductReview review = productReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());
        
        review = productReviewRepository.save(review);
        
        // Update product rating
        updateProductRating(review.getProduct().getId());
        
        return mapToProductReviewResponse(review);
    }

    @Override
    @Transactional
    public void deleteProductReview(Long userId, Long reviewId) {
        ProductReview review = productReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        Long productId = review.getProduct().getId();
        productReviewRepository.delete(review);
        
        // Update product rating
        updateProductRating(productId);
    }

    @Override
    public List<ReviewDTO.ProductReviewResponse> getProductReviews(Long productId, Integer page, Integer size) {
        List<ProductReview> reviews = productReviewRepository.findByProductId(productId);
        return reviews.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToProductReviewResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO.ReviewSummaryResponse getProductReviewSummary(Long productId) {
        List<ProductReview> reviews = productReviewRepository.findByProductId(productId);
        
        ReviewDTO.ReviewSummaryResponse response = new ReviewDTO.ReviewSummaryResponse();
        response.setTotalReviews(reviews.size());
        
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
            response.setAverageRating(avgRating);
            
            // Count by rating
            response.setFiveStarCount((int) reviews.stream().filter(r -> r.getRating() == 5).count());
            response.setFourStarCount((int) reviews.stream().filter(r -> r.getRating() == 4).count());
            response.setThreeStarCount((int) reviews.stream().filter(r -> r.getRating() == 3).count());
            response.setTwoStarCount((int) reviews.stream().filter(r -> r.getRating() == 2).count());
            response.setOneStarCount((int) reviews.stream().filter(r -> r.getRating() == 1).count());
        } else {
            response.setAverageRating(0.0);
        }
        
        return response;
    }

    // Helper methods
    private void updatePanditRating(Long panditId) {
        List<Review> reviews = reviewRepository.findByPanditId(panditId);
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
            
            PanditProfile pandit = panditProfileRepository.findById(panditId)
                .orElse(null);
            if (pandit != null) {
                pandit.setRating(avgRating);
                panditProfileRepository.save(pandit);
            }
        }
    }

    private void updateProductRating(Long productId) {
        List<ProductReview> reviews = productReviewRepository.findByProductId(productId);
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
            
            Product product = productRepository.findById(productId)
                .orElse(null);
            if (product != null) {
                product.setRating(avgRating);
                productRepository.save(product);
            }
        }
    }

    private ReviewDTO.ReviewResponse mapToReviewResponse(Review review) {
        ReviewDTO.ReviewResponse response = new ReviewDTO.ReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUserName(review.getUser().getName());
        response.setUserImageUrl(review.getUser().getProfileImageUrl());
        response.setPanditId(review.getPandit().getId());
        response.setPanditName(review.getPandit().getUser().getName());
        response.setBookingId(review.getBooking().getId());
        // Booking number not in DTO
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private ReviewDTO.ProductReviewResponse mapToProductReviewResponse(ProductReview review) {
        ReviewDTO.ProductReviewResponse response = new ReviewDTO.ProductReviewResponse();
        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setUserName(review.getUser().getName());
        response.setUserImageUrl(review.getUser().getProfileImageUrl());
        response.setProductId(review.getProduct().getId());
        response.setProductName(review.getProduct().getName());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}

