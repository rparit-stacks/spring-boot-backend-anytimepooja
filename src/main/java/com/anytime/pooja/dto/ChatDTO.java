package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest {
        @NotNull(message = "Conversation ID is required")
        private Long conversationId;

        @NotBlank(message = "Message is required")
        @Size(max = 2000, message = "Message must not exceed 2000 characters")
        private String message;

        private MessageType messageType = MessageType.TEXT;
        private String attachmentUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageResponse {
        private Long id;
        private Long conversationId;
        private Long senderId;
        private String senderName;
        private String senderImageUrl;
        private String message;
        private MessageType messageType;
        private String attachmentUrl;
        private Boolean isRead;
        private LocalDateTime readAt;
        private LocalDateTime sentAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationResponse {
        private Long id;
        private Long userId;
        private String userName;
        private String userImageUrl;
        private Long panditId;
        private String panditName;
        private String panditImageUrl;
        private Long bookingId;
        private String bookingNumber;
        private String lastMessage;
        private LocalDateTime lastMessageAt;
        private Integer unreadCount;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StartConversationRequest {
        @NotNull(message = "Pandit ID is required")
        private Long panditId;

        private Long bookingId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationListResponse {
        private List<ConversationResponse> conversations;
        private Integer totalUnreadCount;
    }
}

