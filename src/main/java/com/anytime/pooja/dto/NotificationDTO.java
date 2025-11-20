package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NotificationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationResponse {
        private Long id;
        private String title;
        private String message;
        private NotificationType type;
        private Long referenceId;
        private String referenceType;
        private Boolean isRead;
        private LocalDateTime readAt;
        private String data; // JSON
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationListResponse {
        private java.util.List<NotificationResponse> notifications;
        private Integer unreadCount;
        private Integer totalCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateFCMTokenRequest {
        private String fcmToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationCountResponse {
        private Integer totalUnread;
        private java.util.Map<String, Integer> unreadByType;
        private LocalDateTime lastChecked;
    }
}

