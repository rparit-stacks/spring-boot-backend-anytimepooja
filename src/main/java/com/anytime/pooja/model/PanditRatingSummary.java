package com.anytime.pooja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pandit_ratings_summary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanditRatingSummary {
    
    @Id
    @Column(name = "pandit_id")
    private Long panditId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pandit_id", nullable = false)
    private PanditProfile pandit;
    
    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2)")
    private Double averageRating = 0.0;
    
    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews = 0;
    
    @Column(name = "five_star_count", nullable = false)
    private Integer fiveStarCount = 0;
    
    @Column(name = "four_star_count", nullable = false)
    private Integer fourStarCount = 0;
    
    @Column(name = "three_star_count", nullable = false)
    private Integer threeStarCount = 0;
    
    @Column(name = "two_star_count", nullable = false)
    private Integer twoStarCount = 0;
    
    @Column(name = "one_star_count", nullable = false)
    private Integer oneStarCount = 0;
    
    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
