package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.TicketCategory;
import com.anytime.pooja.model.enums.TicketPriority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class SupportDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTicketRequest {
        private Long bookingId;

        @NotNull(message = "Category is required")
        private TicketCategory category;

        @NotNull(message = "Priority is required")
        private TicketPriority priority = TicketPriority.MEDIUM;

        @NotBlank(message = "Subject is required")
        @Size(max = 200, message = "Subject must not exceed 200 characters")
        private String subject;

        @NotBlank(message = "Message is required")
        @Size(max = 2000, message = "Message must not exceed 2000 characters")
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketResponse {
        private Long id;
        private String ticketNumber;
        private Long userId;
        private String userName;
        private Long bookingId;
        private String bookingNumber;
        private TicketCategory category;
        private TicketPriority priority;
        private String subject;
        private String message;
        private String status;
        private Long assignedTo;
        private String assignedToName;
        private String adminReply;
        private LocalDateTime resolvedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTicketMessageRequest {
        @NotBlank(message = "Message is required")
        @Size(max = 2000, message = "Message must not exceed 2000 characters")
        private String message;

        private String attachmentUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketMessageResponse {
        private Long id;
        private Long ticketId;
        private Long senderId;
        private String senderName;
        private String senderImageUrl;
        private String message;
        private String attachmentUrl;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTicketRequest {
        private TicketPriority priority;
        private String status;
        private Long assignedTo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketReplyRequest {
        @NotBlank(message = "Reply message is required")
        @Size(max = 2000, message = "Reply must not exceed 2000 characters")
        private String message;
    }
}

