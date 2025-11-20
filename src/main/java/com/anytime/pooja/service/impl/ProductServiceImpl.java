package com.anytime.pooja.service.impl;

import com.anytime.pooja.dto.CommonDTO;
import com.anytime.pooja.dto.ProductDTO;
import com.anytime.pooja.model.Category;
import com.anytime.pooja.model.Product;
import com.anytime.pooja.model.ProductImage;
import com.anytime.pooja.model.ProductVariant;
import com.anytime.pooja.repository.*;
import com.anytime.pooja.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Override
    public List<ProductDTO.ProductSummaryResponse> searchProducts(ProductDTO.SearchRequest request) {
        List<Product> products;
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            products = productRepository.searchProducts(request.getQuery());
        } else if (request.getCategoryId() != null) {
            products = productRepository.findByCategoryId(request.getCategoryId());
        } else {
            products = productRepository.findByIsActiveTrue();
        }
        
        // Apply filters
        if (request.getMinPrice() != null) {
            products = products.stream()
                .filter(p -> p.getPrice() >= request.getMinPrice())
                .collect(Collectors.toList());
        }
        if (request.getMaxPrice() != null) {
            products = products.stream()
                .filter(p -> p.getPrice() <= request.getMaxPrice())
                .collect(Collectors.toList());
        }
        if (request.getMinRating() != null) {
            products = products.stream()
                .filter(p -> p.getRating() >= request.getMinRating())
                .collect(Collectors.toList());
        }
        if (request.getInStock() != null && request.getInStock()) {
            products = products.stream()
                .filter(p -> p.getStockQuantity() > 0)
                .collect(Collectors.toList());
        }
        
        // Sort
        if ("price_low".equals(request.getSortBy())) {
            products.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
        } else if ("price_high".equals(request.getSortBy())) {
            products.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        } else if ("rating".equals(request.getSortBy())) {
            products.sort((a, b) -> b.getRating().compareTo(a.getRating()));
        }
        
        return products.stream()
            .skip(request.getPage() * request.getSize())
            .limit(request.getSize())
            .map(this::mapToProductSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public ProductDTO.ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToProductResponse(product);
    }

    @Override
    public List<ProductDTO.ProductSummaryResponse> getFeaturedProducts() {
        List<Product> products = productRepository.findByIsActiveTrueAndIsFeaturedTrue();
        return products.stream()
            .map(this::mapToProductSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO.ProductSummaryResponse> getTrendingProducts() {
        List<Product> products = productRepository.findTopRatedProducts();
        return products.stream()
            .limit(10)
            .map(this::mapToProductSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO.ProductSummaryResponse> getRelatedProducts(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        List<Product> related = productRepository.findByCategoryId(product.getCategory().getId());
        return related.stream()
            .filter(p -> !p.getId().equals(productId))
            .limit(5)
            .map(this::mapToProductSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO.CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        return categories.stream()
            .map(this::mapToCategoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO.ProductSummaryResponse> getProductsByCategory(Long categoryId, Integer page, Integer size) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToProductSummaryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CommonDTO.PaginatedResponse<ProductDTO.ProductResponse> getAllProducts(Integer page, Integer size) {
        List<Product> products = productRepository.findAll();
        List<ProductDTO.ProductResponse> content = products.stream()
            .skip(page * size)
            .limit(size)
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
        
        CommonDTO.PaginatedResponse<ProductDTO.ProductResponse> response = new CommonDTO.PaginatedResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((long) products.size());
        response.setTotalPages((int) Math.ceil((double) products.size() / size));
        response.setHasNext(page < response.getTotalPages() - 1);
        response.setHasPrevious(page > 0);
        response.setIsFirst(page == 0);
        response.setIsLast(page >= response.getTotalPages() - 1);
        return response;
    }

    @Override
    @Transactional
    public ProductDTO.ProductResponse createProduct(ProductDTO.CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Product product = new Product();
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setPrice(request.getPrice());
        product.setMrp(request.getMrp());
        product.setDiscountPercentage(calculateDiscountPercentage(request.getMrp(), request.getPrice()));
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setIsFeatured(request.getIsFeatured());
        product.setIsActive(true);
        product.setTags(request.getTags());
        
        product = productRepository.save(product);
        
        // Save images
        if (request.getImageUrls() != null) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(request.getImageUrls().get(i));
                image.setDisplayOrder(i);
                image.setIsPrimary(i == 0);
                productImageRepository.save(image);
            }
        }
        
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductDTO.ProductResponse updateProduct(Long productId, ProductDTO.UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
            product.setDiscountPercentage(calculateDiscountPercentage(product.getMrp(), request.getPrice()));
        }
        if (request.getMrp() != null) {
            product.setMrp(request.getMrp());
            product.setDiscountPercentage(calculateDiscountPercentage(request.getMrp(), product.getPrice()));
        }
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getLowStockThreshold() != null) product.setLowStockThreshold(request.getLowStockThreshold());
        if (request.getIsFeatured() != null) product.setIsFeatured(request.getIsFeatured());
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());
        if (request.getTags() != null) product.setTags(request.getTags());
        
        product = productRepository.save(product);
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void updateStock(Long productId, ProductDTO.UpdateStockRequest request) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(request.getStockQuantity());
        productRepository.save(product);
    }

    @Override
    public void bulkUpdateStock(List<Long> productIds, Integer stockQuantity) {
        // Implementation will be done later
    }

    @Override
    public List<ProductDTO.ProductResponse> getLowStockProducts() {
        List<Product> products = productRepository.findLowStockProducts();
        return products.stream()
            .map(this::mapToProductResponse)
            .collect(Collectors.toList());
    }

    // Helper methods
    private Double calculateDiscountPercentage(Double mrp, Double price) {
        if (mrp == null || mrp == 0) return 0.0;
        return ((mrp - price) / mrp) * 100;
    }

    private ProductDTO.ProductResponse mapToProductResponse(Product product) {
        ProductDTO.ProductResponse response = new ProductDTO.ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setShortDescription(product.getShortDescription());
        response.setPrice(product.getPrice());
        response.setMrp(product.getMrp());
        response.setDiscountPercentage(product.getDiscountPercentage());
        response.setStockQuantity(product.getStockQuantity());
        response.setLowStockThreshold(product.getLowStockThreshold());
        response.setWeight(product.getWeight());
        response.setDimensions(product.getDimensions());
        response.setIsFeatured(product.getIsFeatured());
        response.setIsActive(product.getIsActive());
        response.setRating(product.getRating());
        response.setTotalSales(product.getTotalSales());
        response.setViewsCount(product.getViewsCount());
        response.setTags(product.getTags());
        
        // Get images
        List<ProductImage> images = productImageRepository.findByProductIdOrdered(product.getId());
        response.setImages(images.stream()
            .map(ProductImage::getImageUrl)
            .collect(Collectors.toList()));
        
        // Get variants
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        response.setVariants(variants.stream()
            .map(this::mapToVariantResponse)
            .collect(Collectors.toList()));
        
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private ProductDTO.ProductSummaryResponse mapToProductSummaryResponse(Product product) {
        ProductDTO.ProductSummaryResponse response = new ProductDTO.ProductSummaryResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setShortDescription(product.getShortDescription());
        response.setPrice(product.getPrice());
        response.setMrp(product.getMrp());
        response.setDiscountPercentage(product.getDiscountPercentage());
        response.setRating(product.getRating());
        response.setTotalSales(product.getTotalSales());
        
        ProductImage primaryImage = productImageRepository.findPrimaryImageByProductId(product.getId()).orElse(null);
        response.setPrimaryImage(primaryImage != null ? primaryImage.getImageUrl() : null);
        
        response.setIsFeatured(product.getIsFeatured());
        response.setInStock(product.getStockQuantity() > 0);
        return response;
    }

    private ProductDTO.CategoryResponse mapToCategoryResponse(Category category) {
        ProductDTO.CategoryResponse response = new ProductDTO.CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        response.setIconUrl(category.getIconUrl());
        response.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        response.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        response.setIsActive(category.getIsActive());
        response.setDisplayOrder(category.getDisplayOrder());
        
        Long productCount = productRepository.countByCategoryId(category.getId());
        response.setProductCount(productCount != null ? productCount.intValue() : 0);
        return response;
    }

    private ProductDTO.ProductVariantResponse mapToVariantResponse(ProductVariant variant) {
        ProductDTO.ProductVariantResponse response = new ProductDTO.ProductVariantResponse();
        response.setId(variant.getId());
        response.setVariantName(variant.getVariantName());
        response.setSku(variant.getSku());
        response.setPrice(variant.getPrice());
        response.setStockQuantity(variant.getStockQuantity());
        return response;
    }
}

