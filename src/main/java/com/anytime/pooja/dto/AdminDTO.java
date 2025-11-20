package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.CouponType;
import com.anytime.pooja.model.enums.KYCStatus;
import com.anytime.pooja.model.enums.SettingDataType;
import com.anytime.pooja.model.enums.TicketPriority;
import com.anytime.pooja.model.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class AdminDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStatsResponse {
        private Long totalUsers;
        private Long totalPandits;
        private Long totalBookings;
        private Long totalOrders;
        private Double totalRevenue;
        private Double bookingRevenue;
        private Double productRevenue;
        private Double commissionEarned;
        private Long pendingKYCs;
        private Long pendingPayouts;
        private Long openTickets;
        private RevenueTrend revenueTrend;
        private UserGrowth userGrowth;
        private List<TopPandit> topPandits;
        private List<BestSeller> bestSellers;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueTrend {
        private List<String> labels; // dates
        private List<Double> bookingRevenue;
        private List<Double> productRevenue;
        private List<Double> totalRevenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGrowth {
        private List<String> labels; // dates
        private List<Long> newUsers;
        private List<Long> newPandits;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopPandit {
        private Long id;
        private String name;
        private Double rating;
        private Integer totalBookings;
        private Double totalEarnings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestSeller {
        private Long id;
        private String name;
        private Integer totalSales;
        private Double revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KYCApprovalRequest {
        @NotNull(message = "Status is required")
        private KYCStatus status;

        @Size(max = 500, message = "Rejection reason must not exceed 500 characters")
        private String rejectionReason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KYCResponse {
        private Long id;
        private Long panditId;
        private String panditName;
        private String documentType;
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
    public static class PayoutProcessRequest {
        @NotNull(message = "Payout IDs are required")
        @NotEmpty(message = "At least one payout ID is required")
        private List<Long> payoutIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayoutResponse {
        private Long id;
        private Long panditId;
        private String panditName;
        private Double amount;
        private Double commissionAmount;
        private Double netAmount;
        private String payoutStatus;
        private LocalDateTime earnedAt;
        private LocalDateTime payoutDate;
        private String payoutReference;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketReplyRequest {
        @NotBlank(message = "Reply message is required")
        @Size(max = 2000, message = "Reply must not exceed 2000 characters")
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendNotificationRequest {
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        private String title;

        @NotBlank(message = "Message is required")
        @Size(max = 1000, message = "Message must not exceed 1000 characters")
        private String message;

        private List<Long> userIds; // null = all users
        private String userType; // ALL, USER, PANDIT
        private String data; // JSON
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueReportRequest {
        private LocalDate fromDate;
        private LocalDate toDate;
        private String type; // BOOKING, PRODUCT, ALL
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueReportResponse {
        private LocalDate fromDate;
        private LocalDate toDate;
        private Double bookingRevenue;
        private Double productRevenue;
        private Double totalRevenue;
        private Double commissionEarned;
        private Double payoutsProcessed;
        private Integer totalBookings;
        private Integer totalOrders;
        private Integer newUsers;
        private Integer newPandits;
        private List<DailyRevenue> dailyBreakdown;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenue {
        private LocalDate date;
        private Double bookingRevenue;
        private Double productRevenue;
        private Double totalRevenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponCreateRequest {
        @NotBlank(message = "Coupon code is required")
        @Size(max = 50, message = "Coupon code must not exceed 50 characters")
        private String code;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        @NotNull(message = "Coupon type is required")
        private CouponType type;

        @NotNull(message = "Discount value is required")
        @DecimalMin(value = "0.0", message = "Discount value must be non-negative")
        private Double discountValue;

        @DecimalMin(value = "0.0", message = "Min order value must be non-negative")
        private Double minOrderValue = 0.0;

        private Double maxDiscountAmount;

        @Min(value = 1, message = "Usage limit must be at least 1")
        private Integer usageLimit;

        @Min(value = 1, message = "Usage per user must be at least 1")
        private Integer usagePerUser = 1;

        private Set<Long> applicableCategories;
        private Set<Long> applicableProducts;

        @NotNull(message = "User type is required")
        private UserType userType = UserType.ALL;

        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "Expiry date is required")
        private LocalDate expiryDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppSettingUpdateRequest {
        @NotBlank(message = "Value is required")
        private String value;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppSettingResponse {
        private String key;
        private String value;
        private String description;
        private SettingDataType dataType;
        private Boolean isPublic;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserManagementResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String role;
        private Boolean isActive;
        private Boolean isEmailVerified;
        private Boolean isPhoneVerified;
        private LocalDateTime lastLogin;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PanditManagementResponse {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String phone;
        private Double rating;
        private Integer totalBookings;
        private Boolean isVerified;
        private Boolean isAvailable;
        private LocalDateTime verificationDate;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TicketManagementResponse {
        private Long id;
        private String ticketNumber;
        private Long userId;
        private String userName;
        private String category;
        private TicketPriority priority;
        private String subject;
        private String status;
        private Long assignedTo;
        private String assignedToName;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;
    }
}

