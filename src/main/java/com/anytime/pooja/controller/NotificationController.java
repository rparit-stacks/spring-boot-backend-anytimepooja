package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.NotificationDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.NotificationServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationServiceInterface notificationService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<NotificationDTO.NotificationListResponse>> getNotifications(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = authService.getCurrentUserId();
        NotificationDTO.NotificationListResponse response = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(buildSuccessResponse(response, "Notifications retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<NotificationDTO.NotificationResponse>> getNotification(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        NotificationDTO.NotificationResponse response = notificationService.getNotification(userId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Notification retrieved successfully"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<CommonDTO.SuccessResponse> markAsRead(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        notificationService.markAsRead(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Notification marked as read successfully"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<CommonDTO.SuccessResponse> markAllAsRead() {
        Long userId = authService.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(buildSuccessResponse("All notifications marked as read successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteNotification(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        notificationService.deleteNotification(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Notification deleted successfully"));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<CommonDTO.SuccessResponse> updateFCMToken(
            @Valid @RequestBody NotificationDTO.UpdateFCMTokenRequest request) {
        Long userId = authService.getCurrentUserId();
        notificationService.updateFCMToken(userId, request);
        return ResponseEntity.ok(buildSuccessResponse("FCM token updated successfully"));
    }

    @GetMapping("/count")
    public ResponseEntity<CommonDTO.ApiResponse<NotificationDTO.NotificationCountResponse>> getNotificationCount() {
        Long userId = authService.getCurrentUserId();
        NotificationDTO.NotificationCountResponse response = notificationService.getNotificationCount(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Notification count retrieved successfully"));
    }

    // Helper methods
    private <T> CommonDTO.ApiResponse<T> buildSuccessResponse(T data, String message) {
        CommonDTO.ApiResponse<T> response = new CommonDTO.ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private CommonDTO.SuccessResponse buildSuccessResponse(String message) {
        CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}

