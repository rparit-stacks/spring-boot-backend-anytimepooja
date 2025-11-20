package com.anytime.pooja.repository;

import com.anytime.pooja.model.PanditEarning;
import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.enums.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PanditEarningRepository extends JpaRepository<PanditEarning, Long> {
    
    List<PanditEarning> findByPandit(PanditProfile pandit);
    
    List<PanditEarning> findByPanditId(Long panditId);
    
    List<PanditEarning> findByPanditIdAndPayoutStatus(Long panditId, PayoutStatus payoutStatus);
    
    @Query("SELECT e FROM PanditEarning e WHERE e.pandit.id = :panditId AND e.earnedAt >= :fromDate AND e.earnedAt <= :toDate")
    List<PanditEarning> findByPanditIdAndDateRange(@Param("panditId") Long panditId,
                                                    @Param("fromDate") LocalDateTime fromDate,
                                                    @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT SUM(e.netAmount) FROM PanditEarning e WHERE e.pandit.id = :panditId")
    Double getTotalEarningsByPanditId(@Param("panditId") Long panditId);
    
    @Query("SELECT SUM(e.netAmount) FROM PanditEarning e WHERE e.pandit.id = :panditId AND e.payoutStatus = :status")
    Double getTotalEarningsByPanditIdAndStatus(@Param("panditId") Long panditId, @Param("status") PayoutStatus status);
    
    @Query("SELECT e FROM PanditEarning e WHERE e.payoutStatus = :status ORDER BY e.earnedAt ASC")
    List<PanditEarning> findPendingPayouts(@Param("status") PayoutStatus status);
}

