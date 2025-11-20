package com.anytime.pooja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pandit_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanditProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "experience_years")
    private Integer experienceYears;
    
    @ElementCollection
    @CollectionTable(name = "pandit_languages", joinColumns = @JoinColumn(name = "pandit_id"))
    @Column(name = "language")
    private Set<String> languages;
    
    @ElementCollection
    @CollectionTable(name = "pandit_service_areas", joinColumns = @JoinColumn(name = "pandit_id"))
    @Column(name = "city")
    private Set<String> serviceAreas;
    
    @Column(columnDefinition = "DECIMAL(3,2)")
    private Double rating = 0.0;
    
    @Column(name = "total_bookings", nullable = false)
    private Integer totalBookings = 0;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "verification_date")
    private LocalDateTime verificationDate;
    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

