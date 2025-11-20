package com.anytime.pooja.model;

import com.anytime.pooja.model.enums.OrderStatus;
import com.anytime.pooja.model.enums.PaymentMethod;
import com.anytime.pooja.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_number", columnList = "order_number"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Double subtotal;
    
    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount = 0.0;
    
    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount = 0.0;
    
    @Column(name = "shipping_charges", nullable = false)
    private Double shippingCharges = 0.0;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Column(name = "coupon_code", length = 50)
    private String couponCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PLACED;
    
    @Column(name = "payment_id", length = 100)
    private String paymentId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "shipping_address", columnDefinition = "JSON")
    private String shippingAddress; // JSON or could be FK to Address
    
    @Column(name = "billing_address", columnDefinition = "JSON")
    private String billingAddress; // JSON
    
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;
    
    @Column(name = "courier_partner", length = 100)
    private String courierPartner;
    
    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    @Column(name = "return_initiated_at")
    private LocalDateTime returnInitiatedAt;
    
    @Column(name = "return_reason", length = 500)
    private String returnReason;
    
    @Column(name = "refund_amount", nullable = false)
    private Double refundAmount = 0.0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

