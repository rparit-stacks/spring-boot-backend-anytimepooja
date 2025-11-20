package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.SupportDTO;
import com.anytime.pooja.model.enums.TicketStatus;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.SupportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/support")
public class SupportController {

    @Autowired
    private SupportService supportService;

    @Autowired
    private AuthService authService;

    // User/Pandit Endpoints
    @PostMapping("/tickets")
    public ResponseEntity<CommonDTO.ApiResponse<SupportDTO.TicketResponse>> createTicket(
            @Valid @RequestBody SupportDTO.CreateTicketRequest request) {
        Long userId = authService.getCurrentUserId();
        SupportDTO.TicketResponse response = supportService.createTicket(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Ticket created successfully"));
    }

    @GetMapping("/tickets")
    public ResponseEntity<CommonDTO.ApiResponse<List<SupportDTO.TicketResponse>>> getUserTickets(
            @RequestParam(required = false) TicketStatus status) {
        Long userId = authService.getCurrentUserId();
        List<SupportDTO.TicketResponse> response = supportService.getUserTickets(userId, status);
        return ResponseEntity.ok(buildSuccessResponse(response, "Tickets retrieved successfully"));
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<SupportDTO.TicketResponse>> getTicket(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        SupportDTO.TicketResponse response = supportService.getTicket(userId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Ticket retrieved successfully"));
    }

    @PostMapping("/tickets/{id}/message")
    public ResponseEntity<CommonDTO.ApiResponse<SupportDTO.TicketMessageResponse>> addMessage(
            @PathVariable Long id, @Valid @RequestBody SupportDTO.AddTicketMessageRequest request) {
        Long userId = authService.getCurrentUserId();
        SupportDTO.TicketMessageResponse response = supportService.addMessage(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Message added successfully"));
    }

    @GetMapping("/tickets/{id}/messages")
    public ResponseEntity<CommonDTO.ApiResponse<List<SupportDTO.TicketMessageResponse>>> getTicketMessages(@PathVariable Long id) {
        List<SupportDTO.TicketMessageResponse> response = supportService.getTicketMessages(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Messages retrieved successfully"));
    }

    @PutMapping("/tickets/{id}/close")
    public ResponseEntity<CommonDTO.SuccessResponse> closeTicket(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        supportService.closeTicket(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Ticket closed successfully"));
    }

    // Admin Endpoints
    @GetMapping("/admin/tickets")
    public ResponseEntity<CommonDTO.PaginatedResponse<SupportDTO.TicketResponse>> getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<SupportDTO.TicketResponse> tickets = supportService.getAllTickets(status, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(tickets, page, size));
    }

    @GetMapping("/admin/tickets/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<SupportDTO.TicketResponse>> getTicketById(@PathVariable Long id) {
        SupportDTO.TicketResponse response = supportService.getTicketById(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Ticket retrieved successfully"));
    }

    @PutMapping("/admin/tickets/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<SupportDTO.TicketResponse>> updateTicket(
            @PathVariable Long id, @Valid @RequestBody SupportDTO.UpdateTicketRequest request) {
        SupportDTO.TicketResponse response = supportService.updateTicket(id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Ticket updated successfully"));
    }

    @PostMapping("/admin/tickets/{id}/reply")
    public ResponseEntity<CommonDTO.ApiResponse<SupportDTO.TicketResponse>> replyToTicket(
            @PathVariable Long id, @Valid @RequestBody SupportDTO.TicketReplyRequest request) {
        Long adminId = authService.getCurrentUserId();
        SupportDTO.TicketResponse response = supportService.replyToTicket(adminId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Reply sent successfully"));
    }

    @PutMapping("/admin/tickets/{id}/assign")
    public ResponseEntity<CommonDTO.SuccessResponse> assignTicket(
            @PathVariable Long id, @RequestParam Long adminId) {
        supportService.assignTicket(id, adminId);
        return ResponseEntity.ok(buildSuccessResponse("Ticket assigned successfully"));
    }

    // Helper methods
    private <T> CommonDTO.ApiResponse<T> buildSuccessResponse(T data, String message) {
        CommonDTO.ApiResponse<T> response = new CommonDTO.ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private CommonDTO.SuccessResponse buildSuccessResponse(String message) {
        CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private <T> CommonDTO.PaginatedResponse<T> buildPaginatedResponse(List<T> content, Integer page, Integer size) {
        CommonDTO.PaginatedResponse<T> response = new CommonDTO.PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((long) content.size());
        response.setTotalPages((int) Math.ceil((double) content.size() / size));
        response.setHasNext(page < response.getTotalPages() - 1);
        response.setHasPrevious(page > 0);
        response.setIsFirst(page == 0);
        response.setIsLast(page >= response.getTotalPages() - 1);
        return response;
    }
}

