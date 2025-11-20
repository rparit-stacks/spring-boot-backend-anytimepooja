package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.CartDTO;
import com.anytime.pooja.model.*;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public CartDTO.CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> createEmptyCart(userId));
        
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartDTO.CartResponse addToCart(Long userId, CartDTO.AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }
        
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> createEmptyCart(userId));
        
        // Check if item already exists
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
            .orElse(null);
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice());
            cartItemRepository.save(cartItem);
        }
        
        recalculateCart(cart);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartDTO.CartResponse updateCartItem(Long userId, Long cartItemId, CartDTO.UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        Cart cart = cartItem.getCart();
        if (!cart.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }
        
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        recalculateCart(cart);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        Cart cart = cartItem.getCart();
        if (!cart.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
        
        cartItemRepository.delete(cartItem);
        recalculateCart(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElse(null);
        
        if (cart != null) {
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
            cartItemRepository.deleteAll(items);
            cart.setSubtotal(0.0);
            cart.setTaxAmount(0.0);
            cart.setDiscountAmount(0.0);
            cart.setTotalAmount(0.0);
            cart.setCouponCode(null);
            cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public CartDTO.CouponValidationResponse applyCoupon(Long userId, CartDTO.ApplyCouponRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
            .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
        
        // Validate coupon
        if (!coupon.getIsActive()) {
            throw new RuntimeException("Coupon is not active");
        }
        
        java.time.LocalDate today = java.time.LocalDate.now();
        if (coupon.getStartDate().isAfter(today) || coupon.getExpiryDate().isBefore(today)) {
            throw new RuntimeException("Coupon is not valid");
        }
        
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Coupon usage limit exceeded");
        }
        
        if (coupon.getMinOrderValue() != null && cart.getSubtotal() < coupon.getMinOrderValue()) {
            throw new RuntimeException("Minimum order amount not met");
        }
        
        // Apply coupon
        cart.setCouponCode(coupon.getCode());
        double discount = calculateDiscount(cart.getSubtotal(), coupon);
        cart.setDiscountAmount(discount);
        recalculateCart(cart);
        
        CartDTO.CouponValidationResponse response = new CartDTO.CouponValidationResponse();
        response.setIsValid(true);
        response.setDiscountAmount(discount);
        response.setMessage("Coupon applied successfully");
        return response;
    }

    @Override
    @Transactional
    public void removeCoupon(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        cart.setCouponCode(null);
        cart.setDiscountAmount(0.0);
        recalculateCart(cart);
    }

    // Helper methods
    private Cart createEmptyCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setSubtotal(0.0);
        cart.setTaxAmount(0.0);
        cart.setDiscountAmount(0.0);
        cart.setTotalAmount(0.0);
        return cartRepository.save(cart);
    }

    private void recalculateCart(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        
        // Calculate subtotal from items (price * quantity)
        double subtotal = items.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        
        cart.setSubtotal(subtotal);
        
        // Calculate tax (assuming 18% GST)
        double tax = subtotal * 0.18;
        cart.setTaxAmount(tax);
        
        // Recalculate total with discount (no shipping charges in Cart entity)
        double total = subtotal + tax - cart.getDiscountAmount();
        cart.setTotalAmount(total);
        
        cartRepository.save(cart);
    }

    private double calculateDiscount(double amount, Coupon coupon) {
        if (coupon.getType() == com.anytime.pooja.model.enums.CouponType.PERCENTAGE) {
            double discount = (amount * coupon.getDiscountValue()) / 100;
            // Apply max discount if set
            if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                discount = coupon.getMaxDiscountAmount();
            }
            return discount;
        } else {
            return Math.min(coupon.getDiscountValue(), amount);
        }
    }

    private CartDTO.CartResponse mapToCartResponse(Cart cart) {
        CartDTO.CartResponse response = new CartDTO.CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setSubtotal(cart.getSubtotal());
        response.setTaxAmount(cart.getTaxAmount());
        response.setDiscountAmount(cart.getDiscountAmount());
        response.setTotalAmount(cart.getTotalAmount());
        response.setCouponCode(cart.getCouponCode());
        response.setUpdatedAt(cart.getUpdatedAt());
        
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        response.setItems(items.stream()
            .map(this::mapToCartItemResponse)
            .collect(Collectors.toList()));
        
        return response;
    }

    private CartDTO.CartItemResponse mapToCartItemResponse(CartItem item) {
        CartDTO.CartItemResponse response = new CartDTO.CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setProductName(item.getProduct().getName());
        response.setSku(item.getProduct().getSku());
        response.setVariantId(item.getVariant() != null ? item.getVariant().getId() : null);
        response.setVariantName(item.getVariant() != null ? item.getVariant().getVariantName() : null);
        // Get primary image from ProductImage repository if needed
        response.setImageUrl(null); // Will be set from ProductImage repository
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setTotal(item.getPrice() * item.getQuantity());
        response.setInStock(item.getProduct().getStockQuantity() > 0);
        response.setAvailableStock(item.getProduct().getStockQuantity());
        return response;
    }
}

