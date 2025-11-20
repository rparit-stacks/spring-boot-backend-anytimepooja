package com.anytime.pooja.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ProductDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotBlank(message = "SKU is required")
        @Size(max = 100, message = "SKU must not exceed 100 characters")
        private String sku;

        @NotNull(message = "Category ID is required")
        private Long categoryId;

        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Product name must not exceed 200 characters")
        private String name;

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        private String description;

        @Size(max = 500, message = "Short description must not exceed 500 characters")
        private String shortDescription;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be non-negative")
        private Double price;

        @NotNull(message = "MRP is required")
        @DecimalMin(value = "0.0", message = "MRP must be non-negative")
        private Double mrp;

        @Min(value = 0, message = "Stock quantity must be non-negative")
        private Integer stockQuantity = 0;

        @Min(value = 0, message = "Low stock threshold must be non-negative")
        private Integer lowStockThreshold = 10;

        @Min(value = 0, message = "Weight must be non-negative")
        private Integer weight; // grams

        private String dimensions; // JSON
        private Boolean isFeatured = false;
        private Set<String> tags;
        private List<String> imageUrls;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String sku;
        private Long categoryId;
        private String categoryName;
        private String name;
        private String description;
        private String shortDescription;
        private Double price;
        private Double mrp;
        private Double discountPercentage;
        private Integer stockQuantity;
        private Integer lowStockThreshold;
        private Integer weight;
        private String dimensions;
        private Boolean isFeatured;
        private Boolean isActive;
        private Double rating;
        private Integer totalSales;
        private Integer viewsCount;
        private Set<String> tags;
        private List<String> images;
        private List<ProductVariantResponse> variants;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantResponse {
        private Long id;
        private String variantName;
        private String sku;
        private Double price;
        private Integer stockQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProductRequest {
        @Size(max = 200, message = "Product name must not exceed 200 characters")
        private String name;

        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        private String description;

        @Size(max = 500, message = "Short description must not exceed 500 characters")
        private String shortDescription;

        @DecimalMin(value = "0.0", message = "Price must be non-negative")
        private Double price;

        @DecimalMin(value = "0.0", message = "MRP must be non-negative")
        private Double mrp;

        @Min(value = 0, message = "Stock quantity must be non-negative")
        private Integer stockQuantity;

        @Min(value = 0, message = "Low stock threshold must be non-negative")
        private Integer lowStockThreshold;

        private Boolean isFeatured;
        private Boolean isActive;
        private Set<String> tags;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateStockRequest {
        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity must be non-negative")
        private Integer stockQuantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchRequest {
        private String query;
        private Long categoryId;
        private Double minPrice;
        private Double maxPrice;
        private Double minRating;
        private Boolean inStock;
        private String sortBy = "popular"; // popular, price_low, price_high, rating, newest
        private Integer page = 0;
        private Integer size = 20;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSummaryResponse {
        private Long id;
        private String sku;
        private String name;
        private String shortDescription;
        private Double price;
        private Double mrp;
        private Double discountPercentage;
        private Double rating;
        private Integer totalSales;
        private String primaryImage;
        private Boolean isFeatured;
        private Boolean inStock;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private String imageUrl;
        private String iconUrl;
        private Long parentId;
        private String parentName;
        private Boolean isActive;
        private Integer displayOrder;
        private Integer productCount;
    }
}

