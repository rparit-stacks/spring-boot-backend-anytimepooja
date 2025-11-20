package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CartDTO;
import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<CartDTO.CartResponse>> getCart() {
        Long userId = authService.getCurrentUserId();
        CartDTO.CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Cart retrieved successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<CommonDTO.ApiResponse<CartDTO.CartResponse>> addToCart(
            @Valid @RequestBody CartDTO.AddToCartRequest request) {
        Long userId = authService.getCurrentUserId();
        CartDTO.CartResponse response = cartService.addToCart(userId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Item added to cart successfully"));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<CartDTO.CartResponse>> updateCartItem(
            @PathVariable Long id, @Valid @RequestBody CartDTO.UpdateCartItemRequest request) {
        Long userId = authService.getCurrentUserId();
        CartDTO.CartResponse response = cartService.updateCartItem(userId, id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Cart item updated successfully"));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> removeCartItem(@PathVariable Long id) {
        Long userId = authService.getCurrentUserId();
        cartService.removeCartItem(userId, id);
        return ResponseEntity.ok(buildSuccessResponse("Item removed from cart successfully"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CommonDTO.SuccessResponse> clearCart() {
        Long userId = authService.getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(buildSuccessResponse("Cart cleared successfully"));
    }

    @PostMapping("/apply-coupon")
    public ResponseEntity<CommonDTO.ApiResponse<CartDTO.CouponValidationResponse>> applyCoupon(
            @Valid @RequestBody CartDTO.ApplyCouponRequest request) {
        Long userId = authService.getCurrentUserId();
        CartDTO.CouponValidationResponse response = cartService.applyCoupon(userId, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Coupon applied successfully"));
    }

    @DeleteMapping("/remove-coupon")
    public ResponseEntity<CommonDTO.SuccessResponse> removeCoupon() {
        Long userId = authService.getCurrentUserId();
        cartService.removeCoupon(userId);
        return ResponseEntity.ok(buildSuccessResponse("Coupon removed successfully"));
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
}

