package com.anytime.pooja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pandit_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanditService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pandit_id", nullable = false)
    private PanditProfile pandit;
    
    @Column(name = "service_name", nullable = false, length = 200)
    private String serviceName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "materials_included", nullable = false)
    private Boolean materialsIncluded = false;
    
    @Column(name = "home_visit", nullable = false)
    private Boolean homeVisit = true;
    
    @Column(name = "max_distance_km")
    private Integer maxDistanceKm;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

