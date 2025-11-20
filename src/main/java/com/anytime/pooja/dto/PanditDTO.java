package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.AccountType;
import com.anytime.pooja.model.enums.DocumentType;
import com.anytime.pooja.model.enums.KYCStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public class PanditDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResponse {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String phone;
        private String bio;
        private Integer experienceYears;
        private Set<String> languages;
        private Set<String> serviceAreas;
        private Double rating;
        private Integer totalBookings;
        private Boolean isVerified;
        private Boolean isAvailable;
        private String profileImageUrl;
        private LocalDateTime verificationDate;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileUpdateRequest {
        @Size(max = 1000, message = "Bio must not exceed 1000 characters")
        private String bio;

        @Min(value = 0, message = "Experience years must be non-negative")
        @Max(value = 50, message = "Experience years must not exceed 50")
        private Integer experienceYears;

        private Set<String> languages;
        private Set<String> serviceAreas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KYCSubmitRequest {
        @NotNull(message = "Document type is required")
        private DocumentType documentType;

        @NotBlank(message = "Document number is required")
        @Size(max = 50, message = "Document number must not exceed 50 characters")
        private String documentNumber;

        @NotBlank(message = "Front image is required")
        private String frontImageUrl;

        private String backImageUrl;
        private String selfieImageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KYCResponse {
        private Long id;
        private DocumentType documentType;
        private String documentNumber; // masked
        private String frontImageUrl;
        private String backImageUrl;
        private String selfieImageUrl;
        private KYCStatus status;
        private String rejectionReason;
        private LocalDateTime submittedAt;
        private LocalDateTime verifiedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceRequest {
        @NotBlank(message = "Service name is required")
        @Size(max = 200, message = "Service name must not exceed 200 characters")
        private String serviceName;

        private Long categoryId;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be non-negative")
        private Double price;

        @NotNull(message = "Duration is required")
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        private Integer durationMinutes;

        private Boolean materialsIncluded = false;
        private Boolean homeVisit = true;

        @Min(value = 0, message = "Max distance must be non-negative")
        private Integer maxDistanceKm;

        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceResponse {
        private Long id;
        private String serviceName;
        private Long categoryId;
        private String categoryName;
        private String description;
        private Double price;
        private Integer durationMinutes;
        private Boolean materialsIncluded;
        private Boolean homeVisit;
        private Integer maxDistanceKm;
        private Boolean isActive;
        private String imageUrl;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityRequest {
        @NotNull(message = "Date is required")
        private LocalDate date;

        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        private LocalTime endTime;

        @Min(value = 15, message = "Slot duration must be at least 15 minutes")
        private Integer slotDuration = 60;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityResponse {
        private Long id;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private Boolean isBooked;
        private Integer slotDuration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkAvailabilityRequest {
        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        private LocalDate endDate;

        @NotNull(message = "Start time is required")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        private LocalTime endTime;

        private Set<LocalDate> excludeDates; // holidays
        private Integer slotDuration = 60;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankDetailsRequest {
        @NotBlank(message = "Account holder name is required")
        @Size(max = 100, message = "Account holder name must not exceed 100 characters")
        private String accountHolderName;

        @NotBlank(message = "Account number is required")
        @Size(max = 50, message = "Account number must not exceed 50 characters")
        private String accountNumber;

        @NotBlank(message = "IFSC code is required")
        @Size(max = 15, message = "IFSC code must not exceed 15 characters")
        private String ifscCode;

        @NotBlank(message = "Bank name is required")
        @Size(max = 100, message = "Bank name must not exceed 100 characters")
        private String bankName;

        @Size(max = 100, message = "Branch name must not exceed 100 characters")
        private String branchName;

        @NotNull(message = "Account type is required")
        private AccountType accountType;

        @Size(max = 100, message = "UPI ID must not exceed 100 characters")
        private String upiId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankDetailsResponse {
        private Long id;
        private String accountHolderName;
        private String accountNumber; // masked
        private String ifscCode;
        private String bankName;
        private String branchName;
        private AccountType accountType;
        private String upiId;
        private Boolean isVerified;
        private LocalDateTime addedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EarningsSummaryResponse {
        private Double totalEarnings;
        private Double pendingPayouts;
        private Double processedPayouts;
        private Integer totalBookings;
        private Double averageEarningPerBooking;
        private LocalDateTime lastPayoutDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EarningsDetailResponse {
        private Long id;
        private String bookingNumber;
        private Double amount;
        private Double commissionPercentage;
        private Double commissionAmount;
        private Double netAmount;
        private String payoutStatus;
        private LocalDateTime earnedAt;
        private LocalDateTime payoutDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private Double latitude;
        private Double longitude;
        private Double radius = 25.0; // km
        private Long categoryId;
        private LocalDate date;
        private Double minRating;
        private Double maxPrice;
        private String sortBy = "rating"; // rating, price, distance
        private Integer page = 0;
        private Integer size = 10;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResponse {
        private Long id;
        private String name;
        private String profileImageUrl;
        private String bio;
        private Integer experienceYears;
        private Double rating;
        private Integer totalBookings;
        private Set<String> languages;
        private Set<String> serviceAreas;
        private Double distance; // km
        private Double minPrice;
        private Boolean isVerified;
        private Boolean isAvailable;
    }
}

