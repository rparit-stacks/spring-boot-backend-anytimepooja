package com.anytime.pooja.repository;

import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.PanditRatingSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PanditRatingSummaryRepository extends JpaRepository<PanditRatingSummary, Long> {
    
    Optional<PanditRatingSummary> findByPandit(PanditProfile pandit);
    
    Optional<PanditRatingSummary> findByPanditId(Long panditId);
}

