package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.BookingStatus;
import com.anytime.pooja.model.enums.PaymentMethod;
import com.anytime.pooja.model.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckAvailabilityRequest {
        @NotNull(message = "Pandit ID is required")
        private Long panditId;

        @NotNull(message = "Service ID is required")
        private Long serviceId;

        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "Duration is required")
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        private Integer durationMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilitySlotResponse {
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean isAvailable;
        private String reason; // if not available
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBookingRequest {
        @NotNull(message = "Pandit ID is required")
        private Long panditId;

        @NotNull(message = "Service ID is required")
        private Long serviceId;

        @NotNull(message = "Booking date is required")
        private LocalDate bookingDate;

        @NotNull(message = "Booking time is required")
        private LocalTime bookingTime;

        @NotNull(message = "Address ID is required")
        private Long addressId;

        @Size(max = 1000, message = "Special instructions must not exceed 1000 characters")
        private String specialInstructions;

        private String couponCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingResponse {
        private Long id;
        private String bookingNumber;
        private Long userId;
        private String userName;
        private Long panditId;
        private String panditName;
        private Long serviceId;
        private String serviceName;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private LocalTime endTime;
        private BookingStatus status;
        private String cancellationReason;
        private String cancelledBy;
        private AddressInfo address;
        private String specialInstructions;
        private Double totalAmount;
        private Double discountAmount;
        private String couponCode;
        private Double finalAmount;
        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus;
        private LocalDateTime confirmedAt;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressInfo {
        private String street;
        private String landmark;
        private String city;
        private String state;
        private String zipCode;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotNull(message = "Status is required")
        private BookingStatus status;

        @Size(max = 500, message = "Reason must not exceed 500 characters")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelBookingRequest {
        @NotBlank(message = "Cancellation reason is required")
        @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RescheduleRequest {
        @NotNull(message = "New booking date is required")
        private LocalDate newBookingDate;

        @NotNull(message = "New booking time is required")
        private LocalTime newBookingTime;

        @Size(max = 200, message = "Reason must not exceed 200 characters")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineResponse {
        private Long id;
        private BookingStatus status;
        private String description;
        private Long createdBy;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingSummaryResponse {
        private Long id;
        private String bookingNumber;
        private String panditName;
        private String serviceName;
        private LocalDate bookingDate;
        private LocalTime bookingTime;
        private BookingStatus status;
        private Double finalAmount;
        private PaymentStatus paymentStatus;
        private LocalDateTime createdAt;
    }
}

