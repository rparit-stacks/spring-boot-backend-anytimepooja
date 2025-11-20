package com.anytime.pooja.controller;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.ProductDTO;
import com.anytime.pooja.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Public Endpoints
    @GetMapping
    public ResponseEntity<CommonDTO.PaginatedResponse<ProductDTO.ProductSummaryResponse>> searchProducts(
            @ModelAttribute ProductDTO.SearchRequest request) {
        List<ProductDTO.ProductSummaryResponse> products = productService.searchProducts(request);
        return ResponseEntity.ok(buildPaginatedResponse(products, request.getPage(), request.getSize()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<ProductDTO.ProductResponse>> getProduct(@PathVariable Long id) {
        ProductDTO.ProductResponse response = productService.getProduct(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Product retrieved successfully"));
    }

    @GetMapping("/featured")
    public ResponseEntity<CommonDTO.ApiResponse<List<ProductDTO.ProductSummaryResponse>>> getFeaturedProducts() {
        List<ProductDTO.ProductSummaryResponse> response = productService.getFeaturedProducts();
        return ResponseEntity.ok(buildSuccessResponse(response, "Featured products retrieved successfully"));
    }

    @GetMapping("/trending")
    public ResponseEntity<CommonDTO.ApiResponse<List<ProductDTO.ProductSummaryResponse>>> getTrendingProducts() {
        List<ProductDTO.ProductSummaryResponse> response = productService.getTrendingProducts();
        return ResponseEntity.ok(buildSuccessResponse(response, "Trending products retrieved successfully"));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<CommonDTO.ApiResponse<List<ProductDTO.ProductSummaryResponse>>> getRelatedProducts(@PathVariable Long id) {
        List<ProductDTO.ProductSummaryResponse> response = productService.getRelatedProducts(id);
        return ResponseEntity.ok(buildSuccessResponse(response, "Related products retrieved successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonDTO.ApiResponse<List<ProductDTO.CategoryResponse>>> getAllCategories() {
        List<ProductDTO.CategoryResponse> response = productService.getAllCategories();
        return ResponseEntity.ok(buildSuccessResponse(response, "Categories retrieved successfully"));
    }

    @GetMapping("/categories/{id}/products")
    public ResponseEntity<CommonDTO.PaginatedResponse<ProductDTO.ProductSummaryResponse>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<ProductDTO.ProductSummaryResponse> products = productService.getProductsByCategory(id, page, size);
        return ResponseEntity.ok(buildPaginatedResponse(products, page, size));
    }

    // Admin Endpoints
    @GetMapping("/admin")
    public ResponseEntity<CommonDTO.PaginatedResponse<ProductDTO.ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        CommonDTO.PaginatedResponse<ProductDTO.ProductResponse> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<CommonDTO.ApiResponse<ProductDTO.ProductResponse>> createProduct(
            @Valid @RequestBody ProductDTO.CreateProductRequest request) {
        ProductDTO.ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse(response, "Product created successfully"));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<ProductDTO.ProductResponse>> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductDTO.UpdateProductRequest request) {
        ProductDTO.ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(buildSuccessResponse(response, "Product updated successfully"));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<CommonDTO.SuccessResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(buildSuccessResponse("Product deleted successfully"));
    }

    @PutMapping("/admin/{id}/stock")
    public ResponseEntity<CommonDTO.SuccessResponse> updateStock(
            @PathVariable Long id, @Valid @RequestBody ProductDTO.UpdateStockRequest request) {
        productService.updateStock(id, request);
        return ResponseEntity.ok(buildSuccessResponse("Stock updated successfully"));
    }

    @GetMapping("/admin/low-stock")
    public ResponseEntity<CommonDTO.ApiResponse<List<ProductDTO.ProductResponse>>> getLowStockProducts() {
        List<ProductDTO.ProductResponse> response = productService.getLowStockProducts();
        return ResponseEntity.ok(buildSuccessResponse(response, "Low stock products retrieved successfully"));
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

