package com.example.E_Commerce.service.impl;

import com.example.E_Commerce.dto.product.ProductRequest;
import com.example.E_Commerce.dto.product.ProductResponse;
import com.example.E_Commerce.exception.CategoryNotFoundException;
import com.example.E_Commerce.exception.ProductNotFoundException;
import com.example.E_Commerce.model.Category;
import com.example.E_Commerce.model.Product;
import com.example.E_Commerce.model.Role;
import com.example.E_Commerce.model.User;
import com.example.E_Commerce.repository.CategoryRepository;
import com.example.E_Commerce.repository.ProductRepository;
import com.example.E_Commerce.security.CustomUserDetails;
import com.example.E_Commerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request, CustomUserDetails currentUser) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .category(category)
                .seller(currentUser.getUser())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(savedProduct -> mapToResponse(savedProduct))
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product not found with the given id" + id));
        return mapToResponse(product);
    }

    @Override
    public void deleteProductById(Long id, CustomUserDetails currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product not found with the given id" + id));

        checkOwnership(product, currentUser);

        productRepository.delete(product);
    }

    @Override
    public ProductResponse UpdateProduct(Long id, ProductRequest request, CustomUserDetails currentUser) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found with id: " + id));

        checkOwnership(product, currentUser);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found with id: " + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);

    }

    private void checkOwnership(Product product, CustomUserDetails currentUser) {
        User user = currentUser.getUser();
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isOwner = product.getSeller().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to modify this product");
        }
    }

    private ProductResponse mapToResponse(Product savedProduct) {
        ProductResponse response = new ProductResponse();
        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setPrice(savedProduct.getPrice());
        response.setStock(savedProduct.getStock());
        response.setImageUrl(savedProduct.getImageUrl());
        response.setCreatedAt(savedProduct.getCreatedAt());
        response.setUpdatedAt(savedProduct.getUpdatedAt());
        response.setCategoryId(savedProduct.getCategory().getId());
        response.setCategoryName(savedProduct.getCategory().getName());
        response.setSellerId(savedProduct.getSeller().getId());
        return response;
    }
}
