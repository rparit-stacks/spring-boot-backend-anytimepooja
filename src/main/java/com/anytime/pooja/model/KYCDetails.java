package com.anytime.pooja.model;

import com.anytime.pooja.model.enums.DocumentType;
import com.anytime.pooja.model.enums.KYCStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KYCDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pandit_id", nullable = false)
    private PanditProfile pandit;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;
    
    @Column(name = "document_number", nullable = false, length = 50)
    private String documentNumber;
    
    @Column(name = "front_image_url", nullable = false, length = 500)
    private String frontImageUrl;
    
    @Column(name = "back_image_url", length = 500)
    private String backImageUrl;
    
    @Column(name = "selfie_image_url", length = 500)
    private String selfieImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KYCStatus status = KYCStatus.PENDING;
    
    @Column(name = "verified_by")
    private Long verifiedBy;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    
    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

