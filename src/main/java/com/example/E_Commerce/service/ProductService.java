package com.example.E_Commerce.service;

import com.example.E_Commerce.dto.product.ProductRequest;
import com.example.E_Commerce.dto.product.ProductResponse;
import com.example.E_Commerce.security.CustomUserDetails;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(@Valid ProductRequest request, CustomUserDetails currentUser);

    List<ProductResponse> getAllProducts();

    ProductResponse UpdateProduct(Long id, ProductRequest request, CustomUserDetails currentUser);

    ProductResponse getProductById(Long id);

    void deleteProductById(Long id, CustomUserDetails currentUser);
}
