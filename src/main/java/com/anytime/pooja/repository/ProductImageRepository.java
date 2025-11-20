package com.anytime.pooja.repository;

import com.anytime.pooja.model.Product;
import com.anytime.pooja.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProduct(Product product);
    
    List<ProductImage> findByProductId(Long productId);
    
    @Query("SELECT i FROM ProductImage i WHERE i.product.id = :productId ORDER BY i.isPrimary DESC, i.displayOrder ASC")
    List<ProductImage> findByProductIdOrdered(@Param("productId") Long productId);
    
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
    
    @Query("SELECT i FROM ProductImage i WHERE i.product.id = :productId AND i.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);
}

