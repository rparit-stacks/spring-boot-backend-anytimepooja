package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.PanditDTO;
import com.anytime.pooja.dto.ProductDTO;
import com.anytime.pooja.service.PanditService;
import com.anytime.pooja.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("")
public class PublicController {

    @Autowired
    private PanditService panditService;

    @Autowired
    private ProductService productService;

    // Public Pandit Search
    @GetMapping("/pandits/search")
    public ResponseEntity<CommonDTO.PaginatedResponse<PanditDTO.SearchResponse>> searchPandits(
            @ModelAttribute PanditDTO.SearchRequest request) {
        List<PanditDTO.SearchResponse> pandits = panditService.searchPandits(request);
        return ResponseEntity.ok(buildPaginatedResponse(pandits, request.getPage(), request.getSize()));
    }

    @GetMapping("/pandits/nearby")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.SearchResponse>>> getNearbyPandits(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "25.0") Double radius) {
        List<PanditDTO.SearchResponse> response = panditService.getNearbyPandits(latitude, longitude, radius);
        return ResponseEntity.ok(buildSuccessResponse(response, "Nearby pandits retrieved successfully"));
    }

    @GetMapping("/pandits/featured")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.SearchResponse>>> getFeaturedPandits() {
        List<PanditDTO.SearchResponse> response = panditService.getFeaturedPandits();
        return ResponseEntity.ok(buildSuccessResponse(response, "Featured pandits retrieved successfully"));
    }

    @GetMapping("/pandits/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<PanditDTO.ProfileResponse>> getPanditProfile(@PathVariable Long id) {
        PanditDTO.ProfileResponse response = panditService.getProfile(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Pandit profile retrieved successfully"));
    }

    @GetMapping("/pandits/{id}/services")
    public ResponseEntity<CommonDTO.ApiResponse<List<PanditDTO.ServiceResponse>>> getPanditServices(@PathVariable Long id) {
        List<PanditDTO.ServiceResponse> response = panditService.getServices(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Pandit services retrieved successfully"));
    }

    @GetMapping("/pandits/{id}/reviews")
    public ResponseEntity<CommonDTO.PaginatedResponse<com.anytime.pooja.dto.ReviewDTO.ReviewResponse>> getPanditReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // Will be implemented in ReviewService
        return ResponseEntity.ok(new CommonDTO.PaginatedResponse<>());
    }

    // Public Product Endpoints (already in ProductController but can be accessed publicly)
    @GetMapping("/categories")
    public ResponseEntity<CommonDTO.ApiResponse<List<ProductDTO.CategoryResponse>>> getAllCategories() {
        List<ProductDTO.CategoryResponse> response = productService.getAllCategories();
        return ResponseEntity.ok(buildSuccessResponse(response, "Categories retrieved successfully"));
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

