package com.anytime.pooja.repository;

import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PanditProfileRepository extends JpaRepository<PanditProfile, Long> {
    
    Optional<PanditProfile> findByUser(User user);
    
    Optional<PanditProfile> findByUserId(Long userId);
    
    List<PanditProfile> findByIsVerified(Boolean isVerified);
    
    List<PanditProfile> findByIsAvailable(Boolean isAvailable);
    
    List<PanditProfile> findByIsVerifiedAndIsAvailable(Boolean isVerified, Boolean isAvailable);
    
    @Query("SELECT p FROM PanditProfile p WHERE p.rating >= :minRating ORDER BY p.rating DESC")
    List<PanditProfile> findByMinRating(@Param("minRating") Double minRating);
    
    @Query("SELECT p FROM PanditProfile p WHERE p.isVerified = true AND p.isAvailable = true ORDER BY p.rating DESC, p.totalBookings DESC")
    List<PanditProfile> findTopRatedPandits();
    
    @Query("SELECT p FROM PanditProfile p WHERE :city MEMBER OF p.serviceAreas")
    List<PanditProfile> findByServiceArea(@Param("city") String city);
    
    @Query("SELECT COUNT(p) FROM PanditProfile p WHERE p.isVerified = true")
    Long countVerifiedPandits();
    
    @Query("SELECT COUNT(p) FROM PanditProfile p WHERE p.isAvailable = true")
    Long countAvailablePandits();
}

