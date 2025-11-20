package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.ChatDTO;
import com.anytime.pooja.model.ChatConversation;
import com.anytime.pooja.repository.ChatConversationRepository;
import com.anytime.pooja.service.ChatWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket Service Implementation for Real-time Chat
 */
@Service
public class ChatWebSocketServiceImpl implements ChatWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatConversationRepository chatConversationRepository;

    @Override
    public void sendMessageToUser(Long userId, ChatDTO.MessageResponse message) {
        // Send to user-specific queue
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/messages",
            message
        );
    }

    @Override
    public void sendMessageToConversation(Long conversationId, ChatDTO.MessageResponse message) {
        // Send to conversation topic
        messagingTemplate.convertAndSend(
            "/topic/conversation/" + conversationId + "/messages",
            message
        );
    }

    @Override
    public void sendTypingIndicator(Long conversationId, Long userId, boolean isTyping) {
        TypingIndicator indicator = new TypingIndicator(userId, conversationId, isTyping);
        messagingTemplate.convertAndSend(
            "/topic/conversation/" + conversationId + "/typing",
            indicator
        );
    }

    @Override
    public void sendReadReceipt(Long conversationId, Long userId) {
        ReadReceipt receipt = new ReadReceipt(userId, conversationId);
        messagingTemplate.convertAndSend(
            "/topic/conversation/" + conversationId + "/read",
            receipt
        );
    }

    /**
     * Get recipient user ID from conversation
     */
    private Long getRecipientId(Long conversationId, Long senderId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (conversation.getUser().getId().equals(senderId)) {
            return conversation.getPandit().getUser().getId();
        } else {
            return conversation.getUser().getId();
        }
    }

    // Inner classes
    private static class TypingIndicator {
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

    private static class ReadReceipt {
        private Long userId;
        private Long conversationId;

        public ReadReceipt(Long userId, Long conversationId) {
            this.userId = userId;
            this.conversationId = conversationId;
        }

        public Long getUserId() { return userId; }
        public Long getConversationId() { return conversationId; }
    }
}

