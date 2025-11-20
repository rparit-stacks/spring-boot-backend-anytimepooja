package com.anytime.pooja.websocket;

import com.anytime.pooja.dto.ChatDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * WebSocket Controller for Real-time Chat
 * Handles WebSocket messages using STOMP protocol
 */
@Controller
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send message via WebSocket
     * Client sends to: /app/chat.send
     * Server sends to: /user/{userId}/queue/messages
     */
    @MessageMapping("/chat.send")
    @SendToUser("/queue/messages")
    public ChatDTO.MessageResponse sendMessage(@Payload ChatWebSocketMessage message, Principal principal) {
        try {
            // Get current user ID from principal
            Long userId = getCurrentUserId(principal);
            
            // Create SendMessageRequest
            ChatDTO.SendMessageRequest request = new ChatDTO.SendMessageRequest();
            request.setConversationId(message.getConversationId());
            request.setMessage(message.getMessage());
            request.setMessageType(message.getMessageType());
            request.setAttachmentUrl(message.getAttachmentUrl());
            
            // Send message via service
            ChatDTO.MessageResponse response = chatService.sendMessage(userId, request);
            
            // Send to recipient (other user in conversation)
            sendToRecipient(response, message.getConversationId(), userId);
            
            return response;
        } catch (Exception e) {
            // Handle error
            return null;
        }
    }

    /**
     * Mark messages as read via WebSocket
     */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ChatReadMessage message, Principal principal) {
        try {
            Long userId = getCurrentUserId(principal);
            chatService.markAsRead(userId, message.getConversationId());
            
            // Notify other user that messages were read
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + message.getConversationId() + "/read",
                new ReadReceipt(userId, message.getConversationId())
            );
        } catch (Exception e) {
            // Handle error
        }
    }

    /**
     * Typing indicator
     */
    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatTypingMessage message, Principal principal) {
        try {
            Long userId = getCurrentUserId(principal);
            
            // Send typing indicator to other user
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + message.getConversationId() + "/typing",
                new TypingIndicator(userId, message.getConversationId(), message.isTyping())
            );
        } catch (Exception e) {
            // Handle error
        }
    }

    /**
     * Send message to recipient (other user in conversation)
     */
    private void sendToRecipient(ChatDTO.MessageResponse message, Long conversationId, Long senderId) {
        // Get recipient ID from conversation
        // This should be fetched from ChatService or Conversation entity
        // For now, sending to conversation topic
        messagingTemplate.convertAndSend(
            "/topic/conversation/" + conversationId + "/messages",
            message
        );
    }

    /**
     * Get current user ID from AuthService
     */
    private Long getCurrentUserId(Principal principal) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return userId;
    }

    // Inner classes for WebSocket messages
    public static class ChatWebSocketMessage {
        private Long conversationId;
        private String message;
        private com.anytime.pooja.model.enums.MessageType messageType;
        private String attachmentUrl;

        // Getters and Setters
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public com.anytime.pooja.model.enums.MessageType getMessageType() { return messageType; }
        public void setMessageType(com.anytime.pooja.model.enums.MessageType messageType) { this.messageType = messageType; }
        public String getAttachmentUrl() { return attachmentUrl; }
        public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    }

    public static class ChatReadMessage {
        private Long conversationId;

        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    }

    public static class ChatTypingMessage {
        private Long conversationId;
        private boolean typing;

        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }

    public static class ReadReceipt {
        private Long userId;
        private Long conversationId;

        public ReadReceipt(Long userId, Long conversationId) {
            this.userId = userId;
            this.conversationId = conversationId;
        }

        public Long getUserId() { return userId; }
        public Long getConversationId() { return conversationId; }
    }

    public static class TypingIndicator {
        private Long userId;
        private Long conversationId;
        private boolean typing;

        public TypingIndicator(Long userId, Long conversationId, boolean typing) {
            this.userId = userId;
            this.conversationId = conversationId;
            this.typing = typing;
        }

        public Long getUserId() { return userId; }
        public Long getConversationId() { return conversationId; }
        public boolean isTyping() { return typing; }
    }
}

