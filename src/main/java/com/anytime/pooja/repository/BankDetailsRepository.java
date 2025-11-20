package com.anytime.pooja.repository;

import com.anytime.pooja.model.BankDetails;
import com.anytime.pooja.model.PanditProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    
    Optional<BankDetails> findByPandit(PanditProfile pandit);
    
    Optional<BankDetails> findByPanditId(Long panditId);
    
    boolean existsByPanditId(Long panditId);
}

