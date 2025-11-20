package com.anytime.pooja.repository;

import com.anytime.pooja.model.PushNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PushNotificationLogRepository extends JpaRepository<PushNotificationLog, Long> {
    
    @Query("SELECT p FROM PushNotificationLog p ORDER BY p.sentAt DESC")
    List<PushNotificationLog> findAllOrderBySentAtDesc();
    
    @Query("SELECT p FROM PushNotificationLog p WHERE p.sentAt >= :fromDate AND p.sentAt <= :toDate ORDER BY p.sentAt DESC")
    List<PushNotificationLog> findByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}

