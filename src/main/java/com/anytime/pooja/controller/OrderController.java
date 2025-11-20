package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.OrderDTO;
import com.anytime.pooja.model.enums.OrderStatus;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    // User Endpoints
    @GetMapping("/checkout/summary")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.CheckoutSummaryResponse>> getCheckoutSummary() {
        Long userId = authService.getCurrentUserId();
        OrderDTO.CheckoutSummaryResponse response = orderService.getCheckoutSummary(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Checkout summary retrieved successfully"));
    }

    @PostMapping("/place")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> placeOrder(
            @Valid @RequestBody OrderDTO.CheckoutRequest request) {
        Long userId = authService.getCurrentUserId();
        OrderDTO.OrderResponse response = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Order placed successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> getOrder(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        OrderDTO.OrderResponse response = orderService.getOrder(userId, id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Order retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<CommonDTO.PaginatedResponse<OrderDTO.OrderSummaryResponse>> getUserOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = authService.getCurrentUserId();
        List<OrderDTO.OrderSummaryResponse> orders = orderService.getUserOrders(userId, status, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(orders, page, size));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> cancelOrder(
            @PathVariable Long id, @Valid @RequestBody OrderDTO.CancelOrderRequest request) {
        Long userId = authService.getCurrentUserId();
        OrderDTO.OrderResponse response = orderService.cancelOrder(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Order cancelled successfully"));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> returnOrder(
            @PathVariable Long id, @Valid @RequestBody OrderDTO.ReturnOrderRequest request) {
        Long userId = authService.getCurrentUserId();
        OrderDTO.OrderResponse response = orderService.returnOrder(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Return request submitted successfully"));
    }

    @GetMapping("/{id}/track")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.TrackingInfo>> getOrderTracking(@PathVariable Long id) {
        OrderDTO.TrackingInfo response = orderService.getOrderTracking(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Tracking information retrieved successfully"));
    }

    // Admin Endpoints
    @GetMapping("/admin")
    public ResponseEntity<CommonDTO.PaginatedResponse<OrderDTO.OrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<OrderDTO.OrderResponse> orders = orderService.getAllOrders(status, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(orders, page, size));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderDTO.OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Order retrieved successfully"));
    }

    @PutMapping("/admin/{id}/status")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> updateOrderStatus(
            @PathVariable Long id, @Valid @RequestBody OrderDTO.UpdateOrderStatusRequest request) {
        OrderDTO.OrderResponse response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Order status updated successfully"));
    }

    @PutMapping("/admin/{id}/cancel")
    public ResponseEntity<CommonDTO.ApiResponse<OrderDTO.OrderResponse>> cancelOrderByAdmin(
            @PathVariable Long id, @RequestParam String reason) {
        OrderDTO.OrderResponse response = orderService.cancelOrderByAdmin(id, reason);
        return ResponseEntity.ok(buildSuccessResponse(response, "Order cancelled successfully"));
    }

    @PostMapping("/admin/{id}/tracking")
    public ResponseEntity<CommonDTO.SuccessResponse> assignTracking(
            @PathVariable Long id,
            @RequestParam String trackingNumber,
            @RequestParam String courierPartner) {
        orderService.assignTracking(id, trackingNumber, courierPartner);
        return ResponseEntity.ok(buildSuccessResponse("Tracking assigned successfully"));
    }

    // Helper methods
    private <T> CommonDTO.ApiResponse<T> buildSuccessResponse(T data, String message) {
        CommonDTO.ApiResponse<T> response = new CommonDTO.ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private CommonDTO.SuccessResponse buildSuccessResponse(String message) {
        CommonDTO.SuccessResponse response = new CommonDTO.SuccessResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setStatusCode(HttpStatus.OK.value());
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    private <T> CommonDTO.PaginatedResponse<T> buildPaginatedResponse(List<T> content, Integer page, Integer size) {
        CommonDTO.PaginatedResponse<T> response = new CommonDTO.PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((long) content.size());
        response.setTotalPages((int) Math.ceil((double) content.size() / size));
        response.setHasNext(page < response.getTotalPages() - 1);
        response.setHasPrevious(page > 0);
        response.setIsFirst(page == 0);
        response.setIsLast(page >= response.getTotalPages() - 1);
        return response;
    }
}

