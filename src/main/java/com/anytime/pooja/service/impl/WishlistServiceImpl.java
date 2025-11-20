package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.WishlistDTO;
import com.anytime.pooja.model.Product;
import com.anytime.pooja.model.User;
import com.anytime.pooja.model.Wishlist;
import com.anytime.pooja.repository.ProductRepository;
import com.anytime.pooja.repository.UserRepository;
import com.anytime.pooja.repository.WishlistRepository;
import com.anytime.pooja.service.CartService;
import com.anytime.pooja.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Override
    public WishlistDTO.WishlistResponse getWishlist(Long userId) {
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);
        
        WishlistDTO.WishlistResponse response = new WishlistDTO.WishlistResponse();
        response.setItems(wishlistItems.stream()
            .map(this::mapToWishlistItemResponse)
            .collect(Collectors.toList()));
        response.setTotalCount(wishlistItems.size());
        return response;
    }

    @Override
    @Transactional
    public void addToWishlist(Long userId, Long productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if already in wishlist
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new RuntimeException("Product already in wishlist");
        }
        
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new RuntimeException("Product not found in wishlist"));
        
        wishlistRepository.delete(wishlist);
    }

    @Override
    @Transactional
    public void moveToCart(Long userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new RuntimeException("Product not found in wishlist"));
        
        // Add to cart
        com.anytime.pooja.dto.CartDTO.AddToCartRequest request = new com.anytime.pooja.dto.CartDTO.AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(1);
        cartService.addToCart(userId, request);
        
        // Remove from wishlist
        wishlistRepository.delete(wishlist);
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    // Helper methods
    private WishlistDTO.WishlistItemResponse mapToWishlistItemResponse(Wishlist wishlist) {
        WishlistDTO.WishlistItemResponse response = new WishlistDTO.WishlistItemResponse();
        response.setId(wishlist.getId());
        response.setProductId(wishlist.getProduct().getId());
        response.setProductName(wishlist.getProduct().getName());
        response.setSku(wishlist.getProduct().getSku());
        response.setPrice(wishlist.getProduct().getPrice());
        response.setMrp(wishlist.getProduct().getMrp());
        response.setDiscountPercentage(wishlist.getProduct().getDiscountPercentage());
        // Get product image from ProductImage repository if needed
        response.setPrimaryImage(null); // Will be set from ProductImage repository
        response.setInStock(wishlist.getProduct().getStockQuantity() > 0);
        response.setAddedAt(wishlist.getAddedAt());
        return response;
    }
}

