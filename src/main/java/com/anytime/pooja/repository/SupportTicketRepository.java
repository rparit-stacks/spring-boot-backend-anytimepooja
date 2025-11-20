package com.anytime.pooja.repository;

import com.anytime.pooja.model.SupportTicket;
import com.anytime.pooja.model.User;
import com.anytime.pooja.model.enums.TicketCategory;
import com.anytime.pooja.model.enums.TicketPriority;
import com.anytime.pooja.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    
    List<SupportTicket> findByUser(User user);
    
    List<SupportTicket> findByUserId(Long userId);
    
    List<SupportTicket> findByStatus(TicketStatus status);
    
    List<SupportTicket> findByCategory(TicketCategory category);
    
    List<SupportTicket> findByPriority(TicketPriority priority);
    
    List<SupportTicket> findByAssignedTo(Long assignedTo);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.status = :status ORDER BY t.priority DESC, t.createdAt ASC")
    List<SupportTicket> findByStatusOrderByPriority(@Param("status") TicketStatus status);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.status = :status AND t.priority = :priority")
    List<SupportTicket> findByStatusAndPriority(@Param("status") TicketStatus status, @Param("priority") TicketPriority priority);
    
    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.status = :status")
    Long countByStatus(@Param("status") TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.assignedTo = :adminId AND t.status != 'CLOSED'")
    Long countOpenTicketsByAdmin(@Param("adminId") Long adminId);
}

