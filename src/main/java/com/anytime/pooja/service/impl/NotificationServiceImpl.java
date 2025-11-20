package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.NotificationDTO;
import com.anytime.pooja.model.Notification;
import com.anytime.pooja.repository.NotificationRepository;
import com.anytime.pooja.service.NotificationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationServiceInterface {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public NotificationDTO.NotificationListResponse getUserNotifications(Long userId, Integer page, Integer size) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        NotificationDTO.NotificationListResponse response = new NotificationDTO.NotificationListResponse();
        response.setNotifications(notifications.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToNotificationResponse)
            .collect(Collectors.toList()));
        response.setUnreadCount((int) notifications.stream()
            .filter(n -> !n.getIsRead())
            .count());
        response.setTotalCount(notifications.size());
        return response;
    }

    @Override
    public NotificationDTO.NotificationResponse getNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToNotificationResponse(notification);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        notification.setIsRead(true);
        notification.setReadAt(java.time.LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        notifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(java.time.LocalDateTime.now());
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void updateFCMToken(Long userId, NotificationDTO.UpdateFCMTokenRequest request) {
        // Store FCM token - implementation depends on User entity structure
        // Will be implemented when User entity has FCM token field
    }

    @Override
    public NotificationDTO.NotificationCountResponse getNotificationCount(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        NotificationDTO.NotificationCountResponse response = new NotificationDTO.NotificationCountResponse();
        response.setTotalUnread((int) notifications.stream()
            .filter(n -> !n.getIsRead())
            .count());
        
        // Count by type
        java.util.Map<String, Integer> unreadByType = notifications.stream()
            .filter(n -> !n.getIsRead())
            .collect(Collectors.groupingBy(
                n -> n.getType() != null ? n.getType().name() : "OTHER",
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
        response.setUnreadByType(unreadByType);
        response.setLastChecked(java.time.LocalDateTime.now());
        return response;
    }

    // Helper methods
    private NotificationDTO.NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationDTO.NotificationResponse response = new NotificationDTO.NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setReferenceId(notification.getReferenceId());
        response.setReferenceType(notification.getReferenceType());
        response.setIsRead(notification.getIsRead());
        response.setReadAt(notification.getReadAt());
        response.setData(notification.getData());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}

