package com.anytime.pooja.service;

import com.anytime.pooja.dto.ChatDTO;

import java.util.List;

public interface ChatService {
    // User Operations
    ChatDTO.ConversationListResponse getUserConversations(Long userId);
    ChatDTO.ConversationResponse startConversation(Long userId, ChatDTO.StartConversationRequest request);
    List<ChatDTO.MessageResponse> getMessages(Long userId, Long conversationId, Integer page, Integer size);
    ChatDTO.MessageResponse sendMessage(Long userId, ChatDTO.SendMessageRequest request);
    void markAsRead(Long userId, Long conversationId);
    
    // Pandit Operations
    ChatDTO.ConversationListResponse getPanditConversations(Long panditId);
    List<ChatDTO.MessageResponse> getPanditMessages(Long panditId, Long conversationId, Integer page, Integer size);
    ChatDTO.MessageResponse sendPanditMessage(Long panditId, ChatDTO.SendMessageRequest request);
    void markAsReadByPandit(Long panditId, Long conversationId);
}

