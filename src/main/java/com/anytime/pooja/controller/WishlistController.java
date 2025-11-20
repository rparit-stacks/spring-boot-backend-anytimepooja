package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.WishlistDTO;
import com.anytime.pooja.service.AuthService;
import com.anytime.pooja.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<WishlistDTO.WishlistResponse>> getWishlist() {
        Long userId = authService.getCurrentUserId();
        WishlistDTO.WishlistResponse response = wishlistService.getWishlist(userId);
        return ResponseEntity.ok(buildSuccessResponse(response, "Wishlist retrieved successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<CommonDTO.SuccessResponse> addToWishlist(@RequestParam Long productId) {
        Long userId = authService.getCurrentUserId();
        wishlistService.addToWishlist(userId, productId);
        return ResponseEntity.ok(buildSuccessResponse("Product added to wishlist successfully"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonDTO.SuccessResponse> removeFromWishlist(@PathVariable Long productId) {
        Long userId = authService.getCurrentUserId();
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(buildSuccessResponse("Product removed from wishlist successfully"));
    }

    @PostMapping("/{productId}/move-to-cart")
    public ResponseEntity<CommonDTO.SuccessResponse> moveToCart(@PathVariable Long productId) {
        Long userId = authService.getCurrentUserId();
        wishlistService.moveToCart(userId, productId);
        return ResponseEntity.ok(buildSuccessResponse("Product moved to cart successfully"));
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<CommonDTO.ApiResponse<Boolean>> isInWishlist(@PathVariable Long productId) {
        Long userId = authService.getCurrentUserId();
        boolean isInWishlist = wishlistService.isInWishlist(userId, productId);
        return ResponseEntity.ok(buildSuccessResponse(isInWishlist, "Wishlist status checked successfully"));
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

