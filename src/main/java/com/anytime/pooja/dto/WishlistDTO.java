package com.anytime.pooja.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class WishlistDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Double price;
        private Double mrp;
        private Double discountPercentage;
        private String primaryImage;
        private Boolean inStock;
        private LocalDateTime addedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistResponse {
        private List<WishlistItemResponse> items;
        private Integer totalCount;
    }
}

