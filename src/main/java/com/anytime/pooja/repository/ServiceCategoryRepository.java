package com.anytime.pooja.repository;

import com.anytime.pooja.model.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    
    List<ServiceCategory> findByIsActiveTrue();
    
    List<ServiceCategory> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    @Query("SELECT s FROM ServiceCategory s WHERE s.isActive = true ORDER BY s.displayOrder ASC")
    List<ServiceCategory> findAllActiveOrdered();
}

