package com.anytime.pooja.repository;

import com.anytime.pooja.model.SupportTicket;
import com.anytime.pooja.model.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    
    List<TicketMessage> findByTicket(SupportTicket ticket);
    
    List<TicketMessage> findByTicketId(Long ticketId);
    
    @Query("SELECT m FROM TicketMessage m WHERE m.ticket.id = :ticketId ORDER BY m.createdAt ASC")
    List<TicketMessage> findByTicketIdOrderByCreatedAt(@Param("ticketId") Long ticketId);
}

