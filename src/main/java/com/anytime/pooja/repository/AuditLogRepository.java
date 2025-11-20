package com.anytime.pooja.repository;

import com.anytime.pooja.model.AuditLog;
import com.anytime.pooja.model.enums.AuditAction;
import com.anytime.pooja.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserId(Long userId);
    
    List<AuditLog> findByUserType(Role userType);
    
    List<AuditLog> findByAction(AuditAction action);
    
    List<AuditLog> findByEntityType(String entityType);
    
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.createdAt DESC")
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<AuditLog> findByEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt >= :fromDate AND a.createdAt <= :toDate ORDER BY a.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}

