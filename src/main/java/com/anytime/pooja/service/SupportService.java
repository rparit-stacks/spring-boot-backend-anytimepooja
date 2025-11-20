package com.anytime.pooja.service;

import com.anytime.pooja.dto.SupportDTO;
import com.anytime.pooja.model.enums.TicketStatus;

import java.util.List;

public interface SupportService {
    // User/Pandit Operations
    SupportDTO.TicketResponse createTicket(Long userId, SupportDTO.CreateTicketRequest request);
    List<SupportDTO.TicketResponse> getUserTickets(Long userId, TicketStatus status);
    SupportDTO.TicketResponse getTicket(Long userId, Long ticketId);
    SupportDTO.TicketMessageResponse addMessage(Long userId, Long ticketId, SupportDTO.AddTicketMessageRequest request);
    List<SupportDTO.TicketMessageResponse> getTicketMessages(Long ticketId);
    void closeTicket(Long userId, Long ticketId);
    
    // Admin Operations
    List<SupportDTO.TicketResponse> getAllTickets(TicketStatus status, Integer page, Integer size);
    SupportDTO.TicketResponse getTicketById(Long ticketId);
    SupportDTO.TicketResponse updateTicket(Long ticketId, SupportDTO.UpdateTicketRequest request);
    SupportDTO.TicketResponse replyToTicket(Long adminId, Long ticketId, SupportDTO.TicketReplyRequest request);
    void assignTicket(Long ticketId, Long adminId);
}

