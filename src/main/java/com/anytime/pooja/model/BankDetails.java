package com.anytime.pooja.model;

import com.anytime.pooja.model.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pandit_id", nullable = false, unique = true)
    private PanditProfile pandit;
    
    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String accountHolderName;
    
    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;
    
    @Column(name = "ifsc_code", nullable = false, length = 15)
    private String ifscCode;
    
    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;
    
    @Column(name = "branch_name", length = 100)
    private String branchName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;
    
    @Column(name = "upi_id", length = 100)
    private String upiId;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;
}

