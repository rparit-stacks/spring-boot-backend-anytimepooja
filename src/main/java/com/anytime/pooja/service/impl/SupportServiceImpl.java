package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.SupportDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.model.enums.TicketStatus;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupportServiceImpl implements SupportService {

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    @Transactional
    public SupportDTO.TicketResponse createTicket(Long userId, SupportDTO.CreateTicketRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setUser(user);
        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId()).orElse(null);
            ticket.setBooking(booking);
        }
        ticket.setCategory(request.getCategory());
        ticket.setPriority(request.getPriority());
        ticket.setSubject(request.getSubject());
        ticket.setMessage(request.getMessage());
        ticket.setStatus(TicketStatus.OPEN);
        
        ticket = supportTicketRepository.save(ticket);
        
        return mapToTicketResponse(ticket);
    }

    @Override
    public List<SupportDTO.TicketResponse> getUserTickets(Long userId, TicketStatus status) {
        List<SupportTicket> tickets = supportTicketRepository.findByUserId(userId);
        
        // Filter by status if provided
        if (status != null) {
            tickets = tickets.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
        }
        
        return tickets.stream()
            .map(this::mapToTicketResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SupportDTO.TicketResponse getTicket(Long userId, Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToTicketResponse(ticket);
    }

    @Override
    @Transactional
    public SupportDTO.TicketMessageResponse addMessage(Long userId, Long ticketId, SupportDTO.AddTicketMessageRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        TicketMessage message = new TicketMessage();
        message.setTicket(ticket);
        message.setSender(userRepository.findById(userId).orElse(null));
        message.setMessage(request.getMessage());
        message.setAttachmentUrl(request.getAttachmentUrl());
        
        message = ticketMessageRepository.save(message);
        
        return mapToTicketMessageResponse(message);
    }

    @Override
    public List<SupportDTO.TicketMessageResponse> getTicketMessages(Long ticketId) {
        List<TicketMessage> messages = ticketMessageRepository.findByTicketIdOrderByCreatedAt(ticketId);
        return messages.stream()
            .map(this::mapToTicketMessageResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void closeTicket(Long userId, Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setResolvedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);
    }

    @Override
    public List<SupportDTO.TicketResponse> getAllTickets(TicketStatus status, Integer page, Integer size) {
        List<SupportTicket> tickets = status != null
            ? supportTicketRepository.findByStatus(status)
            : supportTicketRepository.findAll();
        
        return tickets.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToTicketResponse)
            .collect(Collectors.toList());
    }

    @Override
    public SupportDTO.TicketResponse getTicketById(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return mapToTicketResponse(ticket);
    }

    @Override
    @Transactional
    public SupportDTO.TicketResponse updateTicket(Long ticketId, SupportDTO.UpdateTicketRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (request.getPriority() != null) ticket.setPriority(request.getPriority());
        if (request.getStatus() != null) ticket.setStatus(TicketStatus.valueOf(request.getStatus()));
        if (request.getAssignedTo() != null) {
            // ticket.setAssignedTo(userRepository.findById(request.getAssignedTo()).orElse(null));
        }
        
        ticket = supportTicketRepository.save(ticket);
        return mapToTicketResponse(ticket);
    }

    @Override
    @Transactional
    public SupportDTO.TicketResponse replyToTicket(Long adminId, Long ticketId, SupportDTO.TicketReplyRequest request) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        TicketMessage message = new TicketMessage();
        message.setTicket(ticket);
        message.setSender(userRepository.findById(adminId).orElse(null));
        message.setMessage(request.getMessage());
        ticketMessageRepository.save(message);
        
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket = supportTicketRepository.save(ticket);
        
        return mapToTicketResponse(ticket);
    }

    @Override
    @Transactional
    public void assignTicket(Long ticketId, Long adminId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        // ticket.setAssignedTo(userRepository.findById(adminId).orElse(null));
        supportTicketRepository.save(ticket);
    }

    // Helper methods
    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private SupportDTO.TicketResponse mapToTicketResponse(SupportTicket ticket) {
        SupportDTO.TicketResponse response = new SupportDTO.TicketResponse();
        response.setId(ticket.getId());
        response.setTicketNumber(ticket.getTicketNumber());
        response.setUserId(ticket.getUser().getId());
        response.setUserName(ticket.getUser().getName());
        response.setBookingId(ticket.getBooking() != null ? ticket.getBooking().getId() : null);
        response.setBookingNumber(ticket.getBooking() != null ? ticket.getBooking().getBookingNumber() : null);
        response.setCategory(ticket.getCategory());
        response.setPriority(ticket.getPriority());
        response.setSubject(ticket.getSubject());
        response.setMessage(ticket.getMessage());
        response.setStatus(ticket.getStatus().name());
        // response.setAssignedTo(ticket.getAssignedTo() != null ? ticket.getAssignedTo().getId() : null);
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }

    private SupportDTO.TicketMessageResponse mapToTicketMessageResponse(TicketMessage message) {
        SupportDTO.TicketMessageResponse response = new SupportDTO.TicketMessageResponse();
        response.setId(message.getId());
        response.setTicketId(message.getTicket().getId());
        response.setSenderId(message.getSender() != null ? message.getSender().getId() : null);
        response.setSenderName(message.getSender() != null ? message.getSender().getName() : null);
        response.setMessage(message.getMessage());
        response.setAttachmentUrl(message.getAttachmentUrl());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}

