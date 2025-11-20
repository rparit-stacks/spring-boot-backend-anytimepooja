package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.OrderDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.model.enums.OrderStatus;
import com.anytime.pooja.model.enums.PaymentStatus;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.CartService;
import com.anytime.pooja.service.OrderService;
import com.anytime.pooja.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentService paymentService;

    @Override
    public OrderDTO.CheckoutSummaryResponse getCheckoutSummary(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        OrderDTO.CheckoutSummaryResponse response = new OrderDTO.CheckoutSummaryResponse();
        response.setSubtotal(cart.getSubtotal());
        response.setTaxAmount(cart.getTaxAmount());
        double shippingCharges = cart.getSubtotal() >= 500 ? 0.0 : 50.0;
        response.setShippingCharges(shippingCharges);
        response.setDiscountAmount(cart.getDiscountAmount());
        response.setCouponDiscount(cart.getDiscountAmount());
        response.setTotalAmount(cart.getTotalAmount() + shippingCharges);
        response.setCouponCode(cart.getCouponCode());
        // Map items
        response.setItems(items.stream().map(item -> {
            OrderDTO.CartItemInfo info = new OrderDTO.CartItemInfo();
            info.setProductId(item.getProduct().getId());
            info.setProductName(item.getProduct().getName());
            info.setSku(item.getProduct().getSku());
            info.setQuantity(item.getQuantity());
            info.setPrice(item.getPrice());
            info.setTotal(item.getPrice() * item.getQuantity());
            return info;
        }).collect(Collectors.toList()));
        return response;
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse placeOrder(Long userId, OrderDTO.CheckoutRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        Address address = addressRepository.findById(request.getAddressId())
            .orElseThrow(() -> new RuntimeException("Address not found"));
        
        // Convert address to JSON for shippingAddress
        String shippingAddressJson = convertAddressToJson(address);
        
        // Calculate shipping charges
        double shippingCharges = cart.getSubtotal() >= 500 ? 0.0 : 50.0;
        double totalAmount = cart.getSubtotal() + cart.getTaxAmount() + shippingCharges - cart.getDiscountAmount();
        
        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setShippingAddress(shippingAddressJson);
        order.setBillingAddress(shippingAddressJson); // Same as shipping for now
        order.setSubtotal(cart.getSubtotal());
        order.setTaxAmount(cart.getTaxAmount());
        order.setShippingCharges(shippingCharges);
        order.setDiscountAmount(cart.getDiscountAmount());
        order.setCouponCode(cart.getCouponCode());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());
        
        order = orderRepository.save(order);
        
        // Create order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setSku(cartItem.getProduct().getSku());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            double itemTotal = cartItem.getPrice() * cartItem.getQuantity();
            orderItem.setTotal(itemTotal);
            orderItemRepository.save(orderItem);
            
            // Update product stock
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setTotalSales(product.getTotalSales() + cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Clear cart
        cartService.clearCart(userId);
        
        // Update coupon usage if coupon applied
        if (cart.getCouponCode() != null) {
            Coupon coupon = couponRepository.findByCode(cart.getCouponCode())
                .orElse(null);
            if (coupon != null) {
                // Create coupon usage record
                CouponUsage couponUsage = new CouponUsage();
                couponUsage.setCoupon(coupon);
                couponUsage.setUser(user);
                couponUsage.setOrder(order);
                couponUsage.setDiscountAmount(cart.getDiscountAmount());
                // couponUsageRepository.save(couponUsage); // If repository exists
            }
        }
        
        return mapToOrderResponse(order);
    }

    @Override
    public OrderDTO.OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderDTO.OrderSummaryResponse> getUserOrders(Long userId, OrderStatus status, Integer page, Integer size) {
        List<Order> orders = status != null
            ? orderRepository.findByUserIdAndStatus(userId, status)
            : orderRepository.findByUserId(userId);
        
        return orders.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToOrderSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse cancelOrder(Long userId, Long orderId, OrderDTO.CancelOrderRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order in current status");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(request.getReason());
        order.setCancelledAt(LocalDateTime.now());
        order = orderRepository.save(order);
        
        // Restore product stock
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse returnOrder(Long userId, Long orderId, OrderDTO.ReturnOrderRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Only delivered orders can be returned");
        }
        
        order.setStatus(OrderStatus.RETURNED);
        order.setReturnReason(request.getReason());
        order.setReturnInitiatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        
        return mapToOrderResponse(order);
    }

    @Override
    public OrderDTO.TrackingInfo getOrderTracking(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        OrderDTO.TrackingInfo tracking = new OrderDTO.TrackingInfo();
        tracking.setTrackingNumber(order.getTrackingNumber());
        tracking.setCourierPartner(order.getCourierPartner());
        tracking.setCurrentStatus(order.getStatus());
        tracking.setEstimatedDelivery(order.getEstimatedDelivery());
        // Add tracking events if needed
        tracking.setEvents(java.util.Collections.emptyList());
        return tracking;
    }

    @Override
    public List<OrderDTO.OrderResponse> getAllOrders(OrderStatus status, Integer page, Integer size) {
        List<Order> orders = status != null
            ? orderRepository.findByStatus(status)
            : orderRepository.findAll();
        
        return orders.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToOrderResponse)
            .collect(Collectors.toList());
    }

    @Override
    public OrderDTO.OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse updateOrderStatus(Long orderId, OrderDTO.UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(request.getStatus());
        
        if (request.getStatus() == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        order = orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderDTO.OrderResponse cancelOrderByAdmin(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);
        order.setCancelledAt(LocalDateTime.now());
        order = orderRepository.save(order);
        
        // Restore product stock
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public void assignTracking(Long orderId, String trackingNumber, String courierPartner) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setTrackingNumber(trackingNumber);
        order.setCourierPartner(courierPartner);
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);
    }

    // Helper methods
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String convertAddressToJson(Address address) {
        // Simple JSON conversion - can use Jackson ObjectMapper for proper JSON
        return String.format("{\"street\":\"%s\",\"landmark\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"zipCode\":\"%s\"}",
            address.getStreet(), address.getLandmark(), address.getCity(), address.getState(), address.getZipCode());
    }

    private OrderDTO.AddressInfo parseAddressFromJson(String json) {
        // Simple parsing - should use Jackson ObjectMapper for proper parsing
        OrderDTO.AddressInfo addressInfo = new OrderDTO.AddressInfo();
        // For now, return empty - implement proper JSON parsing
        return addressInfo;
    }

    private OrderDTO.OrderResponse mapToOrderResponse(Order order) {
        OrderDTO.OrderResponse response = new OrderDTO.OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser().getId());
        response.setUserName(order.getUser().getName());
        response.setSubtotal(order.getSubtotal());
        response.setTaxAmount(order.getTaxAmount());
        response.setShippingCharges(order.getShippingCharges());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setCouponCode(order.getCouponCode());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setPaymentId(order.getPaymentId());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setCourierPartner(order.getCourierPartner());
        response.setEstimatedDelivery(order.getEstimatedDelivery());
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCancelledAt(order.getCancelledAt());
        response.setCancellationReason(order.getCancellationReason());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        // Parse address from JSON
        if (order.getShippingAddress() != null) {
            response.setShippingAddress(parseAddressFromJson(order.getShippingAddress()));
        }
        if (order.getBillingAddress() != null) {
            response.setBillingAddress(parseAddressFromJson(order.getBillingAddress()));
        }
        
        // Map items
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        response.setItems(items.stream()
            .map(this::mapToOrderItemResponse)
            .collect(Collectors.toList()));
        
        return response;
    }

    private OrderDTO.OrderSummaryResponse mapToOrderSummaryResponse(Order order) {
        OrderDTO.OrderSummaryResponse response = new OrderDTO.OrderSummaryResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        response.setItemCount(items.size());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    private OrderDTO.OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        OrderDTO.OrderItemResponse response = new OrderDTO.OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProductName());
        response.setSku(item.getSku());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setDiscount(item.getDiscount());
        response.setTotal(item.getTotal());
        response.setImageUrl(null); // Will be set from ProductImage repository
        return response;
    }
}

