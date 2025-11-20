package com.anytime.pooja.repository;

import com.anytime.pooja.model.ChatConversation;
import com.anytime.pooja.model.PanditProfile;
import com.anytime.pooja.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    Optional<ChatConversation> findByUserAndPandit(User user, PanditProfile pandit);
    
    Optional<ChatConversation> findByUserIdAndPanditId(Long userId, Long panditId);
    
    List<ChatConversation> findByUser(User user);
    
    List<ChatConversation> findByUserId(Long userId);
    
    List<ChatConversation> findByPandit(PanditProfile pandit);
    
    List<ChatConversation> findByPanditId(Long panditId);
    
    @Query("SELECT c FROM ChatConversation c WHERE c.user.id = :userId ORDER BY c.lastMessageAt DESC")
    List<ChatConversation> findByUserIdOrderByLastMessage(@Param("userId") Long userId);
    
    @Query("SELECT c FROM ChatConversation c WHERE c.pandit.id = :panditId ORDER BY c.lastMessageAt DESC")
    List<ChatConversation> findByPanditIdOrderByLastMessage(@Param("panditId") Long panditId);
    
    @Query("SELECT SUM(c.unreadCountUser) FROM ChatConversation c WHERE c.user.id = :userId")
    Integer getTotalUnreadCountForUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.unreadCountPandit) FROM ChatConversation c WHERE c.pandit.id = :panditId")
    Integer getTotalUnreadCountForPandit(@Param("panditId") Long panditId);
}

