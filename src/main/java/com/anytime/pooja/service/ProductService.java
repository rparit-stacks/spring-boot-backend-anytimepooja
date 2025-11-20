package com.anytime.pooja.service;

import com.anytime.pooja.dto.ProductDTO;
import com.anytime.pooja.dto.CommonDTO;

import java.util.List;

public interface ProductService {
    // Public Product Operations
    List<ProductDTO.ProductSummaryResponse> searchProducts(ProductDTO.SearchRequest request);
    ProductDTO.ProductResponse getProduct(Long productId);
    List<ProductDTO.ProductSummaryResponse> getFeaturedProducts();
    List<ProductDTO.ProductSummaryResponse> getTrendingProducts();
    List<ProductDTO.ProductSummaryResponse> getRelatedProducts(Long productId);
    List<ProductDTO.CategoryResponse> getAllCategories();
    List<ProductDTO.ProductSummaryResponse> getProductsByCategory(Long categoryId, Integer page, Integer size);
    
    // Admin Product Operations
    CommonDTO.PaginatedResponse<ProductDTO.ProductResponse> getAllProducts(Integer page, Integer size);
    ProductDTO.ProductResponse createProduct(ProductDTO.CreateProductRequest request);
    ProductDTO.ProductResponse updateProduct(Long productId, ProductDTO.UpdateProductRequest request);
    void deleteProduct(Long productId);
    void updateStock(Long productId, ProductDTO.UpdateStockRequest request);
    void bulkUpdateStock(List<Long> productIds, Integer stockQuantity);
    List<ProductDTO.ProductResponse> getLowStockProducts();
}

