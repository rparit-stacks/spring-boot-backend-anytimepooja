package com.anytime.pooja.model;

import com.anytime.pooja.model.enums.JobType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedJob {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 20)
    private JobType jobType;
    
    @Column(nullable = false, columnDefinition = "JSON")
    private String payload; // JSON
    
    @Column(columnDefinition = "TEXT")
    private String exception;
    
    @CreationTimestamp
    @Column(name = "failed_at", nullable = false, updatable = false)
    private LocalDateTime failedAt;
    
    @Column(name = "retried_at")
    private LocalDateTime retriedAt;
}
