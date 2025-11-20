package com.anytime.pooja.controller;

import com.anytime.pooja.dto.ChatDTO;
import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthService authService;

    // User Endpoints
    @GetMapping("/conversations")
    public ResponseEntity<CommonDTO.ApiResponse<ChatDTO.ConversationListResponse>> getUserConversations() {
        Long userId = authService.getCurrentUserId();
        ChatDTO.ConversationListResponse response = chatService.getUserConversations(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Conversations retrieved successfully"));
    }

    @PostMapping("/conversations/start")
    public ResponseEntity<CommonDTO.ApiResponse<ChatDTO.ConversationResponse>> startConversation(
            @Valid @RequestBody ChatDTO.StartConversationRequest request) {
        Long userId = authService.getCurrentUserId();
        ChatDTO.ConversationResponse response = chatService.startConversation(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Conversation started successfully"));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<CommonDTO.PaginatedResponse<ChatDTO.MessageResponse>> getMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        Long userId = authService.getCurrentUserId();
        List<ChatDTO.MessageResponse> messages = chatService.getMessages(userId, id, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(messages, page, size));
    }

    @PostMapping("/conversations/{id}/message")
    public ResponseEntity<CommonDTO.ApiResponse<ChatDTO.MessageResponse>> sendMessage(
            @PathVariable Long id, @Valid @RequestBody ChatDTO.SendMessageRequest request) {
        Long userId = authService.getCurrentUserId();
        request.setConversationId(id);
        ChatDTO.MessageResponse response = chatService.sendMessage(userId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Message sent successfully"));
    }

    @PutMapping("/conversations/{id}/read")
    public ResponseEntity<CommonDTO.SuccessResponse> markAsRead(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        chatService.markAsRead(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Messages marked as read successfully"));
    }

    // Pandit Endpoints
    @GetMapping("/pandit/conversations")
    public ResponseEntity<CommonDTO.ApiResponse<ChatDTO.ConversationListResponse>> getPanditConversations() {
        Long panditId = authService.getCurrentUserId();
        ChatDTO.ConversationListResponse response = chatService.getPanditConversations(panditId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Conversations retrieved successfully"));
    }

    @GetMapping("/pandit/conversations/{id}/messages")
    public ResponseEntity<CommonDTO.PaginatedResponse<ChatDTO.MessageResponse>> getPanditMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {
        Long panditId = authService.getCurrentUserId();
        List<ChatDTO.MessageResponse> messages = chatService.getPanditMessages(panditId, id, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(messages, page, size));
    }

    @PostMapping("/pandit/conversations/{id}/message")
    public ResponseEntity<CommonDTO.ApiResponse<ChatDTO.MessageResponse>> sendPanditMessage(
            @PathVariable Long id, @Valid @RequestBody ChatDTO.SendMessageRequest request) {
        Long panditId = authService.getCurrentUserId();
        request.setConversationId(id);
        ChatDTO.MessageResponse response = chatService.sendPanditMessage(panditId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Message sent successfully"));
    }

    @PutMapping("/pandit/conversations/{id}/read")
    public ResponseEntity<CommonDTO.SuccessResponse> markAsReadByPandit(@PathVariable Long id) {
        Long panditId = authService.getCurrentUserId();
        chatService.markAsReadByPandit(panditId, id);
        return ResponseEntity.ok(buildSuccessResponse("Messages marked as read successfully"));
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

