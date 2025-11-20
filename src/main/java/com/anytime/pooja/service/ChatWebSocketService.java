package com.anytime.pooja.service;

import com.anytime.pooja.dto.ChatDTO;

/**
 * WebSocket Service for Real-time Chat
 * Handles WebSocket-specific operations
 */
public interface ChatWebSocketService {
    
    /**
     * Send message to specific user via WebSocket
     */
    void sendMessageToUser(Long userId, ChatDTO.MessageResponse message);
    
    /**
     * Send message to conversation topic
     */
    void sendMessageToConversation(Long conversationId, ChatDTO.MessageResponse message);
    
    /**
     * Send typing indicator
     */
    void sendTypingIndicator(Long conversationId, Long userId, boolean isTyping);
    
    /**
     * Send read receipt
     */
    void sendReadReceipt(Long conversationId, Long userId);
}

