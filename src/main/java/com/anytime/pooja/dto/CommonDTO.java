package com.anytime.pooja.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CommonDTO {

    // ==================== API RESPONSES ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private Boolean success;
        private String message;
        private T data;
        private Integer statusCode;
        private LocalDateTime timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginatedResponse<T> {
        private List<T> content;
        private Integer page;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Boolean hasNext;
        private Boolean hasPrevious;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private Boolean success = false;
        private String message;
        private String error;
        private Integer statusCode;
        private String timestamp;
        private String path;
        private List<FieldError> fieldErrors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessResponse {
        private Boolean success = true;
        private String message;
        private Integer statusCode = 200;
        private LocalDateTime timestamp;
    }

    // ==================== PAGINATION ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageRequest {
        @Min(value = 0, message = "Page must be non-negative")
        private Integer page = 0;

        @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size must not exceed 100")
        private Integer size = 10;

        private String sortBy;
        private String sortDirection = "ASC"; // ASC or DESC
    }

    // ==================== FILTERS ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRangeFilter {
        private LocalDate fromDate;
        private LocalDate toDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRangeFilter {
        private Double minPrice;
        private Double maxPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilter {
        private String query;
        private String searchBy; // name, email, phone, etc.
        private Boolean exactMatch = false;
    }

    // ==================== STATUS UPDATES ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private String status;
        private String reason;
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkStatusUpdateRequest {
        private List<Long> ids;
        private String status;
        private String reason;
    }

    // ==================== FILE UPLOAD ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileUploadResponse {
        private String fileName;
        private String fileUrl;
        private String filePath;
        private Long fileSize;
        private String contentType;
        private String fileType; // IMAGE, DOCUMENT, etc.
        private LocalDateTime uploadedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MultipleFileUploadResponse {
        private List<FileUploadResponse> files;
        private Integer successCount;
        private Integer failureCount;
        private List<String> errors;
    }

    // ==================== IDENTIFIERS ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdRequest {
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdsRequest {
        private List<Long> ids;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdResponse {
        private Long id;
        private String message;
    }

    // ==================== STATISTICS ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountResponse {
        private Long count;
        private String entityType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsResponse {
        private Map<String, Object> statistics;
        private LocalDateTime generatedAt;
    }

    // ==================== SELECT/DROPDOWN OPTIONS ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        private Long id;
        private String label;
        private String value;
        private Boolean isActive;
        private Object metadata; // Additional data
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionsResponse {
        private List<Option> options;
        private Integer totalCount;
    }

    // ==================== VALIDATION ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResponse {
        private Boolean isValid;
        private String message;
        private List<FieldError> errors;
    }

    // ==================== EXPORT ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportRequest {
        private String format; // PDF, EXCEL, CSV
        private DateRangeFilter dateRange;
        private Map<String, Object> filters;
        private List<String> columns; // Which columns to include
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportResponse {
        private String fileName;
        private String fileUrl;
        private String filePath;
        private Long fileSize;
        private String format;
        private LocalDateTime generatedAt;
        private Integer recordCount;
    }

    // ==================== NOTIFICATION ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationCountResponse {
        private Integer totalUnread;
        private Integer unreadByType; // Map<String, Integer> as JSON
        private LocalDateTime lastChecked;
    }

    // ==================== LOCATION ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationRequest {
        private Double latitude;
        private Double longitude;
        private Double radius; // in km
        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationResponse {
        private Double latitude;
        private Double longitude;
        private String address;
        private String city;
        private String state;
        private String country;
        private String zipCode;
    }

    // ==================== RATING SUMMARY ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingSummary {
        private Double averageRating;
        private Integer totalRatings;
        private Integer fiveStar;
        private Integer fourStar;
        private Integer threeStar;
        private Integer twoStar;
        private Integer oneStar;
        private Double percentage; // Percentage of positive ratings (4+ stars)
    }

    // ==================== METADATA ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataResponse {
        private Map<String, Object> metadata;
        private LocalDateTime lastUpdated;
    }

    // ==================== BULK OPERATIONS ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkOperationResponse {
        private Integer totalItems;
        private Integer successCount;
        private Integer failureCount;
        private List<String> errors;
        private List<Long> successIds;
        private List<Long> failureIds;
    }

    // ==================== DELETE RESPONSE ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResponse {
        private Boolean deleted;
        private Long id;
        private String message;
        private LocalDateTime deletedAt;
    }

    // ==================== ACTIVITY LOG ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityLogResponse {
        private Long id;
        private String action;
        private String entityType;
        private Long entityId;
        private String description;
        private Long performedBy;
        private String performedByName;
        private LocalDateTime performedAt;
    }
}

