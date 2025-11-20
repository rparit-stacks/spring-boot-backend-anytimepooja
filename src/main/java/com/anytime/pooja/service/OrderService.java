package com.anytime.pooja.service;

import com.anytime.pooja.dto.OrderDTO;
import com.anytime.pooja.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    // User Order Operations
    OrderDTO.CheckoutSummaryResponse getCheckoutSummary(Long userId);
    OrderDTO.OrderResponse placeOrder(Long userId, OrderDTO.CheckoutRequest request);
    OrderDTO.OrderResponse getOrder(Long userId, Long orderId);
    List<OrderDTO.OrderSummaryResponse> getUserOrders(Long userId, OrderStatus status, Integer page, Integer size);
    OrderDTO.OrderResponse cancelOrder(Long userId, Long orderId, OrderDTO.CancelOrderRequest request);
    OrderDTO.OrderResponse returnOrder(Long userId, Long orderId, OrderDTO.ReturnOrderRequest request);
    OrderDTO.TrackingInfo getOrderTracking(Long orderId);
    
    // Admin Order Operations
    List<OrderDTO.OrderResponse> getAllOrders(OrderStatus status, Integer page, Integer size);
    OrderDTO.OrderResponse getOrderById(Long orderId);
    OrderDTO.OrderResponse updateOrderStatus(Long orderId, OrderDTO.UpdateOrderStatusRequest request);
    OrderDTO.OrderResponse cancelOrderByAdmin(Long orderId, String reason);
    void assignTracking(Long orderId, String trackingNumber, String courierPartner);
}

