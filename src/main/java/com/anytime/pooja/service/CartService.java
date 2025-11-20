package com.anytime.pooja.service;

import com.anytime.pooja.dto.CartDTO;

public interface CartService {
    CartDTO.CartResponse getCart(Long userId);
    CartDTO.CartResponse addToCart(Long userId, CartDTO.AddToCartRequest request);
    CartDTO.CartResponse updateCartItem(Long userId, Long cartItemId, CartDTO.UpdateCartItemRequest request);
    void removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
    CartDTO.CouponValidationResponse applyCoupon(Long userId, CartDTO.ApplyCouponRequest request);
    void removeCoupon(Long userId);
}

