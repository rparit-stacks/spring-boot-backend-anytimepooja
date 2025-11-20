package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.PaymentMethod;
import com.anytime.pooja.model.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PaymentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePaymentOrderRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.0", message = "Amount must be at least 1")
        private Double amount;

        @NotBlank(message = "Currency is required")
        private String currency = "INR";

        private String description;
        private Long bookingId;
        private Long orderId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentOrderResponse {
        private String orderId;
        private Double amount;
        private String currency;
        private String keyId; // Razorpay key
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyPaymentRequest {
        @NotBlank(message = "Razorpay order ID is required")
        private String razorpayOrderId;

        @NotBlank(message = "Razorpay payment ID is required")
        private String razorpayPaymentId;

        @NotBlank(message = "Razorpay signature is required")
        private String razorpaySignature;

        private Long bookingId;
        private Long orderId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long id;
        private String paymentId;
        private String razorpayOrderId; // Razorpay order ID
        private Double amount;
        private String currency;
        private PaymentMethod paymentMethod;
        private PaymentStatus status;
        private String description;
        private Long bookingId;
        private Long orderId; // Internal order ID
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundRequest {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Refund amount must be at least 0.01")
        private Double amount;

        @Size(max = 500, message = "Reason must not exceed 500 characters")
        private String reason;

        private Boolean isFullRefund = true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundResponse {
        private String refundId;
        private Double amount;
        private String status;
        private String reason;
        private LocalDateTime processedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentStatusResponse {
        private String paymentId;
        private PaymentStatus status;
        private String message;
        private LocalDateTime lastUpdated;
    }
}

