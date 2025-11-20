package com.anytime.pooja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "revenue_reports", indexes = {
    @Index(name = "idx_report_date", columnList = "report_date", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_date", nullable = false, unique = true)
    private LocalDate reportDate;
    
    @Column(name = "booking_revenue", nullable = false)
    private Double bookingRevenue = 0.0;
    
    @Column(name = "product_revenue", nullable = false)
    private Double productRevenue = 0.0;
    
    @Column(name = "total_revenue", nullable = false)
    private Double totalRevenue = 0.0;
    
    @Column(name = "commission_earned", nullable = false)
    private Double commissionEarned = 0.0;
    
    @Column(name = "payouts_processed", nullable = false)
    private Double payoutsProcessed = 0.0;
    
    @Column(name = "active_users", nullable = false)
    private Integer activeUsers = 0;
    
    @Column(name = "new_users", nullable = false)
    private Integer newUsers = 0;
    
    @Column(name = "total_bookings", nullable = false)
    private Integer totalBookings = 0;
    
    @Column(name = "total_orders", nullable = false)
    private Integer totalOrders = 0;
    
    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}
