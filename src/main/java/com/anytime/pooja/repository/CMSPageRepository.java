package com.anytime.pooja.repository;

import com.anytime.pooja.model.CMSPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CMSPageRepository extends JpaRepository<CMSPage, Long> {
    
    Optional<CMSPage> findBySlug(String slug);
    
    List<CMSPage> findByIsActiveTrue();
    
    @Query("SELECT c FROM CMSPage c WHERE c.slug = :slug AND c.isActive = true")
    Optional<CMSPage> findActiveBySlug(@Param("slug") String slug);
    
    boolean existsBySlug(String slug);
}

