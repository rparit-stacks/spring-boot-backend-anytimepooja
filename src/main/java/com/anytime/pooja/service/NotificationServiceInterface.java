package com.anytime.pooja.service;

import com.anytime.pooja.dto.NotificationDTO;

public interface NotificationServiceInterface {
    NotificationDTO.NotificationListResponse getUserNotifications(Long userId, Integer page, Integer size);
    NotificationDTO.NotificationResponse getNotification(Long userId, Long notificationId);
    void markAsRead(Long userId, Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long userId, Long notificationId);
    void updateFCMToken(Long userId, NotificationDTO.UpdateFCMTokenRequest request);
    NotificationDTO.NotificationCountResponse getNotificationCount(Long userId);
}

