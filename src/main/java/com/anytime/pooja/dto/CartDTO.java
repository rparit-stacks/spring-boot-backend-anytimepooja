package com.anytime.pooja.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CartDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToCartRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        private Long variantId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCartItemRequest {
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartResponse {
        private Long id;
        private Long userId;
        private Double subtotal;
        private Double discountAmount;
        private Double taxAmount;
        private Double totalAmount;
        private String couponCode;
        private List<CartItemResponse> items;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Long variantId;
        private String variantName;
        private String imageUrl;
        private Integer quantity;
        private Double price;
        private Double total;
        private Boolean inStock;
        private Integer availableStock;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyCouponRequest {
        @NotBlank(message = "Coupon code is required")
        @Size(max = 50, message = "Coupon code must not exceed 50 characters")
        private String couponCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponValidationResponse {
        private Boolean isValid;
        private String couponCode;
        private String description;
        private Double discountAmount;
        private String message;
    }
}

