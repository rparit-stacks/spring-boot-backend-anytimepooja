package com.anytime.pooja.repository;

import com.anytime.pooja.model.Product;
import com.anytime.pooja.model.User;
import com.anytime.pooja.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    List<Wishlist> findByUser(User user);
    
    List<Wishlist> findByUserId(Long userId);
    
    Optional<Wishlist> findByUserAndProduct(User user, Product product);
    
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    @Query("SELECT w FROM Wishlist w WHERE w.user.id = :userId ORDER BY w.addedAt DESC")
    List<Wishlist> findByUserIdOrderByAddedAtDesc(@Param("userId") Long userId);
}

