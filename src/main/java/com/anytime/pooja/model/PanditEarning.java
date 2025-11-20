package com.anytime.pooja.model;

import com.anytime.pooja.model.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pandit_earnings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanditEarning {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pandit_id", nullable = false)
    private PanditProfile pandit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(name = "commission_percentage", nullable = false)
    private Double commissionPercentage;
    
    @Column(name = "commission_amount", nullable = false)
    private Double commissionAmount;
    
    @Column(name = "net_amount", nullable = false)
    private Double netAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false, length = 20)
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;
    
    @Column(name = "payout_date")
    private LocalDateTime payoutDate;
    
    @Column(name = "payout_reference", length = 100)
    private String payoutReference;
    
    @CreationTimestamp
    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;
}
