package com.anytime.pooja.repository;

import com.anytime.pooja.model.Booking;
import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.Review;
import com.anytime.pooja.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Optional<Review> findByBooking(Booking booking);
    
    Optional<Review> findByBookingId(Long bookingId);
    
    List<Review> findByPandit(PanditProfile pandit);
    
    List<Review> findByPanditId(Long panditId);
    
    List<Review> findByUser(User user);
    
    List<Review> findByUserId(Long userId);
    
    @Query("SELECT r FROM Review r WHERE r.pandit.id = :panditId ORDER BY r.createdAt DESC")
    List<Review> findByPanditIdOrderByCreatedAtDesc(@Param("panditId") Long panditId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.pandit.id = :panditId")
    Double getAverageRatingByPanditId(@Param("panditId") Long panditId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.pandit.id = :panditId AND r.rating = :rating")
    Long countByPanditIdAndRating(@Param("panditId") Long panditId, @Param("rating") Integer rating);
    
    @Query("SELECT r FROM Review r WHERE r.pandit.id = :panditId AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<Review> findByPanditIdAndMinRating(@Param("panditId") Long panditId, @Param("minRating") Integer minRating);
}

