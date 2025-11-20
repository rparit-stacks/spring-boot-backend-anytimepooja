package com.anytime.pooja.service;

import com.anytime.pooja.dto.WishlistDTO;

public interface WishlistService {
    WishlistDTO.WishlistResponse getWishlist(Long userId);
    void addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    void moveToCart(Long userId, Long productId);
    boolean isInWishlist(Long userId, Long productId);
}

