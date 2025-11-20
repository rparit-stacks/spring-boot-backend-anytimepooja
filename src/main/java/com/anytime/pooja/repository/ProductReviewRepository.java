package com.anytime.pooja.repository;

import com.anytime.pooja.model.Product;
import com.anytime.pooja.model.ProductReview;
import com.anytime.pooja.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    
    List<ProductReview> findByProduct(Product product);
    
    List<ProductReview> findByProductId(Long productId);
    
    List<ProductReview> findByUser(User user);
    
    List<ProductReview> findByUserId(Long userId);
    
    Optional<ProductReview> findByProductIdAndUserId(Long productId, Long userId);
    
    @Query("SELECT r FROM ProductReview r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);
    
    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM ProductReview r WHERE r.product.id = :productId AND r.rating = :rating")
    Long countByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    @Query("SELECT r FROM ProductReview r WHERE r.product.id = :productId AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<ProductReview> findByProductIdAndMinRating(@Param("productId") Long productId, @Param("minRating") Integer minRating);
}

