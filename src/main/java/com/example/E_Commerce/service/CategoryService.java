package com.example.E_Commerce.service;

import com.example.E_Commerce.dto.category.CategoryRequest;
import com.example.E_Commerce.dto.category.CategoryResponse;

import java.util.List;


public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoriesById(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
