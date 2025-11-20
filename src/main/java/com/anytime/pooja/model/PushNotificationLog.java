package com.anytime.pooja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "push_notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_ids", columnDefinition = "JSON")
    private String userIds; // JSON array of user IDs
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Column(columnDefinition = "JSON")
    private String data; // JSON payload
    
    @Column(name = "sent_by")
    private Long sentBy; // admin_id
    
    @Column(name = "total_sent", nullable = false)
    private Integer totalSent = 0;
    
    @Column(name = "success_count", nullable = false)
    private Integer successCount = 0;
    
    @Column(name = "failure_count", nullable = false)
    private Integer failureCount = 0;
    
    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;
}
