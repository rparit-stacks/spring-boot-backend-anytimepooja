package com.anytime.pooja.repository;

import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.PanditService;
import com.anytime.pooja.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanditServiceRepository extends JpaRepository<PanditService, Long> {
    
    List<PanditService> findByPandit(PanditProfile pandit);
    
    List<PanditService> findByPanditId(Long panditId);
    
    List<PanditService> findByPanditAndIsActiveTrue(PanditProfile pandit);
    
    List<PanditService> findByPanditIdAndIsActiveTrue(Long panditId);
    
    List<PanditService> findByCategory(ServiceCategory category);
    
    List<PanditService> findByCategoryId(Long categoryId);
    
    @Query("SELECT s FROM PanditService s WHERE s.pandit.id = :panditId AND s.isActive = true ORDER BY s.price ASC")
    List<PanditService> findActiveServicesByPanditOrderByPrice(@Param("panditId") Long panditId);
    
    @Query("SELECT s FROM PanditService s WHERE s.category.id = :categoryId AND s.isActive = true")
    List<PanditService> findActiveServicesByCategory(@Param("categoryId") Long categoryId);

    com.anytime.pooja.service.PanditService findPanditServiceById(Long id);
}

