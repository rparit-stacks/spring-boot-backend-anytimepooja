package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.ChatDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatConversationRepository chatConversationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PanditProfileRepository panditProfileRepository;

    @Override
    public ChatDTO.ConversationListResponse getUserConversations(Long userId) {
        List<ChatConversation> conversations = chatConversationRepository.findByUserId(userId);
        
        ChatDTO.ConversationListResponse response = new ChatDTO.ConversationListResponse();
        List<ChatDTO.ConversationResponse> conversationResponses = conversations.stream()
            .map(c -> mapToConversationResponseForUser(c))
            .collect(Collectors.toList());
        response.setConversations(conversationResponses);
        Integer totalUnread = chatConversationRepository.getTotalUnreadCountForUser(userId);
        response.setTotalUnreadCount(totalUnread != null ? totalUnread : 0);
        return response;
    }

    @Override
    @Transactional
    public ChatDTO.ConversationResponse startConversation(Long userId, ChatDTO.StartConversationRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        PanditProfile pandit = panditProfileRepository.findById(request.getPanditId())
            .orElseThrow(() -> new RuntimeException("Pandit not found"));
        
        // Check if conversation already exists
        ChatConversation existing = chatConversationRepository.findByUserIdAndPanditId(userId, request.getPanditId())
            .orElse(null);
        
        if (existing != null) {
            return mapToConversationResponseForUser(existing);
        }
        
        ChatConversation conversation = new ChatConversation();
        conversation.setUser(user);
        conversation.setPandit(pandit);
        conversation.setUnreadCountUser(0);
        conversation.setUnreadCountPandit(0);
        
        conversation = chatConversationRepository.save(conversation);
        return mapToConversationResponseForUser(conversation);
    }

    @Override
    public List<ChatDTO.MessageResponse> getMessages(Long userId, Long conversationId, Integer page, Integer size) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (!conversation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findByConversationIdOrderBySentAt(conversationId);
        return messages.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToMessageResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatDTO.MessageResponse sendMessage(Long userId, ChatDTO.SendMessageRequest request) {
        ChatConversation conversation = chatConversationRepository.findById(request.getConversationId())
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (!conversation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(userRepository.findById(userId).orElse(null));
        message.setMessageType(request.getMessageType() != null ? request.getMessageType() : com.anytime.pooja.model.enums.MessageType.TEXT);
        message.setMessage(request.getMessage());
        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setIsRead(false);
        message.setSentAt(java.time.LocalDateTime.now());
        
        message = chatMessageRepository.save(message);
        
        // Update unread count for pandit and last message
        conversation.setUnreadCountPandit(conversation.getUnreadCountPandit() != null ? conversation.getUnreadCountPandit() + 1 : 1);
        conversation.setLastMessage(request.getMessage());
        conversation.setLastMessageAt(java.time.LocalDateTime.now());
        chatConversationRepository.save(conversation);
        
        return mapToMessageResponse(message);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long conversationId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (!conversation.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Mark all messages as read
        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversationId);
        messages.forEach(msg -> {
            if (!msg.getSender().getId().equals(userId) && !msg.getIsRead()) {
                msg.setIsRead(true);
                msg.setReadAt(java.time.LocalDateTime.now());
                chatMessageRepository.save(msg);
            }
        });
        
        conversation.setUnreadCountUser(0);
        chatConversationRepository.save(conversation);
    }

    @Override
    public ChatDTO.ConversationListResponse getPanditConversations(Long panditId) {
        List<ChatConversation> conversations = chatConversationRepository.findByPanditId(panditId);
        
        ChatDTO.ConversationListResponse response = new ChatDTO.ConversationListResponse();
        List<ChatDTO.ConversationResponse> conversationResponses = conversations.stream()
            .map(c -> mapToConversationResponseForPandit(c))
            .collect(Collectors.toList());
        response.setConversations(conversationResponses);
        Integer totalUnread = chatConversationRepository.getTotalUnreadCountForPandit(panditId);
        response.setTotalUnreadCount(totalUnread != null ? totalUnread : 0);
        return response;
    }

    @Override
    public List<ChatDTO.MessageResponse> getPanditMessages(Long panditId, Long conversationId, Integer page, Integer size) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (!conversation.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findByConversationIdOrderBySentAt(conversationId);
        return messages.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToMessageResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatDTO.MessageResponse sendPanditMessage(Long panditId, ChatDTO.SendMessageRequest request) {
        ChatConversation conversation = chatConversationRepository.findById(request.getConversationId())
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (!conversation.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(conversation.getPandit().getUser());
        message.setMessageType(request.getMessageType() != null ? request.getMessageType() : com.anytime.pooja.model.enums.MessageType.TEXT);
        message.setMessage(request.getMessage());
        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setIsRead(false);
        message.setSentAt(java.time.LocalDateTime.now());
        
        message = chatMessageRepository.save(message);
        
        // Update unread count for user and last message
        conversation.setUnreadCountUser(conversation.getUnreadCountUser() != null ? conversation.getUnreadCountUser() + 1 : 1);
        conversation.setLastMessage(request.getMessage());
        conversation.setLastMessageAt(java.time.LocalDateTime.now());
        chatConversationRepository.save(conversation);
        
        return mapToMessageResponse(message);
    }

    @Override
    @Transactional
    public void markAsReadByPandit(Long panditId, Long conversationId) {
        ChatConversation conversation = chatConversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        if (!conversation.getPandit().getId().equals(panditId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        // Mark all messages as read
        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversationId);
        messages.forEach(msg -> {
            if (!msg.getSender().getId().equals(conversation.getPandit().getUser().getId()) && !msg.getIsRead()) {
                msg.setIsRead(true);
                msg.setReadAt(java.time.LocalDateTime.now());
                chatMessageRepository.save(msg);
            }
        });
        
        conversation.setUnreadCountPandit(0);
        chatConversationRepository.save(conversation);
    }

    // Helper methods
    private ChatDTO.ConversationResponse mapToConversationResponseForUser(ChatConversation conversation) {
        ChatDTO.ConversationResponse response = new ChatDTO.ConversationResponse();
        response.setId(conversation.getId());
        response.setUserId(conversation.getUser().getId());
        response.setUserName(conversation.getUser().getName());
        response.setUserImageUrl(conversation.getUser().getProfileImageUrl());
        response.setPanditId(conversation.getPandit().getId());
        response.setPanditName(conversation.getPandit().getUser().getName());
        response.setPanditImageUrl(conversation.getPandit().getUser().getProfileImageUrl());
        response.setBookingId(conversation.getBooking() != null ? conversation.getBooking().getId() : null);
        response.setBookingNumber(conversation.getBooking() != null ? conversation.getBooking().getBookingNumber() : null);
        response.setLastMessage(conversation.getLastMessage());
        response.setLastMessageAt(conversation.getLastMessageAt());
        response.setUnreadCount(conversation.getUnreadCountUser() != null ? conversation.getUnreadCountUser() : 0);
        response.setCreatedAt(conversation.getCreatedAt());
        return response;
    }

    private ChatDTO.ConversationResponse mapToConversationResponseForPandit(ChatConversation conversation) {
        ChatDTO.ConversationResponse response = new ChatDTO.ConversationResponse();
        response.setId(conversation.getId());
        response.setUserId(conversation.getUser().getId());
        response.setUserName(conversation.getUser().getName());
        response.setUserImageUrl(conversation.getUser().getProfileImageUrl());
        response.setPanditId(conversation.getPandit().getId());
        response.setPanditName(conversation.getPandit().getUser().getName());
        response.setPanditImageUrl(conversation.getPandit().getUser().getProfileImageUrl());
        response.setBookingId(conversation.getBooking() != null ? conversation.getBooking().getId() : null);
        response.setBookingNumber(conversation.getBooking() != null ? conversation.getBooking().getBookingNumber() : null);
        response.setLastMessage(conversation.getLastMessage());
        response.setLastMessageAt(conversation.getLastMessageAt());
        response.setUnreadCount(conversation.getUnreadCountPandit() != null ? conversation.getUnreadCountPandit() : 0);
        response.setCreatedAt(conversation.getCreatedAt());
        return response;
    }

    private ChatDTO.MessageResponse mapToMessageResponse(ChatMessage message) {
        ChatDTO.MessageResponse response = new ChatDTO.MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversation().getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getName());
        response.setSenderImageUrl(message.getSender().getProfileImageUrl());
        response.setMessageType(message.getMessageType());
        response.setMessage(message.getMessage());
        response.setAttachmentUrl(message.getAttachmentUrl());
        response.setIsRead(message.getIsRead());
        response.setReadAt(message.getReadAt());
        response.setSentAt(message.getSentAt());
        return response;
    }
}

