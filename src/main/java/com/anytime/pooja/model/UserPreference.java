package com.anytime.pooja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(length = 20)
    private String language = "english";
    
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;
    
    @Column(name = "email_notification", nullable = false)
    private Boolean emailNotification = true;
    
    @Column(name = "sms_notification", nullable = false)
    private Boolean smsNotification = true;
    
    @Column(name = "push_notification", nullable = false)
    private Boolean pushNotification = true;
    
    @Column(length = 20)
    private String theme = "light";
}
