package com.anytime.pooja.repository;

import com.anytime.pooja.model.ChatConversation;
import com.anytime.pooja.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByConversation(ChatConversation conversation);
    
    List<ChatMessage> findByConversationId(Long conversationId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt ASC")
    List<ChatMessage> findByConversationIdOrderBySentAt(@Param("conversationId") Long conversationId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.id = :conversationId AND m.sentAt > :after ORDER BY m.sentAt ASC")
    List<ChatMessage> findNewMessages(@Param("conversationId") Long conversationId, @Param("after") LocalDateTime after);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversation.id = :conversationId AND m.sender.id != :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}

