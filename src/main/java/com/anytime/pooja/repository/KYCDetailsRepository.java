package com.anytime.pooja.repository;

import com.anytime.pooja.model.KYCDetails;
import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.enums.KYCStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KYCDetailsRepository extends JpaRepository<KYCDetails, Long> {
    
    Optional<KYCDetails> findByPandit(PanditProfile pandit);
    
    Optional<KYCDetails> findByPanditId(Long panditId);
    
    List<KYCDetails> findByStatus(KYCStatus status);
    
    @Query("SELECT k FROM KYCDetails k WHERE k.status = :status ORDER BY k.submittedAt ASC")
    List<KYCDetails> findPendingKYCs(@Param("status") KYCStatus status);
    
    @Query("SELECT COUNT(k) FROM KYCDetails k WHERE k.status = :status")
    Long countByStatus(@Param("status") KYCStatus status);
    
    @Query("SELECT k FROM KYCDetails k WHERE k.pandit.id = :panditId ORDER BY k.submittedAt DESC")
    List<KYCDetails> findByPanditIdOrderBySubmittedAt(@Param("panditId") Long panditId);
}

