package com.anytime.pooja.dto;

import com.anytime.pooja.model.enums.OrderStatus;
import com.anytime.pooja.model.enums.PaymentMethod;
import com.anytime.pooja.model.enums.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutRequest {
        @NotNull(message = "Address ID is required")
        private Long addressId;

        private String couponCode;

        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutSummaryResponse {
        private Double subtotal;
        private Double discountAmount;
        private Double taxAmount;
        private Double shippingCharges;
        private Double totalAmount;
        private String couponCode;
        private Double couponDiscount;
        private AddressInfo shippingAddress;
        private List<CartItemInfo> items;
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
        private String phone;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemInfo {
        private Long productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private Double price;
        private Double total;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private Long userId;
        private String userName;
        private Double subtotal;
        private Double discountAmount;
        private Double taxAmount;
        private Double shippingCharges;
        private Double totalAmount;
        private String couponCode;
        private OrderStatus status;
        private String paymentId;
        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus;
        private AddressInfo shippingAddress;
        private AddressInfo billingAddress;
        private String trackingNumber;
        private String courierPartner;
        private LocalDateTime estimatedDelivery;
        private LocalDateTime deliveredAt;
        private LocalDateTime cancelledAt;
        private String cancellationReason;
        private List<OrderItemResponse> items;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private Double price;
        private Double discount;
        private Double total;
        private String imageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOrderStatusRequest {
        @NotNull(message = "Status is required")
        private OrderStatus status;

        @Size(max = 500, message = "Note must not exceed 500 characters")
        private String note;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelOrderRequest {
        @NotBlank(message = "Cancellation reason is required")
        @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnOrderRequest {
        @NotBlank(message = "Return reason is required")
        @Size(max = 500, message = "Return reason must not exceed 500 characters")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingInfo {
        private String trackingNumber;
        private String courierPartner;
        private OrderStatus currentStatus;
        private LocalDateTime estimatedDelivery;
        private List<TrackingEvent> events;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingEvent {
        private String status;
        private String description;
        private LocalDateTime timestamp;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummaryResponse {
        private Long id;
        private String orderNumber;
        private Double totalAmount;
        private OrderStatus status;
        private PaymentStatus paymentStatus;
        private Integer itemCount;
        private String primaryImage;
        private LocalDateTime createdAt;
    }
}

